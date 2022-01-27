/*
The MIT License (MIT)

Copyright (c) 2022 Pierre Lindenbaum

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


*/
package com.github.lindenb.jvarkit.tools.setfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.beust.jcommander.Parameter;
import com.github.lindenb.jvarkit.bed.BedLineReader;
import com.github.lindenb.jvarkit.io.ArchiveFactory;
import com.github.lindenb.jvarkit.io.IOUtils;
import com.github.lindenb.jvarkit.iterator.AbstractCloseableIterator;
import com.github.lindenb.jvarkit.iterator.EqualIterator;
import com.github.lindenb.jvarkit.samtools.util.SimpleInterval;
import com.github.lindenb.jvarkit.setfile.SetFileReaderFactory;
import com.github.lindenb.jvarkit.setfile.SetFileRecord;
import com.github.lindenb.jvarkit.util.bio.DistanceParser;
import com.github.lindenb.jvarkit.util.bio.SequenceDictionaryUtils;
import com.github.lindenb.jvarkit.util.bio.bed.BedLine;
import com.github.lindenb.jvarkit.util.bio.fasta.ContigNameConverter;
import com.github.lindenb.jvarkit.util.jcommander.Launcher;
import com.github.lindenb.jvarkit.util.jcommander.NoSplitter;
import com.github.lindenb.jvarkit.util.jcommander.Program;
import com.github.lindenb.jvarkit.util.log.Logger;
import com.github.lindenb.jvarkit.util.samtools.ContigDictComparator;
import com.github.lindenb.jvarkit.variant.vcf.BufferedVCFReader;
import com.github.lindenb.jvarkit.variant.vcf.VCFReaderFactory;

import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.IntervalTreeMap;
import htsjdk.samtools.util.Locatable;
import htsjdk.samtools.util.StringUtil;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFHeader;

/**
BEGIN_DOC


END_DOC

**/
@Program(name="setfiletools",
description="Utilities for the setfile format",
creationDate="20210125",
modificationDate="20210127",
keywords={"setfile"}
)

public class SetFileTools extends Launcher {
	private static final Logger LOG = Logger.build(SetFileTools.class).make();
	@Parameter(names= {"-R","--reference"},description=INDEXED_FASTA_REFERENCE_DESCRIPTION,required=true)
	private Path faidxRef = null;
	@Parameter(names={"-o","--out"},description=OPT_OUPUT_FILE_OR_STDOUT+ ". For action=cluster, output is: "+ArchiveFactory.OPT_DESC)
	protected Path outputFile=null;
	@Parameter(names={"-t","--trim-chr"},description="Remove chr prefix in chromosome names on output.")
	protected boolean trim_chr_prefix = false;
	@Parameter(names={"-U","--remove-unused-interval"},description="Remove ")
	protected boolean remove_unused_interval = false;
	@Parameter(names={"--bed"},description="Restrict input to this bed file.")
	protected Path intersectBedPath = null;
	@Parameter(names={"--bed"},description="Restrict input to thoses vcf file(s). A file with the '.list' suffix is interpreted as a list of paths to the vcfs.")
	protected List<String> intersectVcfPath = new ArrayList<>();
	@Parameter(names={"--stringency"},description="Validation Stringency")
	protected ValidationStringency validationStringency = ValidationStringency.LENIENT;
	@Parameter(names={"-S","--size"},description="number of bases max per bin. (or specify --jobs). "+DistanceParser.OPT_DESCRIPTION,converter=DistanceParser.LongStringConverter.class,splitter=NoSplitter.class)
	private long long_length_per_bin=-1L;
	@Parameter(names={"-J","--jobs"},description="number of clusters. (or specify --size)")
	private int number_of_jobs=-1;
	@Parameter(names={"--min-variants-per-setfile"},description="when using vcf, only keep the setfile is there are at least 'x' overlapping variants.")
	private int min_variant_per_setfile = 1;
	
	/** iterator filtering on VCF */
	private class IntersectVcfIterator extends AbstractCloseableIterator<SetFileRecord> {
		final List<BufferedVCFReader> vcfReaders = new ArrayList<>();
		final List<UnaryOperator<String>> contigConverters = new ArrayList<>();
		final CloseableIterator<SetFileRecord> delegate;
		IntersectVcfIterator(final CloseableIterator<SetFileRecord> delegate,final List<Path> paths) {
			this.delegate = delegate;
			final VCFReaderFactory vrf = VCFReaderFactory.makeDefault();
			final UnaryOperator<VariantContext> simplifier = V->new VariantContextBuilder(V).
					noGenotypes().
					noID().
					unfiltered().
					attributes(Collections.emptyMap()).
					make();
			for(final Path path:paths) {
				@SuppressWarnings("resource")
				final BufferedVCFReader vr=new BufferedVCFReader(vrf.open(path,true),10_000).setSimplifier(simplifier);
				this.vcfReaders.add(vr);
				final VCFHeader h = vr.getHeader();
				this.contigConverters.add(ContigNameConverter.fromOneDictionary(SequenceDictionaryUtils.extractRequired(h)));
				}
			}
		
		@Override
		protected final SetFileRecord advance() {
			while(this.delegate.hasNext()) {
				final SetFileRecord rec = this.delegate.next();
				final List<Locatable> L = new ArrayList<>(rec.size());
				long n = 0L;
				for(int i=0;i< rec.size();i++) {
					final Locatable loc = rec.get(i);
					long n2 = 0L;
					for(int k=0;k< this.vcfReaders.size();++k) {
						final BufferedVCFReader vr = this.vcfReaders.get(k);
						final String ctg = this.contigConverters.get(k).apply(loc.getContig());
						if(StringUtil.isBlank(ctg)) continue;
						try(CloseableIterator<VariantContext> iter2 = vr.query(new SimpleInterval(ctg,loc.getStart(),loc.getEnd()))) {
							n2 += iter2.stream().filter(V->V.overlaps(loc)).count();
							}
						}
					if(n2==0L && remove_unused_interval) {
						continue;
						}
					L.add(loc);
					n+=n2;
					}
				if(L.isEmpty() || n< min_variant_per_setfile) continue;
				return SetFileRecord.create(rec.getName(), L);
				}
			return null;
			}

		
		@Override
		public void close() {
			delegate.close();
			for(BufferedVCFReader b:this.vcfReaders) try {b.close();}
			catch(final IOException err) {}
			}
		}
	
	
	private class IntersectBedIterator extends AbstractCloseableIterator<SetFileRecord> {
		final IntervalTreeMap<Boolean> intervalTreeMap;
		final CloseableIterator<SetFileRecord> delegate;
		IntersectBedIterator(final CloseableIterator<SetFileRecord> delegate,final Path bedPath) {
			this.delegate = delegate;
			try(BedLineReader br = new BedLineReader(bedPath)) {
				br.setContigNameConverter(ContigNameConverter.fromOneDictionary(SequenceDictionaryUtils.extractRequired(faidxRef)));
				br.setValidationStringency(validationStringency);
				this.intervalTreeMap = br.toIntervalTreeMap(X->Boolean.TRUE);
				}
			}
		
		@Override
		protected final SetFileRecord advance() {
			while(this.delegate.hasNext()) {
				final SetFileRecord rec = this.delegate.next();
				final List<Locatable> L = new ArrayList<>(rec.size());
				for(int i=0;i< rec.size();i++) {
					final Locatable loc = rec.get(i);
					boolean keep = this.intervalTreeMap.containsOverlapping(loc);
					if(!keep && remove_unused_interval) {
						continue;
						}
					L.add(loc);
					}
				if(L.isEmpty()) continue;
				return SetFileRecord.create(rec.getName(), L);
				}
			return null;
			}

		
		@Override
		public void close() {
			delegate.close();
			}
		}
	
	private static class Cluster {
		final List<SetFileRecord> records = new ArrayList<>();
		long sum_length = 0L;
		void add(SetFileRecord rec) {
			this.records.add(rec);
			this.sum_length += rec.getLongSumOfLengthOnReference();
		}
		long getSumLength(final SetFileRecord malus) {
			return this.sum_length + (malus==null?0:malus.getLongSumOfLengthOnReference());
		}

	}

	private enum Action {
		tobed,
		frombed,
		view,
		cluster
		};
		
	private String noChr(final String contig) {
		if(trim_chr_prefix && contig.toLowerCase().startsWith("chr")) {
			return contig.substring(3);
		}
		return contig;
	}
	
	private Comparator<Locatable> createSorter() {
		return new ContigDictComparator(SequenceDictionaryUtils.extractRequired(this.faidxRef)).createLocatableComparator();
	}
	
	private CloseableIterator<SetFileRecord> openSetFileIterator(final List<String> args) throws IOException {
		CloseableIterator<SetFileRecord> iter  = null;
		final String input = oneFileOrNull(args);
		final SetFileReaderFactory srf  = new SetFileReaderFactory(SequenceDictionaryUtils.extractRequired(this.faidxRef));
		if(input==null) {
			iter = srf.open(IOUtils.openStdinForBufferedReader());
		} else {
			iter  =srf.open(IOUtils.openURIForBufferedReading(input));
		}
		
		if(intersectBedPath!=null) {
			iter  = new IntersectBedIterator(iter, this.intersectBedPath);
			}
		if(!intersectVcfPath.isEmpty()) {
			iter = new IntersectVcfIterator(iter, IOUtils.unrollPaths(this.intersectVcfPath));
			}
		return iter;
		}
	
	/** print SetFileRecord to pw */
	private void print(PrintWriter pw,SetFileRecord setfile) {
		if(setfile.isEmpty()) return;
		pw.write(setfile.getName());
		for(int i=0;i< setfile.size();i++) {
			final Locatable rec = setfile.get(i);
			pw.write(i==0?"\t":",");
			pw.write(noChr(rec.getContig()));
			pw.write(":");
			pw.write(String.valueOf(rec.getStart()));
			pw.write("-");
			pw.write(String.valueOf(rec.getEnd()));
			}
		pw.write("\n");
	}
	
	private int makeClusters(final List<String> args) throws IOException {
		if(this.number_of_jobs<1 && this.long_length_per_bin<1L) {
			LOG.error("at least --jobs or --size must be specified.");
			return -1;
			}
		if(this.number_of_jobs>0 &&  this.long_length_per_bin>0) {
			LOG.error(" --jobs OR --size must be specified. Not both.");
			return -1;
			}
		
		final List<Cluster> clusters = new ArrayList<>();
	
		try(CloseableIterator<SetFileRecord> iter = openSetFileIterator(args)) {
			final List<SetFileRecord> records = iter.stream().
					filter(R->!R.isEmpty()).
					sorted((A,B)->Long.compare(B.getLongSumOfLengthOnReference(), A.getLongSumOfLengthOnReference())).
				collect(Collectors.toCollection(LinkedList::new));
			while(!records.isEmpty()) {
				final SetFileRecord first = records.remove(0);
				if(number_of_jobs>0) {
					if(clusters.size() < this.number_of_jobs) {
						final Cluster c = new Cluster();
						c.add(first);
						}
					else {
						int best_idx=-1;
						double best_length=-1;
						for(int y=0;y< clusters.size();++y) {
							final double total_length = clusters.get(y).getSumLength(first);
							if(best_idx==-1 ||total_length<best_length ) {
								best_idx=y;
								best_length = total_length;
								}
							}
						clusters.get(best_idx).add(first);
						}
					}
				else {
					int y=0;
					while(y<clusters.size()) {
							final Cluster cluster = clusters.get(y);
							if(cluster.getSumLength(first)<=this.long_length_per_bin) {
								cluster.add(first);
								break;
								}
							y++;
							}
					if(y==clusters.size()) {
						final Cluster cluster = new Cluster();
						cluster.add(first);
						clusters.add(cluster);
						}
					}
				}// end wile !records.isEmpty
			}// end open
		int clusterid = 0;
		try(final ArchiveFactory archive = ArchiveFactory.open(this.outputFile)) {
			final Comparator<Locatable> sorter = createSorter();
			for(final Cluster cluster : clusters) {
				Collections.sort(cluster.records,(A,B)->{
					final Locatable s1  = A.get(0);
					final Locatable s2  = B.get(0);
					final int i = sorter.compare(s1, s2);
					if(i!=0) return i;
					return A.getName().compareTo(B.getName());
					});
				
				final String filename = String.format("cluster.%05d"+SetFileRecord.FILE_EXTENSION,clusterid);
				try(PrintWriter pw = archive.openWriter(filename)) {
					for(final SetFileRecord rec: cluster.records) {
						print(pw,rec);
						}
					pw.flush();
					}
				LOG.info(filename+" "+cluster.getSumLength(null)+"bp");
				++clusterid;
				}
			}
		return 0;
		}

	
	private int view(final List<String> args) throws IOException {
		try(CloseableIterator<SetFileRecord> iter = openSetFileIterator(args)) {
			try(PrintWriter pw = super.openPathOrStdoutAsPrintWriter(this.outputFile)) {
				while(iter.hasNext()) {
					final SetFileRecord rec = iter.next();
					print(pw,rec);
					}
				pw.flush();
				}
			}
		return 0;
		}
	
	private int toBed(final List<String> args) throws IOException {
		try(CloseableIterator<SetFileRecord> iter = openSetFileIterator(args)) {
			try(PrintWriter pw = super.openPathOrStdoutAsPrintWriter(this.outputFile)) {
				while(iter.hasNext()) {
					final SetFileRecord rec = iter.next();
					for(Locatable loc : rec) {
						pw.print(noChr(loc.getContig()));
						pw.print("\t");
						pw.print(loc.getStart()-1);
						pw.print("\t");
						pw.print(loc.getEnd());
						pw.print("\t");
						pw.print(rec.getName());
						pw.println();
						}
					}
				pw.flush();
				}
			}
		return 0;
		}
	
	private int fromBed(final List<String> args) throws IOException {
		final String input = oneFileOrNull(args);
		final Function<BedLine,String> bed2name = bed->{
			if(bed.getColumnCount()<4) throw new IllegalArgumentException("Expected 4 columns but got "+bed);
			return bed.get(3);
			};
		try(BufferedReader br = super.openBufferedReader(input)) {
			try(BedLineReader blr = new BedLineReader(br, input)) {
				blr.setValidationStringency(validationStringency);
				blr.setContigNameConverter(ContigNameConverter.fromOneDictionary(SequenceDictionaryUtils.extractRequired(faidxRef)));

				try(PrintWriter pw = super.openPathOrStdoutAsPrintWriter(this.outputFile)) {
					final EqualIterator<BedLine> iter = new EqualIterator<BedLine>(blr.stream().iterator(),(A,B)->bed2name.apply(A).compareTo(bed2name.apply(B)));
					while(iter.hasNext()) {
						final List<BedLine> lines = iter.next();
						pw.print(bed2name.apply(lines.get(0)));
						for(int i=0;i< lines.size();i++) {
							pw.print(i==0?"\t":",");
							final BedLine rec = lines.get(i);
							pw.print(noChr(rec.getContig()));
							pw.print(":");
							pw.print(rec.getStart()+1);
							pw.print("-");
							pw.print(rec.getEnd());
							}
						pw.println();
						}
					iter.close();
					pw.flush();
					}
				}
			}
		return 0;
		}
	
	@Override
	public int doWork(final List<String> args0) {
		if(args0.isEmpty()) {
			LOG.error("action parameter is missing.");
			return -1;
			}
		try {
			final Action action = Action.valueOf(args0.get(0));
			final List<String> args = args0.subList(1, args0.size());
			switch(action) {
				case frombed: return fromBed(args);
				case tobed: return toBed(args);
				case view: return view(args);
				case cluster: return makeClusters(args);
				default: LOG.error("not implemented "+action);return -1;
				}
			}
		catch(final Throwable err) {
			LOG.error(err);
			return -1;
			}
		}
	
	public static void main(final String[] args) {
		new SetFileTools().instanceMainWithExit(args);
	}

}

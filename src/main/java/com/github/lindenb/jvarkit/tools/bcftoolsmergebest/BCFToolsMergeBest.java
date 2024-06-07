package com.github.lindenb.jvarkit.tools.bcftoolsmergebest;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.beust.jcommander.Parameter;
import com.github.lindenb.jvarkit.io.IOUtils;
import com.github.lindenb.jvarkit.jcommander.OnePassVcfLauncher;
import com.github.lindenb.jvarkit.lang.CharSplitter;
import com.github.lindenb.jvarkit.lang.JvarkitException;
import com.github.lindenb.jvarkit.lang.StringUtils;
import com.github.lindenb.jvarkit.util.JVarkitVersion;
import com.github.lindenb.jvarkit.util.jcommander.Program;
import com.github.lindenb.jvarkit.util.log.Logger;
import com.github.lindenb.jvarkit.util.vcf.VCFUtils;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFIterator;
/**
BEGIN_DOC

## Motivation

can a VCF file generated by 'bcftools merge --force-samples', identify duplicate samples, keep the best"

## Example

```
$ bcftools merge --force-samples src/test/resources/rotavirus_rf.vcf.gz src/test/resources/rotavirus_rf.freebayes.vcf.gz  |\
	grep "#CHROM" -A 3
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	S1	S2	S3	S4	S5	2:S5	2:S2	2:S4	2:S3	2:S1
RF01	243	.	A	C	0	.	DPB=28;EPPR=3.37221;GTI=0;MQMR=60;NS=5;NUMALT=1;ODDS=7.64661;PAIREDR=1;PQR=0;PRO=0;QR=408;RO=24;RPPR=3.37221;SRF=24;SRP=55.1256;SRR=0;DP=28;AB=0.5;ABP=3.0103;AF=0.1;AO=4;CIGAR=1X;DPRA=1.66667;EPP=11.6962;LEN=1;MEANALT=1;MQM=60;PAIRED=1;PAO=0;PQA=0;QA=68;RPL=4;RPP=11.6962;RPR=0;RUN=1;SAF=4;SAP=11.6962;SAR=0;TYPE=snp;AN=10;AC=1	GT:AD:AO:DP:PL:QA:QR:RO	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	0/0:4,0:0:4:0,12,63:0:68:4	0/0:7,1:1:8:0,7,92:17:119:7	0/1:2,2:2:4:20,0,20:34:34:2	0/0:7,1:1:8:0,7,92:17:119:7	0/0:4,0:0:4:0,12,63:0:68:4
RF01	280	.	A	C	0	.	DPB=24;EPPR=4.58955;GTI=0;MQMR=60;NS=5;NUMALT=1;ODDS=6.90616;PAIREDR=1;PQR=0;PRO=0;QR=374;RO=22;RPPR=4.58955;SRF=22;SRP=50.7827;SRR=0;DP=24;AB=0.4;ABP=3.44459;AF=0.1;AO=2;CIGAR=1X;DPRA=1.05263;EPP=3.0103;LEN=1;MEANALT=1;MQM=60;PAIRED=1;PAO=0;PQA=0;QA=34;RPL=1;RPP=3.0103;RPR=1;RUN=1;SAF=2;SAP=7.35324;SAR=0;TYPE=snp;AN=10;AC=1	GT:AD:AO:DP:PL:QA:QR:RO	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	0/1:3,2:2:5:17,0,33:34:51:3	0/0:7,0:0:7:0,21,109:0:119:7	0/0:2,0:0:2:0,6,32:0:34:2	0/0:7,0:0:7:0,21,109:0:119:7	0/0:3,0:0:3:0,9,48:0:51:3
RF01	351	.	T	A	0	.	DPB=25;EPPR=3.94093;GTI=1;MQMR=60;NS=5;NUMALT=1;ODDS=8.27411;PAIREDR=1;PQR=0;PRO=0;QR=357;RO=21;RPPR=3.94093;SRF=21;SRP=48.6112;SRR=0;DP=25;AB=0.4;ABP=3.87889;AF=0.2;AO=4;CIGAR=1X;DPRA=1;EPP=11.6962;LEN=1;MEANALT=1;MQM=60;PAIRED=1;PAO=0;PQA=0;QA=68;RPL=0;RPP=11.6962;RPR=4;RUN=1;SAF=4;SAP=11.6962;SAR=0;TYPE=snp;AN=10;AC=2	GT:AD:AO:DP:PL:QA:QR:RO	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	./.:.:.:.:.:.:.:.	0/0:7,0:0:7:0,21,109:0:119:7	0/1:3,2:2:5:17,0,33:34:51:3	0/0:4,0:0:4:0,12,63:0:68:4	0/1:3,2:2:5:17,0,33:34:51:3	0/0:4,0:0:4:0,12,63:0:68:4


$ bcftools merge --force-samples src/test/resources/rotavirus_rf.vcf.gz src/test/resources/rotavirus_rf.freebayes.vcf.gz  |\
	java -jar dist/jvarkit.jar bcftoolsmergebest |\
	grep "#CHROM" -A 3
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	S3	S4	S5	S1	S2
RF01	243	.	A	C	0	.	AB=0.5;ABP=3.0103;AC=1;AF=0.1;AN=10;AO=4;CIGAR=1X;DP=28;DPB=28;DPRA=1.66667;EPP=11.6962;EPPR=3.37221;GTI=0;LEN=1;MEANALT=1;MQM=60;MQMR=60;NS=5;NUMALT=1;ODDS=7.64661;PAIRED=1;PAIREDR=1;PAO=0;PQA=0;PQR=0;PRO=0;QA=68;QR=408;RO=24;RPL=4;RPP=11.6962;RPPR=3.37221;RPR=0;RUN=1;SAF=4;SAP=11.6962;SAR=0;SRF=24;SRP=55.1256;SRR=0;TYPE=snp	GT:AD:AO:DP:PL:QA:QR:RO	0/0:7,1:1:8:0,7,92:17:119:7	0/1:2,2:2:4:20,0,20:34:34:2	0/0:4,0:0:4:0,12,63:0:68:4	./.	./.
RF01	280	.	A	C	0	.	AB=0.4;ABP=3.44459;AC=1;AF=0.1;AN=10;AO=2;CIGAR=1X;DP=24;DPB=24;DPRA=1.05263;EPP=3.0103;EPPR=4.58955;GTI=0;LEN=1;MEANALT=1;MQM=60;MQMR=60;NS=5;NUMALT=1;ODDS=6.90616;PAIRED=1;PAIREDR=1;PAO=0;PQA=0;PQR=0;PRO=0;QA=34;QR=374;RO=22;RPL=1;RPP=3.0103;RPPR=4.58955;RPR=1;RUN=1;SAF=2;SAP=7.35324;SAR=0;SRF=22;SRP=50.7827;SRR=0;TYPE=snp	GT:AD:AO:DP:PL:QA:QR:RO	0/0:7,0:0:7:0,21,109:0:119:7	0/0:2,0:0:2:0,6,32:0:34:2	0/1:3,2:2:5:17,0,33:34:51:3	./.	./.
RF01	351	.	T	A	0	.	AB=0.4;ABP=3.87889;AC=2;AF=0.2;AN=10;AO=4;CIGAR=1X;DP=25;DPB=25;DPRA=1;EPP=11.6962;EPPR=3.94093;GTI=1;LEN=1;MEANALT=1;MQM=60;MQMR=60;NS=5;NUMALT=1;ODDS=8.27411;PAIRED=1;PAIREDR=1;PAO=0;PQA=0;PQR=0;PRO=0;QA=68;QR=357;RO=21;RPL=0;RPP=11.6962;RPPR=3.94093;RPR=4;RUN=1;SAF=4;SAP=11.6962;SAR=0;SRF=21;SRP=48.6112;SRR=0;TYPE=snp	GT:AD:AO:DP:PL:QA:QR:RO	0/1:3,2:2:5:17,0,33:34:51:3	0/0:4,0:0:4:0,12,63:0:68:4	0/0:7,0:0:7:0,21,109:0:119:7	./.	./.
```

END_DOC
*/
@Program(name="bcftoolsmergebest",
description="Scan a VCF file generated by 'bcftools merge --force-samples', identify duplicate samples, keep the best",
keywords={"merge","vcf","bcftools"},
creationDate="20240604",
modificationDate="20240604",
jvarkit_amalgamion = true,
biostars={9594639},
menu="VCF Manipulation"
)
public class BCFToolsMergeBest extends OnePassVcfLauncher {
	private static final Logger LOG = Logger.build(BCFToolsMergeBest.class).make();
	@Parameter(names="--rename-samples",description = "TSV file for manually assigning samples . Syntax: old-name(tab)new-name. Both names must be present in the VCF file")
	private Path renameSamplesPath=null;

	@Parameter(names="-c",description = "comma separated list of criteria used to compare to genotype: GQ:best GQ, DP: highest depth, AD : best AD ratio according to diploy genotype, PL: highest value, GQ: better called than no-call, FT: PASS variant are better, RND: random")
	private String betterThanStr=String.join(",",VCFConstants.GENOTYPE_QUALITY_KEY,VCFConstants.DEPTH_KEY,VCFConstants.GENOTYPE_ALLELE_DEPTHS,VCFConstants.GENOTYPE_PL_KEY,VCFConstants.GENOTYPE_FILTER_KEY,"RND");
	
	private static class Sample {
		final String sn;
		final Set<String> aliases = new  HashSet<>();
		Sample(final String sn) {
			this.sn = sn;
			this.aliases.add(sn);
			}
		@Override
		public String toString() {
			return sn+"<-("+String.join(" | ", this.aliases)+")";
			}
		}
	private final Map<String,String> renameMap = new HashMap<>();
	private final List<Comparator<Genotype>> betterThanList=new ArrayList<>();
	
private boolean isBetter(Genotype g1,Genotype g2) {
	for(Comparator<Genotype> fun: this.betterThanList) {
		final int i=fun.compare(g1, g2);
		if(i==0) continue;
		return i<0;
		}
	return true;
	}
	
private Genotype findBest(final List<Genotype> array) {
	if(array.size()==1)  return array.get(0);
	Genotype best=array.get(0);
	for(int i=1;i< array.size();i++) {
		if(isBetter(array.get(i),best)) {
			best=array.get(i); 
			}
		}
	return best;
	}
	
@Override
protected Logger getLogger() {
	return LOG;
	}

@Override
protected int beforeVcf() {
	
	if(this.renameSamplesPath!=null) {
		
		try(BufferedReader br=IOUtils.openPathForBufferedReading(this.renameSamplesPath)) {
			br.lines().filter(S->!StringUtils.isBlank(S)).forEach(S->{
				final String[] tokens= CharSplitter.TAB.split(S);
				if(tokens.length!=2) throw new JvarkitException.TokenErrors(2, tokens);
				if(renameMap.containsKey(tokens[0])) throw new IllegalArgumentException("duplicate old-name :"+tokens[0]+" in "+this.betterThanStr);
				if(renameMap.containsKey(tokens[1])) throw new IllegalArgumentException("new-name is also old-name in :"+tokens[1]+" in "+this.betterThanStr);
				if(renameMap.values().stream().anyMatch(T->T.equals(tokens[0]))) throw new IllegalArgumentException("new-name is also old-name");
				renameMap.put(tokens[0], tokens[1]);
				});
			}
		catch(IOException err) {
			LOG.error(err);
			return -1;
		}
	}

	
	
	for(final String c: CharSplitter.COMMA.split(this.betterThanStr)) {
		if(StringUtils.isBlank(c)) continue;
		if(c.equals(VCFConstants.GENOTYPE_QUALITY_KEY)) {
			betterThanList.add((g1,g2)->{
				if(g1.hasGQ() && g2.hasGQ()) {
					return g1.getGQ()> g2.getGQ()?-1:1;
					}
				return 0;
				});
			}
		else if(c.equals(VCFConstants.DEPTH_KEY)) {
			betterThanList.add((g1,g2)->{
				if(g1.hasDP() && g2.hasDP()) {
					return g1.getDP()> g2.getDP()?-1:1;
					}
				return 0;
				});
			}
		else if(c.equals(VCFConstants.GENOTYPE_PL_KEY)) {
			betterThanList.add((g1,g2)->{
				if(g1.hasPL() && g2.hasPL()) {
					final int pl1=Arrays.stream(g1.getPL()).max().orElse(0);
					final int pl2=Arrays.stream(g2.getPL()).max().orElse(0);
					return pl1>pl2?-1:1;
					}
				return 0;
				});
			}
		else if(c.equals(VCFConstants.GENOTYPE_KEY)) {
			betterThanList.add((g1,g2)->{
				if(g1.isNoCall() && !g2.isNoCall()) {
					return 1;
					}
				else if(!g1.isNoCall() && g2.isNoCall()) {
					return -1;
					}
				return 0;
				});
			}
		else if(c.equals(VCFConstants.GENOTYPE_FILTER_KEY)) {
			betterThanList.add((g1,g2)->{
				if(g1.isFiltered() && !g2.isFiltered()) {
					return 1;
					}
				else if(!g1.isFiltered() && g2.isFiltered()) {
					return -1;
					}
				return 0;
				});
			}
		else if(c.equals(VCFConstants.GENOTYPE_ALLELE_DEPTHS)) {
			betterThanList.add((g1,g2)->{
				if(g1.hasAD() && g2.hasAD()) {
					final int[] ad1 = g1.getAD();
					if(ad1.length!=2) return 0;
					final int[] ad2 = g2.getAD();
					if(ad2.length!=2) return 0;
					if(g1.isHomVar() && g1.isHomVar() && ad1[0]!=ad2[0]) {
						if(ad1[0]!=ad2[0])  return ad1[0] < ad2[0]?-1:1; //cleaner genotype
						return ad1[1] > ad2[1]?-1:1;
						}
					else if(g1.isHomRef() && g1.isHomRef() && ad1[1]!=ad2[1]) {
						if(ad1[1]!=ad2[1])  return ad1[1] < ad2[1]?-1:1;//cleaner genotype
						return ad1[0] > ad2[0]?-1:1;
						}
					else if(g1.isHet() && g1.isHet() && (ad1[0]+ad1[1])>0 && (ad2[0]+ad2[1])>0) {
						final double f1  =1.0-(ad1[1]/(double)(ad1[0]+ad1[1]));
						final double f2  =1.0-(ad2[1]/(double)(ad2[0]+ad2[1]));
						return f1 < f2?-1:1;
						}
					}
				return 0;
				});
			}
		else if(c.equals("RND")) {
			betterThanList.add((g1,g2)->Math.random() < 0.5?-1:1);
			}
		else
			{
			LOG.error("unknown criteria :"+c);
			return -1;
			}
		}
	if(this.betterThanList.isEmpty()) {
		LOG.error("no prioritization criteria");
		return -1;
		}
	return super.beforeVcf();
	}
@Override
protected int doVcfToVcf(String inputName, VCFIterator iterin, VariantContextWriter out) {
	final VCFHeader header = iterin.getHeader();
	final Set<String> vcf_samples_set = new HashSet<>(header.getGenotypeSamples());//as set
	final Map<String,Sample> hashmap = new HashMap<>(vcf_samples_set.size()); 
	
	final Pattern pattern = Pattern.compile("^\\d+\\:.+");
	
	for(String oldName:this.renameMap.keySet()) {
		if(!vcf_samples_set.contains(oldName)) continue;
		final String newName=this.renameMap.get(oldName);
		if(!vcf_samples_set.contains(newName)) throw new IllegalArgumentException("sample "+oldName+" was set to replace "+newName+" but it's not in the final VCF.");
		Sample sample = hashmap.get(newName);
		if(sample==null) {
			sample = new  Sample(newName);
			hashmap.put(newName,sample);
			}
		sample.aliases.add(oldName);
		}
	
	// sample without ambiguity
	for(final String sn:vcf_samples_set ) {
		if(pattern.matcher(sn).matches()) continue;//this is bcftools merge alias,skip
		if(hashmap.values().stream().anyMatch(it->it.aliases.contains(sn))) {
			continue;//looks like a bcftools merge alias
			}
		hashmap.put(sn,new  Sample(sn));
		}

	// sample with ambiguity
	for(final String sn:vcf_samples_set ) {
		if(!pattern.matcher(sn).matches()) continue;//not a bcftools merge alias
		if(hashmap.values().stream().anyMatch(it->it.aliases.contains(sn))) {
			continue;//looks like a bcftools merge alias
			}
		String sn2 = StringUtils.substringAfter(sn, ":");
		if(this.renameMap.containsKey(sn2)) {
			sn2= this.renameMap.get(sn2);
			}
		
		// destination new-name is not present, ignore it
		if(!vcf_samples_set.contains(sn2)) {
			sn2 = sn;
			}
		Sample sample = hashmap.get(sn2);
		if(sample==null) {
			sample = new  Sample(sn2);
			hashmap.put(sn2,sample);
			}
		sample.aliases.add(sn);
		}

	//paranoid
	for(final String sn:vcf_samples_set ) {
		if(hashmap.values().stream().filter(V->V.aliases.contains(sn)).count()!=1L) {
			throw new IllegalStateException(sn+" "+hashmap);
			}
		}
	
	
	final VCFHeader header2 = new VCFHeader(
			header.getMetaDataInInputOrder(),
			new TreeSet<>(hashmap.keySet())
			);
	final String mappinStr= hashmap.values().stream().
			filter(it->it.aliases.size()>1).
			flatMap(it->it.aliases.stream().filter(S->!it.sn.equals(S)).map(S->S+"->"+it.sn)).
			collect(Collectors.joining(" ; "));
			
	if(!StringUtils.isBlank(mappinStr)) {
		LOG.info(mappinStr);
		header2.addMetaDataLine(new VCFHeaderLine(getProgramName()+".mapping",mappinStr));
		}
	
	JVarkitVersion.getInstance().addMetaData(this, header2);
	
	out.writeHeader(header2);
	
	// nothing to change
	if(hashmap.values().stream().allMatch(it->it.aliases.size()==1) || hashmap.isEmpty()) {
		VCFUtils.copyVariantsTo(iterin, out);
		return 0;
		}
	
	while(iterin.hasNext()) {
		final VariantContext ctx = iterin.next();
		final VariantContextBuilder vcb = new VariantContextBuilder(ctx);
		final List<Genotype> genotypes = new ArrayList<>(hashmap.size());
		for(final Sample sn: hashmap.values()) {
			final Genotype g = findBest(sn.aliases.stream().
				map(SN->ctx.getGenotype(SN)).
				collect(Collectors.toList()));
			genotypes.add(new GenotypeBuilder(g).name(sn.sn).phased(false).make());
			}
		vcb.genotypes(genotypes);
		out.add(vcb.make());
		}
	return 0;
	}


	public static void main(String[] args) {
		new BCFToolsMergeBest().instanceMainWithExit(args);
	}

}

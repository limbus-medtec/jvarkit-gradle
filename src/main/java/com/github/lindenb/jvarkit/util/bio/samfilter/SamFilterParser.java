/* Generated By:JavaCC: Do not edit this line. SamFilterParser.java */
/*
The MIT License (MIT)

Copyright (c) 2019 Pierre Lindenbaum

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
package com.github.lindenb.jvarkit.util.bio.samfilter;
import htsjdk.samtools.util.CloserUtil;
import htsjdk.samtools.util.Interval;
import htsjdk.samtools.util.IntervalTreeMap;
import htsjdk.samtools.util.RuntimeIOException;
import htsjdk.samtools.Cigar;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMReadGroupRecord;
import htsjdk.samtools.filter.SamRecordFilter;
import java.util.function.Predicate;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import com.github.lindenb.jvarkit.io.IOUtils;
import com.github.lindenb.jvarkit.util.bio.bed.BedLine;
import com.github.lindenb.jvarkit.util.bio.bed.BedLineCodec;
import com.github.lindenb.jvarkit.util.log.Logger;


import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.function.Predicate;
//import javax.annotation.processing.Generated;

/**

  <#DIGIT:["0"-"9"]>
| <#LETTER: (["a"-"z"]|"_"|["A"-"Z"])>
| <INT:<DIGIT>  (<DIGIT>)*  >
| <#CMP: ("ge"|"gt"|"le"|"lt"|"eq"|"ne")>
| <CPAR: ")">
| <NOT: "(NOT">
| <AND: "(AND">

| <ANY: ~[]>
| <MAPQ: "(MAPQ-" <CMP> >
| <FLAG: "(flag" >
| <LE: "(<=" >
| <GE: "(>=" >
| <EQ: "(==" >
| <NE: "(!=" >
| <LT: "(<" >
| <GT: "(>" >

  <OR>   list = nodelist() <CPAR> { return orNode(list);}
	| <AND>  list =  nodelist() <CPAR> { return andNode(list);}
	| <NOT> pf = node() <CPAR> { return pf.negate(); }
	| <READMAPPED> <CPAR> { return readUnmapped().negate();}
	| <READUNMAPPED> <CPAR> { return readUnmapped();}
	| <FLAG>  t=<INT> <CPAR> { return testFlag(Integer.parseInt(t.image));}
	| <OVERLAP> text=characters() <CPAR> { return overlapBed(text);}
	

private  ToIntFunction<SAMRecord> integer() : {Token t;}
	{
	(
	  t=<INT> { return  toInt(Integer.parseInt(t.image)); }
	 | <MAPQ> <CPAR> { return mapq(); 	}
	 )}


private java.util.Comparator<Integer> intcmp():{}
	{
	(
	 <LT> { return null ;}
	)
	}	
	
	private List<Predicate<SAMRecord>> nodelist(): {
			final List<Predicate<SAMRecord>> L = new ArrayList<Predicate<SAMRecord>>();
			Predicate<SAMRecord> f=null;
			}
	{
	f=node() { L.add(f);} (f=node() { L.add(f);} )* 
		{
		return L;
		}
	}
*/


//@Generated("javacc")
public class SamFilterParser implements SamFilterParserConstants {
                private static final Logger LOG = Logger.build(SamFilterParser.class).make();

                public static final String FILTER_DESCRIPTION = "A filter expression. Reads matching the expression will be filtered-out. Empty String means 'filter out nothing/Accept all'. See https://github.com/lindenb/jvarkit/blob/master/src/main/resources/javacc/com/github/lindenb/jvarkit/util/bio/samfilter/SamFilterParser.jj for a complete syntax. ";
                public static final String DEFAULT_FILTER = "mapqlt(1) || MapQUnavailable() || Duplicate() || FailsVendorQuality() || NotPrimaryAlignment() || SupplementaryAlignment()";
                public static final String DEFAULT_OPT = "--samFilter";

                public static final SamRecordFilter  ACCEPT_ALL = new SamRecordFilter() {
                                        @Override
                                        public boolean filterOut(final SAMRecord first, SAMRecord second) {
                                                return false;
                                        }

                                        @Override
                                        public boolean filterOut(SAMRecord record) {
                                                return false;
                                        }
                                        @Override
                                        public String toString() {
                                                return "Accept All/ Filter out nothing";
                                        }
                                };


        public static class StringConverter implements IStringConverter<SamRecordFilter>
                {
                @Override
                public SamRecordFilter convert(final String s)
                        {
                        if(s==null || s.trim().isEmpty()) return ACCEPT_ALL;
                                try {
                                        return SamFilterParser.build(s);
                                } catch (final ParseException e) {
                                        throw new ParameterException(e);
                                        }
                        }
                }

                public static SamRecordFilter buildDefault() {
                        try {
                                return build(DEFAULT_FILTER);
                                }
                        catch(final ParseException err)
                                {
                                throw new IllegalArgumentException(err);
                                }
                        }

                public static SamRecordFilter buildAcceptAll() {
                        return ACCEPT_ALL;
                        }


                public static SamRecordFilter build(final String expr) throws ParseException {

                        Reader r= null;
                        try {
                        r= new StringReader(expr);
                        SamFilterParser sfp =new SamFilterParser(r);
                        final Predicate<SAMRecord> pred= sfp.anyNode();
                        r.close();
                        return new SamRecordFilter() {
                                @Override
                                public boolean filterOut(SAMRecord first, SAMRecord second) {
                                        throw new IllegalStateException("SamRecordFilter.filterOut(a,b); <- shouldn't happen");
                                }

                                @Override
                                public boolean filterOut(final SAMRecord record) {
                                        return pred.test(record);
                                }
                                @Override
                                public String toString() {
                                        return expr;
                                        }
                        };
                        } catch(final ParseException err) {
                                LOG.error(err);
                                throw new RuntimeIOException(err);
                        } catch(final IOException err) {
                                LOG.error(err);
                                throw new RuntimeIOException(err);
                        } finally
                        {
                                CloserUtil.close(r);
                        }
                }


        private static Predicate<SAMRecord> overlapBed(final String fname) {
            final File bedFile = new File(fname);
            final BedLineCodec codec = new BedLineCodec();
            final IntervalTreeMap<Boolean> intervals = new IntervalTreeMap<Boolean>();
            BufferedReader r = null;
            try {
                r = IOUtils.openFileForBufferedReading(bedFile);
                String line;
                while((line=r.readLine())!=null) {
                        final BedLine bedline = codec.decode(line);
                        if(bedline==null)  continue;
                        intervals.put(new Interval(
                                bedline.getContig(),
                                bedline.getStart()+1,
                                bedline.getEnd()),
                                Boolean.TRUE
                                );
                }
                CloserUtil.close(r);
                    return new Predicate<SAMRecord>() {
                                        @Override
                                        public boolean test(final SAMRecord t) {
                                                return !t.getReadUnmappedFlag() &&
                                                                intervals.containsContained(new Interval(t.getContig(), t.getStart(), t.getEnd()))
                                                                ;
                                        }
                                };
            } catch(final IOException err) {
                LOG.error(err);
                throw new RuntimeIOException(err);
            }
            finally {
                CloserUtil.close(r);
            }
        }


        private static String unescape(final String s) throws ParseException
                {
                final StringBuilder b=new StringBuilder(s.length());
        int i=0;
        while(i<s.length())
                {
                if(s.charAt(i)=='\u005c\u005c')
                        {
                        if( i+1== s.length())  throw new ParseException("Badly escaped string "+s);
                        ++i;
                        switch(s.charAt(i))
                                {
                                case 'n': b.append("\u005cn");break;
                                case 'r': b.append("\u005cr");break;
                                case 't': b.append("\u005ct");break;
                                case '\u005c\u005c': b.append("\u005c\u005c");break;
                                case '\u005c'': b.append("\u005c'");break;
                                case '\u005c"': b.append("\u005c"");break;
                                default: throw new ParseException("Badly escaped string "+s);
                                }
                        }
                else
                        {
                        b.append(s.charAt(i));
                        }
                ++i;
                }
        return b.toString();
                }

                private static Predicate<SAMRecord> duplicateFilter() {
                        return new Predicate<SAMRecord>() {
                                @Override public boolean test(final SAMRecord rec) { return rec.getDuplicateReadFlag();}
                        }; }
                private static Predicate<SAMRecord> unmappedFilter() {
                        return new Predicate<SAMRecord>() {
                                @Override public boolean test(final SAMRecord rec) { return rec.getReadUnmappedFlag();}
                        }; }

                private static Predicate<SAMRecord> mappedFilter() {
                        return new Predicate<SAMRecord>() {
                                @Override public boolean test(final SAMRecord rec) { return !rec.getReadUnmappedFlag();}
                        }; }

                private static Predicate<SAMRecord> failsVendorQuality() {
                        return new Predicate<SAMRecord>() {
                                @Override public boolean test(final SAMRecord rec) { return rec.getReadFailsVendorQualityCheckFlag();}
                        }; }
                private static Predicate<SAMRecord> readPaired() {
                        return new Predicate<SAMRecord>() {
                                @Override public boolean test(final SAMRecord rec) { return rec.getReadPairedFlag();}
                        }; }
                private static Predicate<SAMRecord> mateUnmapped() {
                        return new Predicate<SAMRecord>() {
                                @Override public boolean test(final SAMRecord rec) { return rec.getMateUnmappedFlag();}
                        }; }
                private static Predicate<SAMRecord> samFlag(final int flg) {
                        return new Predicate<SAMRecord>() {
                                @Override public boolean test(final SAMRecord rec) { return (rec.getFlags() & flg) != 0;}
                        }; }
        private static Predicate<SAMRecord> sample(final String s) {
            return new Predicate<SAMRecord>() {
                    @Override public boolean test(final SAMRecord rec) { SAMReadGroupRecord rg=rec.getReadGroup(); return rg!=null && s.equals(rg.getSample());}
            }; }
                private static Predicate<SAMRecord> group(final String s) {
            return new Predicate<SAMRecord>() {
                    @Override public boolean test(final SAMRecord rec) { SAMReadGroupRecord rg=rec.getReadGroup(); return rg!=null && s.equals(rg.getId());}
            }; }
                private static Predicate<SAMRecord> notPrimaryAlignmentFlag() {
                return new Predicate<SAMRecord>() {
                        @Override public boolean test(final SAMRecord rec) { return rec.isSecondaryAlignment();}
                }; }
                private static Predicate<SAMRecord> supplementaryAlignmentFlag() {
                return new Predicate<SAMRecord>() {
                        @Override public boolean test(final SAMRecord rec) { return rec.getSupplementaryAlignmentFlag();}
                }; }

        private static Predicate<SAMRecord> readClipped() {
        return new Predicate<SAMRecord>() {
                @Override public boolean test(final SAMRecord rec) {
                if(rec.getReadUnmappedFlag()) return false;
                final Cigar c= rec.getCigar();
                if(c==null || c.isEmpty()) return false;
                return c.isClipped();

                }
        }; }

        private static Predicate<SAMRecord>  mapqUnavailable() {
                return new Predicate<SAMRecord>() {
                @Override public boolean test(final SAMRecord rec) { return  (rec.getMappingQuality() == SAMRecord.NO_MAPPING_QUALITY);}
                }; }



                 private static Predicate<SAMRecord>  hasFlag(final int flg) {
                return new Predicate<SAMRecord>() {
                @Override public boolean test(final SAMRecord rec) { return   (rec.getFlags() & flg) != 0;}
                }; }


        private static Predicate<SAMRecord>  discordant() {
            return new Predicate<SAMRecord>() {
            @Override public boolean test(final SAMRecord rec) {
                return  rec.getReadPairedFlag() &&
                                !rec.getReadUnmappedFlag() &&
                                !rec.getMateUnmappedFlag() &&
                                rec.getReferenceIndex()!=rec.getMateReferenceIndex();
                        }
            }; }


                 private static Predicate<SAMRecord>  mapqLowerThan(final int mapq) {
            return new Predicate<SAMRecord>() {
            @Override public boolean test(final SAMRecord rec) {
                        return  rec.getMappingQuality() < mapq;
                        }
            }; }

  final private Predicate<SAMRecord> anyNode() throws ParseException {
                                          Predicate<SAMRecord> other;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      other = NegateExpr();
                                 {if (true) return other;}
      break;
    case DUPLICATE:
    case UNMAPPED:
    case MAPPED:
    case MATEUNMAPPED:
    case FAILSVENDORQUALITY:
    case NOTPRIMARYALIGNMENT:
    case SUPPLEMENTARYALIGNMENT:
    case PAIRED:
    case CLIPPED:
    case MAPQUNAVAILABLE:
    case OVERLAP:
    case SAMFLAG:
    case SAMPLE:
    case GROUP:
    case HASFLAG:
    case DISCORDANT:
    case MAPQLT:
      other = OrExpr();
                            {if (true) return other;}
      break;
    case OPAR:
      jj_consume_token(OPAR);
      other = anyNode();
      jj_consume_token(CPAR);
                                          {if (true) return other;}
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final private Predicate<SAMRecord> NegateExpr() throws ParseException {
    jj_consume_token(NOT);
    OrExpr();
                         {if (true) return null;}
    throw new Error("Missing return statement in function");
  }

  final private Predicate<SAMRecord> OrExpr() throws ParseException {
                                         Predicate<SAMRecord> root,other;
    root = AndExpr();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
      jj_consume_token(OR);
      other = AndExpr();
                                               root = root.or(other);
    }
                                                                              {if (true) return root;}
    throw new Error("Missing return statement in function");
  }

  final private Predicate<SAMRecord> AndExpr() throws ParseException {
                                          Predicate<SAMRecord> root,other;
    root = UnaryExpr();
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
      jj_consume_token(AND);
      other = UnaryExpr();
                                                    root = root.and(other);
    }
                                                                                   {if (true) return root;}
    throw new Error("Missing return statement in function");
  }

  final private Predicate<SAMRecord> UnaryExpr() throws ParseException {
                                           String str; Token t; int flg;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DUPLICATE:
      jj_consume_token(DUPLICATE);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                      {if (true) return duplicateFilter();}
      break;
    case UNMAPPED:
      jj_consume_token(UNMAPPED);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                     {if (true) return unmappedFilter();}
      break;
    case MAPPED:
      jj_consume_token(MAPPED);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                   {if (true) return mappedFilter();}
      break;
    case FAILSVENDORQUALITY:
      jj_consume_token(FAILSVENDORQUALITY);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                               {if (true) return failsVendorQuality();}
      break;
    case PAIRED:
      jj_consume_token(PAIRED);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                    {if (true) return readPaired();}
      break;
    case CLIPPED:
      jj_consume_token(CLIPPED);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                     {if (true) return readClipped();}
      break;
    case MAPQUNAVAILABLE:
      jj_consume_token(MAPQUNAVAILABLE);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                             {if (true) return mapqUnavailable();}
      break;
    case MATEUNMAPPED:
      jj_consume_token(MATEUNMAPPED);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                          {if (true) return mateUnmapped();}
      break;
    case NOTPRIMARYALIGNMENT:
      jj_consume_token(NOTPRIMARYALIGNMENT);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                                 {if (true) return notPrimaryAlignmentFlag();}
      break;
    case SUPPLEMENTARYALIGNMENT:
      jj_consume_token(SUPPLEMENTARYALIGNMENT);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                                    {if (true) return supplementaryAlignmentFlag();}
      break;
    case OVERLAP:
      jj_consume_token(OVERLAP);
      jj_consume_token(OPAR);
      str = characters();
      jj_consume_token(CPAR);
                                                      {if (true) return overlapBed(str);}
      break;
    case SAMFLAG:
      jj_consume_token(SAMFLAG);
      jj_consume_token(OPAR);
      t = jj_consume_token(INT);
      jj_consume_token(CPAR);
                                            {if (true) return samFlag(Integer.parseInt(t.image));}
      break;
    case SAMPLE:
      jj_consume_token(SAMPLE);
      jj_consume_token(OPAR);
      str = characters();
      jj_consume_token(CPAR);
                                                     {if (true) return sample(str);}
      break;
    case GROUP:
      jj_consume_token(GROUP);
      jj_consume_token(OPAR);
      str = characters();
      jj_consume_token(CPAR);
                                                    {if (true) return group(str);}
      break;
    case HASFLAG:
      jj_consume_token(HASFLAG);
      jj_consume_token(OPAR);
      flg = integer();
      jj_consume_token(CPAR);
                                                  {if (true) return hasFlag(flg);}
      break;
    case DISCORDANT:
      jj_consume_token(DISCORDANT);
      jj_consume_token(OPAR);
      jj_consume_token(CPAR);
                                        {if (true) return discordant();}
      break;
    case MAPQLT:
      jj_consume_token(MAPQLT);
      jj_consume_token(OPAR);
      flg = integer();
      jj_consume_token(CPAR);
                                                 {if (true) return mapqLowerThan(flg);}
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final private String characters() throws ParseException {
        Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SIMPLE_QUOTE_LITERAL:
      t = jj_consume_token(SIMPLE_QUOTE_LITERAL);
                {if (true) return  unescape(t.image.substring(1,t.image.length()-1));}
      break;
    case DOUBLE_QUOTE_LITERAL:
      t = jj_consume_token(DOUBLE_QUOTE_LITERAL);
                {if (true) return unescape(t.image.substring(1,t.image.length()-1));}
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final private int integer() throws ParseException {
                          Token t;
    t = jj_consume_token(INT);
                  {if (true) return  Integer.parseInt(t.image);}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public SamFilterParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[5];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0xfffc1400,0x100,0x200,0xfffc0000,0x30000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x7,0x0,0x0,0x7,0x0,};
   }

  /** Constructor with InputStream. */
  public SamFilterParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public SamFilterParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new SamFilterParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public SamFilterParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new SamFilterParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public SamFilterParser(SamFilterParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(SamFilterParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[35];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 5; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 35; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

        }

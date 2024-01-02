/*
The MIT License (MIT)

Copyright (c) 2024 Pierre Lindenbaum

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
package com.github.lindenb.jvarkit.bed;

import com.github.lindenb.jvarkit.samtools.util.SimpleInterval;

import htsjdk.samtools.util.Locatable;

/** interface for 0-Based interval . Not the same as Locatable which is a 1-based interval */
public interface BedInterval {
	/** return contig */
	public String getContig();
	/** return 0-based start */
	public int getBedStart();
	/** return 0-based end */
	public int getBedEnd();
	/** convert this bed as Locatable */
	public default Locatable toLocatable() {
		if((this instanceof Locatable)) return Locatable.class.cast(this);
		return new SimpleInterval(getContig(), getBedStart()+1, getBedEnd());
		}
	}

/*
 * Copyright (c) 2014-2021 by Wen Yu
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later version.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 */

package pixy.image.png;

import pixy.util.Builder;

/**
 * Special chunk builder for UnknownChunk.
 *
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 12/31/2012
 */
public class UnknownChunkBuilder implements Builder<Chunk> {

	private int chunkType;
	private byte[] data;
	
	public UnknownChunkBuilder type(int type) {
		this.chunkType = type;
		return this;
	}
	
	public UnknownChunkBuilder data(byte[] data) {
		this.data = data;
		return this;
	}
	
	public UnknownChunkBuilder() {}

	public Chunk build() {
		long crc = Chunk.calculateCRC(chunkType, data);
	    
	    return new UnknownChunk(data.length, chunkType, data, crc);	
	}
}

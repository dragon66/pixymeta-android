/**
 * Copyright (c) 2014-2016 by Wen Yu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * Change History - most recent changes go on top of previous changes
 *
 * JFIFThumbnail.java
 *
 * Who   Date       Description
 * ====  =======    =================================================
 * WY    14Jul2015  Added copy constructor
 * WY    12Jul2015  Initial creation
 */

package pixy.meta.jpeg;

import java.io.IOException;
import java.io.OutputStream;

import pixy.image.IBitmap;
import pixy.meta.Thumbnail;

public class JFIFThumbnail extends Thumbnail {

	public JFIFThumbnail(IBitmap thumbnail) {
		super(thumbnail);
	}
	
	public JFIFThumbnail(JFIFThumbnail other) { // Copy constructor
		this.dataType = other.dataType;
		this.height = other.height;
		this.width = other.width;
		this.thumbnail = other.thumbnail;
		this.compressedThumbnail = other.compressedThumbnail;
	}

	@Override
	public void write(OutputStream os) throws IOException {
		IBitmap thumbnail = getRawImage();
		if(thumbnail == null) throw new IllegalArgumentException("Expected raw data thumbnail does not exist!");
		int thumbnailWidth = thumbnail.getWidth();
		int thumbnailHeight = thumbnail.getHeight();
		int[] pixels = new int[thumbnailWidth*thumbnailHeight];
		thumbnail.getPixels(pixels, 0, thumbnailWidth, 0, 0, thumbnailWidth, thumbnailHeight);
		for(int pixel : pixels) {
			os.write(pixel >> 16); // Red
			os.write(pixel >> 8); // Green
			os.write(pixel); // Blue
		}
	}
}
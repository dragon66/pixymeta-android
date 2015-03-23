/**
 * Copyright (c) 2015 by Wen Yu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Any modifications to this file must keep this entire header intact.
 * 
 * Change History - most recent changes go on top of previous changes
 *
 * IRBThumbnail.java
 *
 * Who   Date       Description
 * ====  =========  ===========================================================
 * WY    13Mar2015  Initial creation for IRBReader to encapsulate IRB thumbnail
 */

package pixy.meta.adobe;

import android.graphics.Bitmap;
import pixy.meta.Thumbnail;
import pixy.meta.adobe.ImageResourceID;
import pixy.util.MetadataUtils;

/** 
 * Photoshop Image Resource Block thumbnail wrapper.
 *
 * @author Wen Yu, yuwen_66@yahoo.com 
 * @version 1.0 01/10/2015   
 */
public class IRBThumbnail extends Thumbnail {
	//Padded row bytes = (width * bits per pixel + 31) / 32 * 4.
	private int paddedRowBytes;
	// Total size = widthbytes * height * planes
	private int totalSize;
	// Size after compression. Used for consistency check.
	private int compressedSize;
	// Bits per pixel. = 24
	private int bitsPerPixel;
	// Number of planes. = 1
	private int numOfPlanes;
	private ImageResourceID id;

	public IRBThumbnail(ImageResourceID id, int dataType, int width, int height, int paddedRowBytes, int totalSize, int compressedSize, int bitsPerPixel, int numOfPlanes, byte[] data) {
		this.id = id;
		this.paddedRowBytes = paddedRowBytes;
		this.totalSize = totalSize;
		this.compressedSize = compressedSize;
		this.bitsPerPixel = bitsPerPixel;
		this.numOfPlanes = numOfPlanes;		
		// JFIF data in RGB format. For resource ID 1033 (0x0409) the data is in BGR format.
		if(dataType == DATA_TYPE_KJpegRGB) {
			setImage(width, height, dataType, data);
		} else if(dataType == DATA_TYPE_KRawRGB) {
			// kRawRGB - NOT tested yet!
			int[] colors = MetadataUtils.toARGB(data);
			setImage(Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888));
		}
	}
	
	public int getBitsPerPixel() {
		return bitsPerPixel;
	}
	
	public int getCompressedSize() {
		return compressedSize;
	}
	
	public int getNumOfPlanes() {
		return numOfPlanes;
	}
	
	public int getPaddedRowBytes() {
		return paddedRowBytes;
	}
	
	public ImageResourceID getResouceID() {
		return id;
	}
	
	public int getTotalSize() {
		return totalSize;		
	}
 }
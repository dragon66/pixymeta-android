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
 * ImageMetadata.java
 *
 * Who   Date       Description
 * ====  =========  =====================================================
 * WY    13Mar2015  Initial creation
*/

package pixy.meta.image;

import java.util.Map;

import org.w3c.dom.Document;

import pixy.meta.Metadata;
import pixy.meta.MetadataType;
import pixy.meta.Thumbnail;
import pixy.meta.image.ImageMetadataReader;

public class ImageMetadata extends Metadata {
	
	private ImageMetadataReader reader;

	public ImageMetadata(Document document) {
		super(MetadataType.IMAGE, null);
		this.reader = new ImageMetadataReader(document);
	}
	
	public ImageMetadata(Document document, Map<String, Thumbnail> thumbnails) {
		super(MetadataType.IMAGE, null);
		this.reader = new ImageMetadataReader(document, thumbnails);
	}

	@Override
	public ImageMetadataReader getReader() {
		return reader;
	}
}
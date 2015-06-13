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
 * Metadata.java
 *
 * Who   Date       Description
 * ====  =========  =================================================
 * WY    16Apr2015  Changed insertIRB() parameter List to Collection
 * WY    16Apr2015  Removed ICC_Profile related code
 * WY    13Mar2015  initial creation
 */

package pixy.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import pixy.image.bmp.BMPMeta;
import pixy.image.gif.GIFMeta;
import pixy.image.jpeg.JPEGMeta;
import pixy.image.png.PNGMeta;
import pixy.image.tiff.TIFFMeta;
import pixy.meta.Metadata;
import pixy.util.MetadataUtils;
import pixy.meta.MetadataReader;
import pixy.meta.MetadataType;
import pixy.meta.adobe._8BIM;
import pixy.meta.exif.Exif;
import pixy.meta.iptc.IPTCDataSet;
import pixy.image.ImageType;
import pixy.io.FileCacheRandomAccessInputStream;
import pixy.io.FileCacheRandomAccessOutputStream;
import pixy.io.RandomAccessInputStream;
import pixy.io.RandomAccessOutputStream;

/**
 * Base class for image metadata.
 *  
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 01/12/2015
 */
public abstract class Metadata {
	public static final int IMAGE_MAGIC_NUMBER_LEN = 4;
	// Fields
	private MetadataType type;
	private byte[] data;
	
	// Obtain a logger instance
	private static final Logger LOGGER = LoggerFactory.getLogger(Metadata.class);		
	
	public static void  extractThumbnails(File image, String pathToThumbnail) throws IOException {
		FileInputStream fin = new FileInputStream(image);
		extractThumbnails(fin, pathToThumbnail);
		fin.close();
	}
	
	public static void extractThumbnails(InputStream is, String pathToThumbnail) throws IOException {
		// ImageIO.IMAGE_MAGIC_NUMBER_LEN bytes as image magic number
		PushbackInputStream pushbackStream = new PushbackInputStream(is, IMAGE_MAGIC_NUMBER_LEN);
		ImageType imageType = MetadataUtils.guessImageType(pushbackStream);		
		// Delegate thumbnail extracting to corresponding image tweaker.
		switch(imageType) {
			case JPG:
				JPEGMeta.extractThumbnails(pushbackStream, pathToThumbnail);
				break;
			case TIFF:
				RandomAccessInputStream randIS = new FileCacheRandomAccessInputStream(pushbackStream);
				TIFFMeta.extractThumbnail(randIS, pathToThumbnail);
				randIS.close();
				break;
			case PNG:
				LOGGER.info("PNG image format does not contain any thumbnail");
				break;
			case GIF:
			case PCX:
			case TGA:
			case BMP:
				LOGGER.info("{} image format does not contain any thumbnails", imageType);
				break;
			default:
				pushbackStream.close();
				throw new IllegalArgumentException("Thumbnail extracting is not supported for " + imageType + " image");				
		}		
	}
	
	public static void extractThumbnails(String image, String pathToThumbnail) throws IOException {
		extractThumbnails(new File(image), pathToThumbnail);
	}
	
	/**
	 * @param is input image stream 
	 * @param os output image stream
	 * @param exif Exif instance
	 * @param update True to keep the original data, otherwise false
	 * @throws IOException 
	 */
	public static void insertExif(InputStream is, OutputStream out, Exif exif) throws IOException {
		insertExif(is, out, exif);
	}
	
	public static void insertExif(InputStream is, OutputStream out, Exif exif, boolean update) throws IOException {
		// ImageIO.IMAGE_MAGIC_NUMBER_LEN bytes as image magic number
		PushbackInputStream pushbackStream = new PushbackInputStream(is, IMAGE_MAGIC_NUMBER_LEN);
		ImageType imageType = MetadataUtils.guessImageType(pushbackStream);		
		// Delegate EXIF inserting to corresponding image tweaker.
		switch(imageType) {
			case JPG:
				JPEGMeta.insertExif(pushbackStream, out, exif, update);
				break;
			case TIFF:
				RandomAccessInputStream randIS = new FileCacheRandomAccessInputStream(pushbackStream);
				RandomAccessOutputStream randOS = new FileCacheRandomAccessOutputStream(out);
				TIFFMeta.insertExif(randIS, randOS, exif, update);
				randIS.close();
				randOS.close();
				break;
			case GIF:
			case PCX:
			case TGA:
			case BMP:
			case PNG:
				LOGGER.info("{} image format does not support EXIF data", imageType);
				break;
			default:
				pushbackStream.close();
				throw new IllegalArgumentException("EXIF data inserting is not supported for " + imageType + " image");				
		}		
	}
	
	public static void insertICCProfile(InputStream is, OutputStream out, byte[] icc_profile) throws IOException {
		// ImageIO.IMAGE_MAGIC_NUMBER_LEN bytes as image magic number
		PushbackInputStream pushbackStream = new PushbackInputStream(is, IMAGE_MAGIC_NUMBER_LEN);
		ImageType imageType = MetadataUtils.guessImageType(pushbackStream);		
		// Delegate ICCP inserting to corresponding image tweaker.
		switch(imageType) {
			case JPG:
				JPEGMeta.insertICCProfile(pushbackStream, out, icc_profile);
				break;
			case TIFF:
				RandomAccessInputStream randIS = new FileCacheRandomAccessInputStream(pushbackStream);
				RandomAccessOutputStream randOS = new FileCacheRandomAccessOutputStream(out);
				TIFFMeta.insertICCProfile(icc_profile, 0, randIS, randOS);
				randIS.close();
				randOS.close();
				break;
			case GIF:
			case PCX:
			case TGA:
			case BMP:
				LOGGER.info("{} image format does not support ICCProfile data", imageType);
				break;
			default:
				pushbackStream.close();
				throw new IllegalArgumentException("ICCProfile data inserting is not supported for " + imageType + " image");				
		}		
	}

	public static void insertIPTC(InputStream is, OutputStream out, Collection<IPTCDataSet> iptcs) throws IOException {
		insertIPTC(is, out, iptcs, false);
	}
	
	public static void insertIPTC(InputStream is, OutputStream out, Collection<IPTCDataSet> iptcs, boolean update) throws IOException {
		// ImageIO.IMAGE_MAGIC_NUMBER_LEN bytes as image magic number
		PushbackInputStream pushbackStream = new PushbackInputStream(is, IMAGE_MAGIC_NUMBER_LEN);
		ImageType imageType = MetadataUtils.guessImageType(pushbackStream);		
		// Delegate IPTC inserting to corresponding image tweaker.
		switch(imageType) {
			case JPG:
				JPEGMeta.insertIPTC(pushbackStream, out, iptcs, update);
				break;
			case TIFF:
				RandomAccessInputStream randIS = new FileCacheRandomAccessInputStream(pushbackStream);
				RandomAccessOutputStream randOS = new FileCacheRandomAccessOutputStream(out);
				TIFFMeta.insertIPTC(randIS, randOS, iptcs, update);
				randIS.close();
				randOS.close();
				break;
			case PNG:
			case GIF:
			case PCX:
			case TGA:
			case BMP:
				LOGGER.info("{} image format does not support IPTC data", imageType);
				break;
			default:
				pushbackStream.close();
				throw new IllegalArgumentException("IPTC data inserting is not supported for " + imageType + " image");				
		}		
	}
	
	public static void insertIRB(InputStream is, OutputStream out, Collection<_8BIM> bims) throws IOException {
		insertIRB(is, out, bims, false);
	}
	
	public static void insertIRB(InputStream is, OutputStream out, Collection<_8BIM> bims, boolean update) throws IOException {
		// ImageIO.IMAGE_MAGIC_NUMBER_LEN bytes as image magic number
		PushbackInputStream pushbackStream = new PushbackInputStream(is, IMAGE_MAGIC_NUMBER_LEN);
		ImageType imageType = MetadataUtils.guessImageType(pushbackStream);		
		// Delegate IRB inserting to corresponding image tweaker.
		switch(imageType) {
			case JPG:
				JPEGMeta.insertIRB(pushbackStream, out, bims, update);
				break;
			case TIFF:
				RandomAccessInputStream randIS = new FileCacheRandomAccessInputStream(pushbackStream);
				RandomAccessOutputStream randOS = new FileCacheRandomAccessOutputStream(out);
				TIFFMeta.insertIRB(randIS, randOS, bims, update);
				randIS.close();
				randOS.close();
				break;
			case PNG:
			case GIF:
			case PCX:
			case TGA:
			case BMP:
				LOGGER.info("{} image format does not support IRB data", imageType);
				break;
			default:
				pushbackStream.close();
				throw new IllegalArgumentException("IRB data inserting is not supported for " + imageType + " image");				
		}		
	}
	
	public static void insertIRBThumbnail(InputStream is, OutputStream out, Bitmap thumbnail) throws IOException {
		// ImageIO.IMAGE_MAGIC_NUMBER_LEN bytes as image magic number
		PushbackInputStream pushbackStream = new PushbackInputStream(is, IMAGE_MAGIC_NUMBER_LEN);
		ImageType imageType = MetadataUtils.guessImageType(pushbackStream);		
		// Delegate IRB thumbnail inserting to corresponding image tweaker.
		switch(imageType) {
			case JPG:
				JPEGMeta.insertIRBThumbnail(pushbackStream, out, thumbnail);
				break;
			case TIFF:
				RandomAccessInputStream randIS = new FileCacheRandomAccessInputStream(pushbackStream);
				RandomAccessOutputStream randOS = new FileCacheRandomAccessOutputStream(out);
				TIFFMeta.insertThumbnail(randIS, randOS, thumbnail);
				randIS.close();
				randOS.close();
				break;
			case PNG:
			case GIF:
			case PCX:
			case TGA:
			case BMP:
				LOGGER.info("{} image format does not support IRB thumbnail", imageType);
				break;
			default:
				pushbackStream.close();
				throw new IllegalArgumentException("IRB thumbnail inserting is not supported for " + imageType + " image");				
		}		
	}
	
	public static void insertXMP(InputStream is, OutputStream out, String xmp) throws IOException {
		// ImageIO.IMAGE_MAGIC_NUMBER_LEN bytes as image magic number
		PushbackInputStream pushbackStream = new PushbackInputStream(is, IMAGE_MAGIC_NUMBER_LEN);
		ImageType imageType = MetadataUtils.guessImageType(pushbackStream);		
		// Delegate XMP inserting to corresponding image tweaker.
		switch(imageType) {
			case JPG:
				JPEGMeta.insertXMP(pushbackStream, out, xmp, null); // No ExtendedXMP
				break;
			case TIFF:
				RandomAccessInputStream randIS = new FileCacheRandomAccessInputStream(pushbackStream);
				RandomAccessOutputStream randOS = new FileCacheRandomAccessOutputStream(out);
				TIFFMeta.insertXMP(xmp, randIS, randOS);
				randIS.close();
				randOS.close();
				break;
			case PNG:
				PNGMeta.insertXMP(pushbackStream, out, xmp);
				break;
			case GIF:
				GIFMeta.insertXMPApplicationBlock(pushbackStream, out, xmp);
				break;
			case PCX:
			case TGA:
			case BMP:
				LOGGER.info("{} image format does not support XMP data", imageType);
				break;
			default:
				pushbackStream.close();
				throw new IllegalArgumentException("XMP inserting is not supported for " + imageType + " image");				
		}		
	}
	
	public static Map<MetadataType, Metadata> readMetadata(File image) throws IOException {
		FileInputStream fin = new FileInputStream(image);
		Map<MetadataType, Metadata> metadataMap = readMetadata(fin);
		fin.close();
		
		return metadataMap; 
	}
	
	/**
	 * Reads all metadata associated with the input image
	 *
	 * @param is InputStream for the image
	 * @return a list of Metadata for the input stream
	 * @throws IOException
	 */
	public static Map<MetadataType, Metadata> readMetadata(InputStream is) throws IOException {
		// Metadata map for all the Metadata read
		Map<MetadataType, Metadata> metadataMap = new HashMap<MetadataType, Metadata>();
		// ImageIO.IMAGE_MAGIC_NUMBER_LEN bytes as image magic number
		PushbackInputStream pushbackStream = new PushbackInputStream(is, IMAGE_MAGIC_NUMBER_LEN);
		ImageType imageType = MetadataUtils.guessImageType(pushbackStream);		
		// Delegate metadata reading to corresponding image tweakers.
		switch(imageType) {
			case JPG:
				metadataMap = JPEGMeta.readMetadata(pushbackStream);
				break;
			case TIFF:
				RandomAccessInputStream randIS = new FileCacheRandomAccessInputStream(pushbackStream);
				metadataMap = TIFFMeta.readMetadata(randIS);
				randIS.close();
				break;
			case PNG:
				metadataMap = PNGMeta.readMetadata(pushbackStream);
				break;
			case GIF:
				metadataMap = GIFMeta.readMetadata(pushbackStream);
				break;
			case BMP:
				metadataMap = BMPMeta.readMetadata(pushbackStream);
				break;
			default:
				pushbackStream.close();
				throw new IllegalArgumentException("Metadata reading is not supported for " + imageType + " image");
				
		}	
		pushbackStream.close();
		
		return metadataMap;
	}
	
	public static Map<MetadataType, Metadata> readMetadata(String image) throws IOException {
		return readMetadata(new File(image));
	}
	
	/**
	 * Remove meta data from image
	 * 
	 * @param is InputStream for the input image
	 * @param os OutputStream for the output image
	 * @throws IOException
	 */
	public static void removeMetadata(InputStream is, OutputStream os, MetadataType ...metadataTypes) throws IOException {
		// ImageIO.IMAGE_MAGIC_NUMBER_LEN bytes as image magic number
		PushbackInputStream pushbackStream = new PushbackInputStream(is, IMAGE_MAGIC_NUMBER_LEN);
		ImageType imageType = MetadataUtils.guessImageType(pushbackStream);		
		// Delegate meta data removing to corresponding image tweaker.
		switch(imageType) {
			case JPG:
				JPEGMeta.removeMetadata(pushbackStream, os, metadataTypes);
				break;
			case TIFF:
				RandomAccessInputStream randIS = new FileCacheRandomAccessInputStream(pushbackStream);
				RandomAccessOutputStream randOS = new FileCacheRandomAccessOutputStream(os);
				TIFFMeta.removeMetadata(randIS, randOS, metadataTypes);
				randIS.close();
				randOS.close();
				break;
			case PCX:
			case TGA:
			case BMP:
				LOGGER.info("{} image format does not support meta data", imageType);
				break;
			default:
				pushbackStream.close();
				throw new IllegalArgumentException("Metadata removing is not supported for " + imageType + " image");				
		}
	}
	
	public Metadata(MetadataType type, byte[] data) {
		this.type = type;
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public abstract MetadataReader getReader();
	
	public MetadataType getType() {
		return type;
	}
	
	public void showMetadata() {
		MetadataReader reader = getReader();
		if(reader != null)
			reader.showMetadata();
	}
	
	/**
	 * Writes the metadata out to the output stream
	 * 
	 * @param out OutputStream to write the metadata to
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException {
		out.write(data);
	}	
}
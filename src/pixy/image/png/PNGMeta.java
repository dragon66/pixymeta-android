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
 * PNGMeta.java
 *
 * Who   Date       Description
 * ====  =========  =================================================
 * WY    13Mar2015  Initial creation
 */

package pixy.image.png;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.zip.InflaterInputStream;

import org.w3c.dom.Document;

import pixy.meta.Metadata;
import pixy.meta.MetadataType;
import pixy.meta.adobe.XMP;
import pixy.meta.icc.ICCProfile;
import pixy.image.png.Chunk;
import pixy.image.png.ChunkType;
import pixy.image.png.TextBuilder;
import pixy.image.png.TextReader;
import pixy.image.png.UnknownChunk;
import pixy.io.IOUtils;
import pixy.string.XMLUtils;
/**
 * PNG image tweaking tool
 *
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 10/18/2012
 */
public class PNGMeta {
	
	/** PNG signature constant */
    private static final long SIGNATURE = 0x89504E470D0A1A0AL;
	
   	public static void insertChunk(Chunk customChunk, InputStream is, OutputStream os) throws IOException
  	{
  		insertChunks(new Chunk[]{customChunk}, is, os);
  	}
  	
  	public static void insertChunks(Chunk[] chunks, InputStream is, OutputStream os) throws IOException
  	{
  		List<Chunk> list = readChunks(is);  		
        Collections.addAll(list, chunks);
    	
  		IOUtils.writeLongMM(os, SIGNATURE);
	
        serializeChunks(list, os);
  	}
  	
  	public static void insertChunks(List<Chunk> chunks, InputStream is, OutputStream os) throws IOException
  	{
  		List<Chunk> list = readChunks(is);  		
        list.addAll(chunks);
    	
  		IOUtils.writeLongMM(os, SIGNATURE);
	
        serializeChunks(list, os);
  	}
  	
  	public static void insertXMP(InputStream is, OutputStream os, String xmp) throws IOException {
  		Document doc = XMLUtils.createXML(xmp);
		XMLUtils.insertLeadingPI(doc, "xpacket", "begin='' id='W5M0MpCehiHzreSzNTczkc9d'");
		XMLUtils.insertTrailingPI(doc, "xpacket", "end='w'");
		String newXmp = XMLUtils.serializeToString(doc); // DONOT use XMLUtils.serializeToStringLS()
  		// Adds XMP chunk
		TextBuilder xmpBuilder = new TextBuilder(ChunkType.ITXT).keyword("XML:com.adobe.xmp");
		xmpBuilder.text(newXmp);
	    Chunk xmpChunk = xmpBuilder.build();
	    
	    insertChunk(xmpChunk, is, os);
    }
  	
   	public static byte[] readICCProfile(byte[] buf) throws IOException {
  		 int profileName_len = 0;
		 while(buf[profileName_len] != 0) profileName_len++;
 		 String profileName = new String(buf, 0, profileName_len,"UTF-8");
 		
 		 InflaterInputStream ii = new InflaterInputStream(new ByteArrayInputStream(buf, profileName_len + 2, buf.length - profileName_len - 2));
 		 System.out.println("ICCProfile name: " + profileName);
 		 
 		 byte[] icc_profile = IOUtils.readFully(ii, 4096);
 		 System.out.println("ICCProfile length: " + icc_profile.length);
 	 		 
 		 return icc_profile;
  	}
  	
  	public static List<Chunk> readChunks(InputStream is) throws IOException {  		
  		List<Chunk> list = new ArrayList<Chunk>();
 		 //Local variables for reading chunks
        int data_len = 0;
        int chunk_type = 0;
        byte[] buf = null;
     
        long signature = IOUtils.readLongMM(is);

        if (signature != SIGNATURE)
        {
       	 	throw new RuntimeException("--- NOT A PNG IMAGE ---");
        }   

        /** Read header */
        /** We are expecting IHDR */
        if ((IOUtils.readIntMM(is)!=13)||(IOUtils.readIntMM(is) != ChunkType.IHDR.getValue()))
        {
            throw new RuntimeException("Not a valid IHDR chunk.");
        }     
        
        buf = new byte[13];
        IOUtils.read(is, buf, 0, 13);
  
        list.add(new Chunk(ChunkType.IHDR, 13, buf, IOUtils.readUnsignedIntMM(is)));         
      
        while (true)
        {
        	data_len = IOUtils.readIntMM(is);
	       	chunk_type = IOUtils.readIntMM(is);
	   
	       	if (chunk_type == ChunkType.IEND.getValue()) {
	        	 list.add(new Chunk(ChunkType.IEND, data_len, new byte[0], IOUtils.readUnsignedIntMM(is)));
	       		 break;
	       	} 
       		ChunkType chunkType = ChunkType.fromInt(chunk_type);
       		buf = new byte[data_len];
       		IOUtils.read(is, buf,0, data_len);
              
       		if (chunkType == ChunkType.UNKNOWN)
       			list.add(new UnknownChunk(data_len, chunk_type, buf, IOUtils.readUnsignedIntMM(is)));
       		else
       			list.add(new Chunk(chunkType, data_len, buf, IOUtils.readUnsignedIntMM(is)));
        }
        
        return list;
  	}
  	
	public static Map<MetadataType, Metadata> readMetadata(InputStream is) throws IOException {
		Map<MetadataType, Metadata> metadataMap = new HashMap<MetadataType, Metadata>();
		List<Chunk> chunks = PNGMeta.readChunks(is);
		Iterator<Chunk> iter = chunks.iterator();
		
		while (iter.hasNext()) {
			Chunk chunk = iter.next();
			ChunkType type = chunk.getChunkType();
			long length = chunk.getLength();
			if(type == ChunkType.ICCP)
				metadataMap.put(MetadataType.ICC_PROFILE, new ICCProfile(readICCProfile(chunk.getData())));
			if(type == ChunkType.ITXT) {// We may find XMP data inside here
				TextReader reader = new TextReader(chunk);
				if(reader.getKeyword().equals("XML:com.adobe.xmp")); // We found XMP data
	   				metadataMap.put(MetadataType.XMP, new XMP(reader.getText()));
	   		}
			System.out.print(type.getName() + " (" + type.getAttribute() + ")");
			System.out.print(" | " + length + " bytes");
			System.out.println(" | " + "0x" + Long.toHexString(chunk.getCRC()) + " (CRC)");
		}
		
		is.close();
		
		return metadataMap;
	}
  	
	public static List<Chunk> removeChunks(List<Chunk> chunks, ChunkType chunkType) {
  		
  		Iterator<Chunk> iter = chunks.listIterator();
   	
   		while(iter.hasNext()) {
   			
   			Chunk chunk = iter.next();
   		
   			if (chunk.getChunkType() == chunkType) {   				
   				iter.remove();
   			}   			
   		}
   		
   		return chunks;  		
  	}
   	
   	/**
   	 * Removes chunks which have the same ChunkType values from the chunkEnumSet.
   	 * 
   	 * @param chunks a list of chunks to be checked.
   	 * @param chunkEnumSet a set of ChunkType (better use a HashSet instead of EnumSet for performance).
   	 * @return a list of chunks with the specified chunks removed if any.
   	 */
   	
   	public static List<Chunk> removeChunks(List<Chunk> chunks, Set<ChunkType> chunkEnumSet) {
  		
  		Iterator<Chunk> iter = chunks.listIterator();
   	
   		while(iter.hasNext()) {
   			
   			Chunk chunk = iter.next();
   		
   			if (chunkEnumSet.contains(chunk.getChunkType())) {   				
   				iter.remove();
   			}   			
   		}
   		
   		return chunks;  		
  	}
  	
   	public static void serializeChunks(List<Chunk> chunks, OutputStream os) throws IOException {
  		
  		Collections.sort(chunks);
  	    
  		for(Chunk chunk : chunks) {
        	chunk.write(os);
        }
  	}
  	
  	private PNGMeta() {}
}

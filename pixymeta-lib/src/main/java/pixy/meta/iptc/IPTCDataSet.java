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
 * IPTCDataSet.java
 *
 * Who   Date       Description
 * ====  =========  =================================================
 * WY    16Jul2015  Added two new constructors for IPTCApplicationTag
 * WY    13Mar2015  Initial creation
 */

package pixy.meta.iptc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pixy.meta.iptc.IPTCApplicationTag;
import pixy.meta.iptc.IPTCDataSet;
import pixy.meta.iptc.IPTCEnvelopeTag;
import pixy.meta.iptc.IPTCFotoStationTag;
import pixy.meta.iptc.IPTCNewsPhotoTag;
import pixy.meta.iptc.IPTCObjectDataTag;
import pixy.meta.iptc.IPTCPostObjectDataTag;
import pixy.meta.iptc.IPTCPreObjectDataTag;
import pixy.meta.iptc.IPTCRecord;
import pixy.meta.iptc.IPTCTag;
import pixy.io.IOUtils;
import pixy.string.StringUtils;
import pixy.util.ArrayUtils;

/**
 * International Press Telecommunications Council (IPTC) data set
 * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 06/10/2013
 */
public class IPTCDataSet {
	// Fields
	private int recordNumber; // Corresponds to IPTCRecord enumeration recordNumber 
	private int tag; // Corresponds to IPTC tag enumeration tag field
	private int size;
	private byte[] data;
	private int offset;
	private IPTCTag tagEnum;
	
	// A unique name used as HashMap key
	private String name;
	
	// Obtain a logger instance
	private static final Logger LOGGER = LoggerFactory.getLogger(IPTCDataSet.class);
	
	public IPTCDataSet(int tag, byte[] data) {
		this(IPTCRecord.APPLICATION, tag, data);
	}
	
	public IPTCDataSet(int recordNumber, int tag, int size, byte[] data, int offset) {
		this.recordNumber = recordNumber;
		this.tag = tag;
		this.size = size;
		this.data = data;
		this.offset = offset;		
		this.name = getTagName();
	}
	
	public IPTCDataSet(int tag, String value) {
		this(tag, value.getBytes());
	}
	
	public IPTCDataSet(IPTCApplicationTag appTag, byte[] data) {
		this(appTag.getTag(), data);
	}
	
	public IPTCDataSet(IPTCApplicationTag appTag, String value) {
		this(appTag.getTag(), value);
	}
	
	public IPTCDataSet(IPTCRecord record, int tag, byte[] data) {
		this(record.getRecordNumber(), tag, data.length, data, 0);
	}
	
	public IPTCDataSet(IPTCRecord record, int tag, String value) {
		this(record, tag, value.getBytes());
	}
	
	public boolean allowMultiple() {
		return tagEnum.allowMultiple();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IPTCDataSet other = (IPTCDataSet) obj;
		byte[] thisData = ArrayUtils.subArray(data, offset, size);
		byte[] thatData = ArrayUtils.subArray(other.data, other.offset, other.size);
		if (!Arrays.equals(thisData, thatData))
			return false;
		if (recordNumber != other.recordNumber)
			return false;
		if (tag != other.tag)
			return false;
		return true;
	}
	
	public byte[] getData() {
		return ArrayUtils.subArray(data, offset, size);
	}
	
	public String getDataAsString() {
		return tagEnum.getDataAsString(getData());
	}
	
	public String getName() {
		return name;
	}
	
	public int getRecordNumber() {
		return recordNumber;
	}

	public int getSize() {
		return size;
	}
	
	public int getTag() {
		return tag;
	}
	
	public IPTCTag getTagEnum() {
		return tagEnum;
	}
	
	private String getTagName() {
		switch(IPTCRecord.fromRecordNumber(recordNumber)) {
			case APPLICATION:
				tagEnum = IPTCApplicationTag.fromTag(tag);
				break;
			case ENVELOP:
				tagEnum = IPTCEnvelopeTag.fromTag(tag);
				break;
			case FOTOSTATION:
				tagEnum = IPTCFotoStationTag.fromTag(tag);
				break;
			case NEWSPHOTO:
				tagEnum = IPTCNewsPhotoTag.fromTag(tag);
				break;
			case OBJECTDATA:
				tagEnum = IPTCObjectDataTag.fromTag(tag);
				break;
			case POST_OBJECTDATA:
				tagEnum = IPTCPostObjectDataTag.fromTag(tag);
				break;
			case PRE_OBJECTDATA:
				tagEnum = IPTCPreObjectDataTag.fromTag(tag);
				break;
			default:
				tagEnum = IPTCApplicationTag.UNKNOWN;
		}
		
		return tagEnum.getName();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(ArrayUtils.subArray(data, offset, size));
		result = prime * result + recordNumber;
		result = prime * result + tag;
		return result;
	}
	
	public void print() {
		
		switch (recordNumber) {
			case 1: //Envelope Record
				LOGGER.info("Record number {}: Envelope Record", recordNumber);
				break;
			case 2: //Application Record
				LOGGER.info("Record number {}: Application Record", recordNumber);
				break;
			case 3: //NewsPhoto Record
				LOGGER.info("Record number {}: NewsPhoto Record", recordNumber);
				break;
			case 7: //PreObjectData Record
				LOGGER.info("Record number {}: PreObjectData Record", recordNumber);
				break;
			case 8: //ObjectData Record
				LOGGER.info("Record number {}: ObjectData Record", recordNumber);
				break;				
			case 9: //PostObjectData Record
				LOGGER.info("Record number {}: PostObjectData Record", recordNumber);
				break;	
			case 240: //FotoStation Record
				LOGGER.info("Record number {}: FotoStation Record", recordNumber);
				break;	
			default:
				LOGGER.info("Record number {}: Unknown Record", recordNumber);
				break;
		}		
		
		LOGGER.info("Dataset name: {}", name);
		LOGGER.info("Dataset tag: {}[{}]", tag, StringUtils.shortToHexStringMM((short)tag));
		LOGGER.info("Dataset size: {}", size);
		
		LOGGER.info("Dataset value: {}", getDataAsString());
	}
	
	/**
	 * Write the current IPTCDataSet to the OutputStream
	 * 
	 * @param out OutputStream to write the IPTCDataSet
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException {
		out.write(0x1c); // tag marker
		out.write(recordNumber);
		out.write(getTag());
		IOUtils.writeShortMM(out, size);
		out.write(data, offset, size);
	}
}
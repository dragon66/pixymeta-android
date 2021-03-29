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

package pixy.image.tiff;

import pixy.string.StringUtils;

/**
 * TIFF SShort type field.
 * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 02/24/2013
 */
public final class SShortField extends AbstractShortField {

	public SShortField(short tag, short[] data) {
		super(tag, FieldType.SSHORT, data);	
	}
	
	public int[] getDataAsLong() {
		//
		int[] temp = new int[data.length];
		
		for(int i=0; i<data.length; i++) {
			temp[i] = data[i];
		}
		
		return temp;
	}
	
	public String getDataAsString() {
		return StringUtils.shortArrayToString(data, 0, MAX_STRING_REPR_LEN, false);
	}
}

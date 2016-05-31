/**
 * Copyright (c) 2014-2016 by Wen Yu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package pixy.image;

import java.util.Map;
import java.util.HashMap;

/**
 * Image types supported by ImageReader and ImageWriter.
 * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 10/09/2012
 */
public enum ImageType {
	
	GIF("Gif") { 
		@Override
		public String getExtension() {
			return "gif";
		}		
	},
	
    PNG("Png") { 
		@Override
		public String getExtension() {
			return "png";
		}
	},
	
    JPG("Jpeg") { 
		@Override
		public String getExtension() {
			return "jpg";
		}
	},
	
	JPG2000("Jpeg2000") {
		@Override
		public String getExtension() {
			return "jp2";
		}
	},
	
    BMP("Bitmap") { 
		@Override
		public String getExtension() {
			return "bmp";
		}
	},
	
    TGA("Targa") { 
		@Override
		public String getExtension() {
			return "tga";
		}
	},
	
	TIFF("Tiff") { 
		@Override
		public String getExtension() {
			return "tif";
		}
	},
	
    PCX("Pcx") { 
		@Override
		public String getExtension() {
			return "pcx";
		}
	},
	
	UNKNOWN("Unknown") {
		@Override
		public String getExtension() {
			return null;
		}
	};
    
    private static final Map<String, ImageType> stringMap = new HashMap<String, ImageType>();
   
    static
    {
      for(ImageType type : values())
          stringMap.put(type.toString(), type);
    }
   
    public static ImageType fromString(String name)
    {
      return stringMap.get(name);
    }
   
    private final String name;
   
    private ImageType(String name)
    {
      this.name = name;
    }
    
    public abstract String getExtension();

    @Override
    public String toString()
    {
      return name;
    }
}

package pixy.meta.image;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import pixy.meta.NativeMetadata;
import pixy.image.jpeg.JPEGMeta;
import pixy.image.jpeg.Segment;
import pixy.io.IOUtils;
import pixy.string.StringUtils;
import pixy.util.ArrayUtils;

/**
 * JPEG native image metadata
 * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 03/13/2015
 */
public class JPGNativeMetadata extends NativeMetadata<Segment> {
	
	public JPGNativeMetadata() {
		;
	}

	public JPGNativeMetadata(List<Segment> segments) {
		super(segments);
	}
	
	@Override
	public String getMimeType() {
		return "image/jpg";
	}

	@Override
	public void showMetadata() {
		List<Segment> metadataList = getMetadataList();
		try {
			for(Segment segment : metadataList) {
				byte[] data = segment.getData();
				switch(segment.getMarker()) {
					case APP0:
						readAPP0(data);
						break;
					case APP12:
						readAPP12(data);
						break;
					case APP14:
						readAPP14(data);
						break;
					default:						
				}
			}
		} catch (IOException e) {
			;
		}
	}
	
	private static void readAPP14(byte[] data) throws IOException {	
		String[] app14Info = {"DCTEncodeVersion: ", "APP14Flags0: ", "APP14Flags1: ", "ColorTransform: "};		
		int expectedLen = 12; // Expected length of this segment is 12.
		if (data.length >= expectedLen) { 
			byte[] buf = ArrayUtils.subArray(data, 0, 5);
			
			if(Arrays.equals(buf, JPEGMeta.ADOBE_ID)) {
				for (int i = 0, j = 5; i < 3; i++, j += 2) {
					System.out.println(app14Info[i] + StringUtils.shortToHexStringMM(IOUtils.readShortMM(data, j)));
				}
				System.out.println(app14Info[3] + (((data[11]&0xff) == 0)? "Unknown (RGB or CMYK)":
					((data[11]&0xff) == 1)? "YCbCr":"YCCK" ));
			}
		}
	}
	
	private static void readAPP0(byte[] data) throws IOException {
		int i = JPEGMeta.JFIF_ID.length;
	    // JFIF segment
	    if(Arrays.equals(ArrayUtils.subArray(data, 0, i), JPEGMeta.JFIF_ID) || Arrays.equals(ArrayUtils.subArray(data, 0, i), JPEGMeta.JFXX_ID)) {
	    	System.out.print(new String(data, 0, i).trim());
	    	System.out.println(" - version " + (data[i++]&0xff) + "." + (data[i++]&0xff));
	    	System.out.print("Density unit: ");
	    	
	    	switch(data[i++]&0xff) {
	    		case 0:
	    			System.out.println("No units, aspect ratio only specified");
	    			break;
	    		case 1:
	    			System.out.println("Dots per inch");
	    			break;
	    		case 2:
	    			System.out.println("Dots per centimeter");
	    			break;
	    		default:
	    	}
	    	
	    	System.out.println("X density: " + IOUtils.readUnsignedShortMM(data, i));
	    	i += 2;
	    	System.out.println("Y density: " + IOUtils.readUnsignedShortMM(data, i));
	    	i += 2;
	    	int thumbnailWidth = data[i++]&0xff;
	    	int thumbnailHeight = data[i++]&0xff;
	    	System.out.println("Thumbnail dimension: " + thumbnailWidth + "X" + thumbnailHeight);	   
	    }
	}
	
	private static void readAPP12(byte[] data) throws IOException {
		// APP12 is either used by some old cameras to set PictureInfo
		// or Adobe PhotoShop to store Save for Web data - called Ducky segment.
		String[] duckyInfo = {"Ducky", "Photoshop Save For Web Quality: ", "Comment: ", "Copyright: "};
		int currPos = 0;
		byte[] buf = ArrayUtils.subArray(data, 0, JPEGMeta.DUCKY_ID.length);
		currPos += JPEGMeta.DUCKY_ID.length;
		
		if(Arrays.equals(JPEGMeta.DUCKY_ID, buf)) {
			System.out.println("=>" + duckyInfo[0]);
			short tag = IOUtils.readShortMM(data, currPos);
			currPos += 2;
			
			while (tag != 0x0000) {
				System.out.println("Tag value: " + StringUtils.shortToHexStringMM(tag));
				
				int len = IOUtils.readUnsignedShortMM(data, currPos);
				currPos += 2;
				System.out.println("Tag length: " + len);
				
				switch (tag) {
					case 0x0001: // Image quality
						System.out.print(duckyInfo[1]);
						System.out.println(IOUtils.readUnsignedIntMM(data, currPos));
						currPos += 4;
						break;
					case 0x0002: // Comment
						System.out.print(duckyInfo[2]);
						System.out.println(new String(data, currPos, currPos + len).trim());
						currPos += len;
						break;
					case 0x0003: // Copyright
						System.out.print(duckyInfo[3]);
						System.out.println(new String(data, currPos, currPos + len).trim());
						currPos += len;
						break;
					default: // Do nothing!					
				}
				
				tag = IOUtils.readShortMM(data, currPos);
				currPos += 2;
			}			
		} else {
			buf = ArrayUtils.subArray(data, 0, 10);
			if (Arrays.equals(JPEGMeta.PICTURE_INFO_ID, buf)) {
				// TODO process PictureInfo.
			}
		}
	}
}
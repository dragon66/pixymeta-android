package pixy.meta.image;

import java.util.List;

import pixy.meta.NativeMetadata;
import pixy.image.png.Chunk;

/**
 * PNG native image metadata
 * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 03/13/2015
 */
public class PNGNativeMetadata extends NativeMetadata<Chunk> {
	
	public PNGNativeMetadata() {
		;
	}

	public PNGNativeMetadata(List<Chunk> chunks) {
		super(chunks);
	}
	
	@Override
	public String getMimeType() {
		return "image/png";
	}

	@Override
	public void showMetadata() {
		;
	}
}
package pixy.meta.image;

import java.util.List;

import pixy.image.gif.ApplicationExtension;
import pixy.meta.NativeMetadata;

/**
 * GIF native image metadata
 * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 03/13/2015
 */
public class GIFNativeMetadata extends NativeMetadata<ApplicationExtension> {
	
	public GIFNativeMetadata() {
		;
	}

	public GIFNativeMetadata(List<ApplicationExtension> applications) {
		super(applications);
	}
	
	@Override
	public String getMimeType() {
		return "image/gif";
	}

	@Override
	public void showMetadata() {
		;
	}
}
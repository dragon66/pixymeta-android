package pixy.meta.image;

import java.io.UnsupportedEncodingException;

import pixy.meta.Metadata;
import pixy.meta.MetadataReader;
import pixy.meta.MetadataType;

public class Comment extends Metadata {
	private String comment;
	
	public Comment(byte[] data) {
		super(MetadataType.COMMENT, data);
		try {
			this.comment = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public String getComment() {
		return comment;
	}
	
	public void showMetadata() {
		System.out.println("Comment: " + comment);
	}

	@Override
	public MetadataReader getReader() {
		return null;
	}
}
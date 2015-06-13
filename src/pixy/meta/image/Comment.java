package pixy.meta.image;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pixy.meta.Metadata;
import pixy.meta.MetadataReader;
import pixy.meta.MetadataType;

public class Comment extends Metadata {
	private String comment;
	
	// Obtain a logger instance
	private static final Logger LOGGER = LoggerFactory.getLogger(Comment.class);
	
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
		LOGGER.info("Comment: {}", comment);
	}

	@Override
	public MetadataReader getReader() {
		return null;
	}
}
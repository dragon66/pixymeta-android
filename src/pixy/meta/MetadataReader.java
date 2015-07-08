package pixy.meta;

import pixy.util.Reader;

public interface MetadataReader extends Reader {
	public void showMetadata();
	public boolean isDataRead();
}
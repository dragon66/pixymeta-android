package pixy.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class NativeMetadata<T> {
	private List<T> metadataList;
	
	public NativeMetadata() {
		;
	}
	
	public NativeMetadata(List<T> meta) {
		metadataList = meta;
	}
	
	public void addMeta(T meta) {
		if(metadataList == null)
			metadataList = new ArrayList<T>();
		metadataList.add(meta);
	}
	
	public List<T> getMetadataList() {
		return Collections.unmodifiableList(metadataList);
	}
	
	public abstract String getMimeType();
	public abstract void showMetadata();	
}
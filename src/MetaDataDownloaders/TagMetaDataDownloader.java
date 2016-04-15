package MetaDataDownloaders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JTextArea;
import org.jinstagram.Instagram;

public class TagMetaDataDownloader extends MetaDataDownloader {

	int numTags;
	
	public TagMetaDataDownloader(Instagram instagram, int numImages, 
			JTextArea textArea, String tag) {
		super(instagram, numImages, textArea, null);
		//From line 649 from Instagram.java
		methodChain[0] = "getRecentMediaTags";
		methodChain[1] = "getData";
	
		methodArgument[0][0] = tag;
		
		filename = "tags_metadata";
	}

	@Override
	protected Object getMethodChainWithArguments(Object o, int objNum) {
		Method m;
		
		try {
			switch (objNum) {
				case 0:
					m = instagram.getClass().getDeclaredMethod(methodChain[0], String.class);
					return (Object) m.invoke(instagram, methodArgument[0][0]);
				case 1:
					m = o.getClass().getMethod(methodChain[objNum]);
					return (Object) m.invoke(o);
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
			
		return null;
	}
}

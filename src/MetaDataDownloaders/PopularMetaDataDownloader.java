package MetaDataDownloaders;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JTextArea;

import org.jinstagram.Instagram;

public class PopularMetaDataDownloader extends MetaDataDownloader {
	
	/** Depending on what data you are querying, the jInstagram sequence
	 * of methods will vary. For example, when querying for popular media
	 * the call is to instagram.getPopularMedia().getData();, but this will
	 * vary for different types of queries.
	 * 
	 * To handle this, there is an array that you should use to store the
	 * name of each method in the chain in each element. Start at the first
	 * method (put that string in element 0) and put each method name in
	 * the next element in order. The program will then traverse this chain,
	 * calling each method in sequence to return your list of metadata.
	 * 
	 * @param i - the Instagram object (a singleton)
	 * @param e - the ExecutorService object (a singleton)
	 * @param n - the number of images for which metadata is requested
	 */
	public PopularMetaDataDownloader(Instagram instagram, 
			int numImages, JTextArea textArea) {
		
		super(instagram, numImages, textArea, null);
		
		methodChain[0] = "getPopularMedia";
		methodChain[1] = "getData";
		
		filename = "popular_metadata";
	}
	
	@Override
	protected Object getMethodChainWithArguments(Object o, int objNum) {
		Method m;
		
		try {
			switch (objNum) {
				case 0:
					m = instagram.getClass().getMethod(methodChain[0]);
					return (Object) m.invoke(instagram);
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

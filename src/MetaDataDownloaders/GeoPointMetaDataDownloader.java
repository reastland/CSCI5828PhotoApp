package MetaDataDownloaders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JTextArea;

import org.jinstagram.Instagram;

public class GeoPointMetaDataDownloader extends MetaDataDownloader {

	double[] geoPoint;
	
	public GeoPointMetaDataDownloader(Instagram instagram, int numImages, 
			JTextArea textArea, double[] point) {
		
		super(instagram, numImages, textArea, null);
		
		methodChain[0] = "searchMedia";
		methodChain[1] = "getData";
		
		geoPoint = point;
				
		filename = "geopoint_metadata";
	}
	
	@Override
	protected Object getMethodChainWithArguments(Object o, int objNum) {
		Method m;
		
		try {
			switch (objNum) {
				case 0:
					m = instagram.getClass().getDeclaredMethod(methodChain[0], double.class, double.class);
					return (Object) m.invoke(instagram, geoPoint[0], geoPoint[1]);
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

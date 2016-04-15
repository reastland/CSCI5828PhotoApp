package MetaDataDownloaders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;//Needed to send a formatted date
import javax.swing.JTextArea;
import org.jinstagram.Instagram;

public class DateByLocationMetaDataDownloader extends MetaDataDownloader {
	
	//Class variables
    int [] IDs;
    Date [] dates;
	
	public DateByLocationMetaDataDownloader(Instagram instagram, int numImages, 
			JTextArea textArea, String location, int minID, int maxID, Date dateMax, Date dateMin) {
		
		super(instagram, numImages, textArea, null);
		
		//This function invokes the MediaFeed search from line 777 in the instagram.java api		
		methodChain[0] = "getRecentMediaByLocation";
		methodChain[1] = "getData";
																				
		//We are presumably forced to use the depricated java.util.Date library since the Instagram function uses Date objects
		//Therefore we will likely need to set the date with the Calendar class and adjust the time based on that value like below:
				
		//Store values in appropriate arrays
		IDs = new int [2];
		dates = new Date [2];
		
		IDs[0] = 0;
		IDs[1] = 0;
		dates[0] = dateMax;//dateMax: Return data before this date
		dates[1] = dateMin;//dateMin: Return data after this date	
		
		methodArgument[0][0] = location;//Store the location string id		
		
		filename = "dateLocation_metadata";
}
	
	@Override
	protected Object getMethodChainWithArguments(Object o, int objNum) {
		Method m;
		
		try {
            switch (objNum) {
				case 0:
					m = instagram.getClass().getDeclaredMethod(methodChain[0], String.class, int.class, int.class, Date.class, Date.class);
					return (Object) m.invoke(instagram, methodArgument[0][0], IDs[0], IDs[1], dates[0], dates[1]);
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

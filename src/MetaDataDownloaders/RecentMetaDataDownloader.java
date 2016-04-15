package MetaDataDownloaders;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.JTextArea;

import org.jinstagram.Instagram;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.locations.LocationSearchFeed;
import org.jinstagram.exceptions.InstagramException;

public class RecentMetaDataDownloader extends MetaDataDownloader {
	
	private double latitude;
	private double longitude;

	public RecentMetaDataDownloader(Instagram instagram, int numImages, JTextArea textArea) {
		super(instagram, numImages, textArea, null);
		
		// The instagram API only allows recent media to be queried by location or by user, so we will use location
		// This location is New York City
		latitude = 40.7127;
		longitude = 74.0059;
		
		// Gets all related locations
		LocationSearchFeed locationSearchFeed;
		try {
			locationSearchFeed = instagram.searchLocation(latitude, longitude);
			
			// If locations returned
			List<Location> locationList = locationSearchFeed.getLocationList();
			if (locationList.size() > 0) {
				// Grab first location
				Location firstLocation = locationList.get(0);
				
				// Get a list of recent media object from a given location
				methodChain[0] = "getRecentMediaByLocation";
				methodChain[1] = "getData";
				methodArgument[0][0] = firstLocation.getId();
				methodArgument[1][0] = "empty";
				System.out.println("Location ID for recent: " + firstLocation.getId());
			} else {
				// No locations returned
				methodChain[0] = "empty";
    			methodChain[1] = "empty";
    			methodArgument[0][0] = "empty";
    			methodArgument[1][0] = "empty";
    			System.out.println("No locations returned.");
			}
		} catch (InstagramException e) {
			methodChain[0] = "empty";
			methodChain[1] = "empty";
			methodArgument[0][0] = "empty";
			methodArgument[1][0] = "empty";
			System.out.println("Photographer instagram exception." + e.getMessage());
			System.out.println(e.getStackTrace());
		}
		
		filename = "recent_metadata";
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

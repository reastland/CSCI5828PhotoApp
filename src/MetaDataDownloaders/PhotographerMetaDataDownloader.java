package MetaDataDownloaders;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.JTextArea;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

public class PhotographerMetaDataDownloader extends MetaDataDownloader {
	
//	private String photographerId;

	public PhotographerMetaDataDownloader(Instagram instagram, int numImages,
			JTextArea textArea, String photographer) {
		super(instagram, numImages, textArea, null);
		
		UserFeed userFeed;
		try {
			userFeed = instagram.searchUser(photographer);
    		List<UserFeedData> userList = userFeed.getUserList();
    		
    		if (userList.size() == 1) {
    			for (UserFeedData user : userList) {
    				methodChain[0] = "getRecentMediaFeed";
    				methodChain[1] = "getData";
    				methodArgument[0][0] = user.getId();
    				methodArgument[1][0] = "empty";
    				String output = "Photographer user ID: " + user.getId();
    				System.out.println(output);
    				textArea.append(output);
        		}
    		} else if (userList.size() > 1) {
    			String output = "Too many photographers part of search.";
    			System.out.println(output);
    			textArea.append(output + "\nTerminating Request\n\n");
    			subclassConstructorCompletedSuccessfully = false;
    		} else {
    			String output = "No photographers returned.";
    			System.out.println(output);
    			textArea.append(output + "\nTerminating Request\n\n");
    			subclassConstructorCompletedSuccessfully = false;
    		}
		} catch (InstagramException e) {
			System.out.println("Photographer instagram exception." + e.getMessage());
			System.out.println(e.getStackTrace());
			subclassConstructorCompletedSuccessfully = false;
		}
		
		filename = "photographer_metadata";
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

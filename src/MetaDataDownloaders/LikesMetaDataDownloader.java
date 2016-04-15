package MetaDataDownloaders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JTextArea;

import org.jinstagram.Instagram;

public class LikesMetaDataDownloader extends MetaDataDownloader {
	
	String photographer;
	
	public LikesMetaDataDownloader(Instagram instagram, int numImages, 
			JTextArea textArea, String verificationCode) {
		
		super(instagram, numImages, textArea, null);
		
//		Verifier verifier = new Verifier(verificationCode);
//		Token accessToken = tokenizer.getService().getAccessToken(null, verifier);
		
		
		
		
		
//		methodChain[0] = "getUserLikedMediaFeed";
//		methodChain[1] = "getData";
//		
//		this.photographer = photographer;
//		UserInfo userInfo;
////		UserFeed userFeed;
//		try {
//			userInfo = instagram.getUserInfo("18808100");
////			userFeed = instagram.searchUser(photographer);
//    		String userId = userInfo.getData().getId();
//    		
//    		if (userList.size() == 1) {
//    			for (UserFeedData user : userList) {
//    				methodChain[0] = "getRecentMediaFeed";
//    				methodChain[1] = "getData";
//    				methodArgument[0][0] = user.getId();
//    				methodArgument[1][0] = "empty";
//    				String output = "Photographer user ID: " + user.getId();
//    				System.out.println(output);
//    				textArea.append(output);
//        		}
//    		} else if (userList.size() > 1) {
//    			String output = "Too many photographers part of search.";
//    			System.out.println(output);
//    			textArea.append(output + "\nTerminating Request\n\n");
//    			subclassConstructorCompletedSuccessfully = false;
//    		} else {
//    			String output = "No photographers returned.";
//    			System.out.println(output);
//    			textArea.append(output + "\nTerminating Request\n\n");
//    			subclassConstructorCompletedSuccessfully = false;
//    		}
//		} catch (InstagramException e) {
//			System.out.println("Photographer instagram exception." + e.getMessage());
//			System.out.println(e.getStackTrace());
//			subclassConstructorCompletedSuccessfully = false;
//		}
		
		
		
		
		
		
		
		
		
		
		
		
		filename = "phototitle_metadata";
	}
	
	@Override
	protected Object getMethodChainWithArguments(Object o, int objNum) {
		Method m;
		System.out.println(objNum);
		try {
			switch (objNum) {
				case 0:
//					m = instagram.getClass().getDeclaredMethod(methodChain[0], String.class);
//					return (Object) m.invoke(instagram, photographer);
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

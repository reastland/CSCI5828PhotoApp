import org.jinstagram.Instagram;

public class PhotoApp {

	public static void main(String[] args) {

		final String CLIENT_ID = "b8047a90923847448dd31dd1b9bd7e29";
//		final String CLIENT_SECRET = "fc3e6f3b67b6461a8b2d8ca250f1cf9c";
//		final String CALLBACK_URL = "http://www.eastland.name"; 
 
		Instagram instagram = new Instagram(CLIENT_ID);

		new GUI(instagram);	
		
	}
}

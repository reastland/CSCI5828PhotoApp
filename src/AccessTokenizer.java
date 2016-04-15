

import javax.swing.JTextArea;

import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;

public class AccessTokenizer {
	InstagramService service;
	JTextArea textArea;
	
	public AccessTokenizer(JTextArea textArea) {
		final org.jinstagram.auth.model.Token EMPTY_TOKEN = null;
		final String CLIENT_ID = "b8047a90923847448dd31dd1b9bd7e29";
		final String CLIENT_SECRET = "fc3e6f3b67b6461a8b2d8ca250f1cf9c";
		final String CALLBACK_URL = "http://www.eastland.name"; 
 
		this.textArea = textArea;
		
		service = new InstagramAuthService().apiKey(CLIENT_ID)
				.apiSecret(CLIENT_SECRET).callback(CALLBACK_URL).build();
		
	}
	public void getVerficationCode(JTextArea textArea) {
		
		String authorizationUrl = service.getAuthorizationUrl(null);
 
//		System.out.println("** Instagram Authorization ** \n\n");
//		System.out.println("Copy & Paste the below Authorization URL in your browser...");
//		System.out.println("Authorization URL : " + authorizationUrl);
 
		textArea.append("** Instagram Authorization ** \n\n");
		textArea.append("Copy & Paste the below Authorization URL in your browser...\n\n");
		textArea.append(authorizationUrl + "\n\n");
		textArea.append("and enter the verification code in the field above");
		
	}
	
	public InstagramService getService() {return service;}
	
	public Token getAccessToken (String verifierCode) {
//		Scanner sc = new Scanner(System.in);
// 
//		String verifierCode;
// 
//		System.out.print("Your Verifier Code : ");
//		verifierCode = sc.next();
//		
//		System.out.println();
// 
		Verifier verifier = new Verifier(verifierCode);
		Token accessToken = service.getAccessToken(null, verifier);
		
		return accessToken;
		
	}
	
}

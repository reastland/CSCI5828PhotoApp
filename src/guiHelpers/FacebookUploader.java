package guiHelpers;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;


public class FacebookUploader {

	final static String BOUNDARY = "----------V2ymHFg03ehbqgZCaKO6jy";
	final static String ACCESS_TOKEN = 
//			"680281895420063|bWLT2Jl4fdoe8oDkwLbapx8zkT0";
			"CAAJqtni5QJ8BABA2wkjVaYctUogXzTAPJAh3vvoZAYvqlZAKlB0c2Ncr1rMZALVRvMmy0ZCw6nvn4JbmY8TFe6RFCcAWFioRuF5XUZBzlpsh4AtuIZAvTc82RHNv6HAxgBclKCSduQiIZBNlCOwZAAu2XU8WOZBaNasRY8B8Gef18ufWxC0GkaytmNk3t0mxlYldZAVsauZCUT3k1kZAZCrktTYSi";
//	"AQByg8wpQg3JFjLKR2ZHPzMjuZY1G8sI5PI4NBTxRakPpGIo39IbTxcX6FvAla0ePZlTE8U4DjtQT6I8lzEXXpKM9nNG34-4ryRBhtwuP5JvaK-hu61O558L_NAtExTkmSxF8d7Ld9J27rsCGbK4a4rl4c_R5hj4304wYV6ZCyYfrfRR_zOrf-n8L_BBXrVpd-SbprTMduEK4cqMwnF71W0HQtwn9GHAJMZhW5KRiEcYgcM0IeiMvHpx0Bu03_9tg3HBg74jv60pdlooYvm9fFC41sDBXTacqwDszEaEbzgV9n2Nx_4UpYlFmc-g43Xv72ex2b6DMDeZMOk5V87s-UZi#_=_";
//	        "680281895420063|384791af9c1472716af95d64ebde51b0";
//			"a3afb9b06a3764f68a5908f99b2b8293";
//	"CAAJqtni5QJ8BABHLgf13VygMZC9mi1NHEJChTrZADfmVoA4DSS5uGp9bO3kRqefixmQZAJvsOk52igVHg27dWE2jWErlfPjbE7XPho6yWav2CVD5MTBCG7AmgqzwLgjCQQoT7ju51KuKqnUzUf2FCRqd5tu3mxKWWB4nLySfp2uXsLfqAEFzC2RUnpZAYA5kfwf8gh6biY75TtHyzeop";
//	"AAACEdEose0cBAFqz0jrzIyfMkOA1y1HdpcyFhsB5ZAyYyUveRgiEMjeVKGwZAhgUAV1FgYB8nNfBeMI8c3YIiynL9qRkRWsewS9GVyG5mFchW1t7Kk";
	
	
	public static void uploadPhoto() { 
 
		System.out.println("This isn't working yet");
//		proceed();
	}
	
	private static void proceed() {
	    URL url; 
	    HttpURLConnection urlConn; 
	    DataOutputStream printout; 
	    DataInputStream input; 

	    //------------------------------------------------------------------- 

	    File image = new File("photos/869962121213985271_1540079.jpg"); 
	    if (!image.exists()) System.out.println("no");
	    else System.out.println("yes");

	    FileInputStream imgStream;
		try {
			imgStream = new FileInputStream(image);

		    byte [] buffer = new byte[(int) image.length()]; 
	
		    imgStream.read(buffer); 
	
		    //------------------------------------------------------------------- 
	
	
		    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.18.1.1", 4444)); 
	
		    //url = new URL ("https://graph.facebook.com/me/feed"); 
		    url = new URL("https://graph.facebook.com/me/photos?access_token=" + ACCESS_TOKEN); 
		    System.out.println(url);
		    System.out.println("Open Connection"); 
	
		    urlConn = (HttpURLConnection) url.openConnection(proxy); 
	
		    urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + getBoundaryString()); 
	
		    urlConn.setDoOutput (true); 
		    urlConn.setUseCaches (false); 
		    urlConn.setRequestMethod("POST"); 
	
		   // String content = "access_token=" + URLEncoder.encode ("AAACkMOZA41QEBAHQHUyYcMsLAewOYIe1j5dlOVOlMZBm6h9rvCQEFhmcBHg7ETHrdlrgv4sau573xMVuxIt8DzRxKFuqRqqBskDvOZA9iIkZCdPyI4Bu"); 
	
		    String boundary = getBoundaryString(); 
	
		    String boundaryMessage = getBoundaryMessage(boundary, "upload_field", "P1025[01]_03-07-11.JPG", "image/jpg"); 
	
		    String endBoundary = "\r\n--" + boundary + "--\r\n"; 
	
		    ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	
		    bos.write(boundaryMessage.getBytes()); 
	
		    bos.write(buffer); 
	
		    bos.write(endBoundary.getBytes()); 
	
		    System.out.println("here");
		    printout = new DataOutputStream (urlConn.getOutputStream ()); 
		    System.out.println("here");
		    
		    //printout.writeBytes(content); 
	
		    printout.write(bos.toByteArray()); 
	
		    printout.flush (); 
		    printout.close (); 
		    // Get response data. 
	
	
	
		    //input = new DataInputStream (urlConn.getInputStream ()); 
	
		    if (urlConn.getResponseCode() == 400 || urlConn.getResponseCode() == 500) { 
		        input = new DataInputStream (urlConn.getErrorStream()); 
		    } else { 
		        input = new DataInputStream (urlConn.getInputStream()); 
		    } 
	
		    String str; 
		    while (null != ((str = input.readLine()))) 
		    { 
		        System.out.println (str); 
		    } 
		    input.close (); 

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		    
	}

	public static String getBoundaryString() 
	{ 
	    return BOUNDARY; 
	} 

	public static String getBoundaryMessage(String boundary, String fileField, String fileName, String fileType) 
	{ 
	    StringBuffer res = new StringBuffer("--").append(boundary).append("\r\n"); 
	    res.append("Content-Disposition: form-data; name=\"").append(fileField).append("\"; filename=\"").append(fileName).append("\"\r\n")  
	        .append("Content-Type: ").append(fileType).append("\r\n\r\n"); 

	    return res.toString(); 
	} 

}
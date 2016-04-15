import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SaveImageFromUrl {
	
	private Boolean saved;
	private String imageUrl;
	private String destinationFile;
	private String directoryName;
	
	public Boolean saveImage(ExecutorService executor, final String url, final String file) {
	
		directoryName = "photos";
		
		if (!makePhotosDirectory(directoryName)) {return false;}
		
		saved = false;
		imageUrl = url;
		destinationFile = file;
		
		Future<?> future = executor.submit(new Runnable() {
			public void run() {
				saveFile();
			}
		});

		try {
			future.get(); // waits for the thread to complete
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return saved;
	}
	
	public void saveFile() {
		File file = new File(directoryName, destinationFile);
		URL url;
		try {
			url = new URL(imageUrl);
			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(file);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();

			saved = true;
			
		} catch (IOException e) {
			e.printStackTrace();
			saved = false;
		}			
		
	}

	
	private static Boolean makePhotosDirectory(String directoryName) {
		
		File directory = new File(directoryName);
		
		  // if the directory does not exist, create it
		  if (!directory.exists()) {
			  System.out.println("creating photos directory");
		    try{
		    	directory.mkdir();
		    	System.out.println("photos directory created");
		        return true;
		     } catch(SecurityException se){
		    	 return false;
		     }     
		  }
		  
		  return true;
	}
	
}

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.jinstagram.entity.users.feed.MediaFeedData;

public class PhotoSaver {

	private static ExecutorService executor;
	
	public static void savePhotosToDisk(
			final List<MediaFeedData> imagesMetaDataList, final JTextArea textArea) {
		
		textArea.append("\nPreparing to save photos to photos directory\n\n");
		
		executor = Executors.newCachedThreadPool();
		
		final SwingWorker<Void, String> imageWorker = new SwingWorker<Void, String>() {
		     @Override
		     protected Void doInBackground() throws Exception {
		    	 
				SaveImageFromUrl saver = new SaveImageFromUrl();
		 		
				for (MediaFeedData mfd : imagesMetaDataList) {
		 			
		 			String imageURL = mfd.getImages().getLowResolution().getImageUrl();
					final String filename = mfd.getId() + ".jpg"; // just name the file using the image id from instagram
					System.out.println(filename);
		    	 
			        String textToPublish = "";
			        
					if (saver.saveImage(executor, imageURL, filename)) {
						textToPublish = filename + " written to photos directory\n";
					} else {
						textToPublish = filename + " not saved!\n";
					}
	
			        publish(textToPublish);
			       
		 		}
		 		publish("\nFinished saving photos to disk\n\n");
				executor.shutdown();
		 		return null;
		     }

		     @Override
		     protected void process(List<String> chunks) {
		        for (String chunk : chunks) {
		           textArea.append(chunk);
		        }
		     }
		  };
		  
		  imageWorker.addPropertyChangeListener(new PropertyChangeListener() {

			  @Override
			  public void propertyChange(PropertyChangeEvent evt) {
				  if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
					  try {
						  imageWorker.get();
			          } catch (InterruptedException e) {
			        	  e.printStackTrace();
			          } catch (ExecutionException e) {
			              e.printStackTrace();
			          }
				  }
			  }
		  });

		  imageWorker.execute();	
	}

}

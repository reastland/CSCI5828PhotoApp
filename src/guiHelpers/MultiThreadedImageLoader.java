package guiHelpers;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.jinstagram.entity.users.feed.MediaFeedData;

public class MultiThreadedImageLoader {

	List<MediaFeedData> imagesList;
	Dimension imageSize;
	JTextArea textArea;
	JPanel imagesPanel;
	JPanel infoPanel;
	JPanel centerPanel;
	JFrame frame;
	CardLayout cardLayout;
	int numImagesToFetch;
    final AtomicInteger numImagesFromFile = new AtomicInteger(0);
    final AtomicInteger numImagesFromUrl = new AtomicInteger(0);
	
    
	public MultiThreadedImageLoader(
			List<MediaFeedData> list, Dimension d, JTextArea area, 
			JPanel imagesPanel, JPanel infoPanel, JPanel centerPanel, 
			JFrame f, CardLayout layout, int n) {
		imagesList = list;
		imageSize = d;
		textArea = area;
		this.imagesPanel = imagesPanel;
		this.infoPanel = infoPanel;
		this.centerPanel = centerPanel;
		frame = f;
		this.cardLayout = layout;
		numImagesToFetch = n;
	}

	public void loadImagesAndTrackTime() throws InterruptedException, ExecutionException {
	
		final long start = System.nanoTime();
		
		final SwingWorker<Void, String> imageWorker = new SwingWorker<Void, String>() {
		     @Override
		     protected Void doInBackground() throws Exception {
		    	 publish("Initiating threaded fetching of images from disk / server\n");
		    	 publish("Fetching " + numImagesToFetch + " images\n\n");
		    	 
		    	 loadAndDisplayImages();
		    	 
		    	 final long end = System.nanoTime();
		    	 publish("Finished fetching all images\n");
		    	 publish(numImagesFromFile.get() + " images loaded from disk.\n");
		    	 publish(numImagesFromUrl.get() + " images fetched from Instagram.\n");
		    	 publish("Time taken: " + (end - start)/1.0e9 + " seconds\n\n");
		    	 
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
	
	private void loadAndDisplayImages() throws InterruptedException, ExecutionException {      
		
	    final List<Callable<Tuple>> partitions = 
	    		new ArrayList<Callable<Tuple>>();
	    
	    for(final MediaFeedData data : imagesList) {
	    	partitions.add(new Callable<Tuple>() {
	    		public Tuple call() throws Exception {
	    			File photosDir = new File("photos/");
	    			File file = new File(photosDir, data.getId() + ".jpg");
	    			
	    			if (file.exists()) {
	    				numImagesFromFile.incrementAndGet();
	    				Image image = ImageDisplayer.displayImageFromFile(file, imageSize);
	    				return new Tuple(data, image);
	    			} else {
	    				numImagesFromUrl.incrementAndGet();
	    				String url = data.getImages().getLowResolution().getImageUrl();
	    				Image image = ImageDisplayer.displayImageFromUrl(url, imageSize); 
	    	    		return new Tuple(data, image);
	    			}
	    		}        
	    	});
	    }
	    
	    final ExecutorService executorPool = Executors.newFixedThreadPool(numImagesToFetch);    
	    
	    // run each callable, capture the results in a list of futures
	    final List<Future<Tuple>> futureTuple = 
	    		executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
	    
//	    System.out.println("futures done");
	    
	    // iterate over the list of futures, retrieving each map and adding it the
	    // main hashmap
	    HashSet<Tuple> hashset = new HashSet<Tuple>();
	    for(final Future<Tuple> future : futureTuple) {
//	    	Image image = img.get(); // this will block the UI
	    	Tuple tuple = future.get();
	    	hashset.add(tuple);
	    }
	    
	    Iterator<Tuple> iterator = hashset.iterator();
	    while (iterator.hasNext()) {	    	
	    	    	
	    	Tuple tuple = iterator.next();
	    	final ImageButton imageButton = new ImageButton(
	    			tuple, imageSize, frame, infoPanel, centerPanel, cardLayout);
	    	imageButton.addMouseMotionListener();
	    	imageButton.addPopupMenu(frame);

	    	imagesPanel.add(imageButton);
	    	imagesPanel.revalidate();
	    	imagesPanel.repaint();
	    	imagesPanel.setVisible(true);
	    }
	    
	    executorPool.shutdown();
	} 	
	
	public HashMap<String, Tuple> makeHashmap(MediaFeedData data, Image image) {
		Tuple tuple = new Tuple(data, image);
		HashMap<String, Tuple> hashmap = new HashMap<String, Tuple>();
		hashmap.put(data.getId(), tuple);
		return hashmap;
	}
}

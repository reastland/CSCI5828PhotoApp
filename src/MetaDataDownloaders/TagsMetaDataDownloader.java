package MetaDataDownloaders;

import java.util.ArrayList;
import java.util.Collections;
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

import javax.swing.JTextArea;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

/** This class does not utilize the methods in the superclass MetaDataDownloader.
 * It is different than the other downloaders in that it must repeatedly
 * call the jInstagram library. The other classes only call that library
 * a single time.
 * 
 * @author Randall
 */
public class TagsMetaDataDownloader extends MetaDataDownloader {

	private String[] tags;
	JTextArea outputArea;
	
	public TagsMetaDataDownloader(Instagram in, int n, JTextArea textArea,
			String[] tags) {
		super(in, n, textArea, tags);
		this.tags = tags;
		outputArea = textArea;
	}
	
	/** Calls the jInstagram library once for each tag in the array.
	 * Merges the results into a single combined list of the desired size.
	 * Each tag will have 1/tags.length space in the combined list.
	 *  
	 * @param instagram
	 * @param numImages
	 * @param textArea
	 * @param tags
	 * @param saveToFile
	 * @return
	 */
	@Override
	public List<MediaFeedData> fetchImageMetaData(
			final Boolean verbose, List<MediaFeedData> existingList) 
					throws InterruptedException {
		
		// use a hashset as an intermediary data structure since it won't allow for
		// duplicate images to be added to the list. Cast it at the very end.
		HashSet<MediaFeedData> mainList = new HashSet<MediaFeedData>();

		
		// the Instagram library returns MediaFeedData lists in blocks of 20
		// let's minimize the number of excess network calls
		final int blockSize = 20;
		int numberOfImagesPerTag = numImagesRequested / tags.length;
		int numberOfNetworkCallsPerTag = numberOfImagesPerTag / blockSize + 1;
		final int numThreads = numberOfNetworkCallsPerTag * tags.length;
		
		// Potential problem is that there might not be enough images in the Instagram
		// database that match this tag to satisfy the number we want. Oh well...
		
		// Execute the network calls
	    List<HashSet<MediaFeedData>> tagLists = 
	    		executeNetworkCalls(numberOfNetworkCallsPerTag, numThreads);
	  
		// now take the first n number of images from each sub list and add
		// them to the main list (where n = numImagesToTakeFromThisList)
		int numImagesToTakeFromThisList = numImagesRequested / tags.length + 1;
		for (HashSet<MediaFeedData> tagList : tagLists) {
			Iterator<MediaFeedData> iterator = tagList.iterator();
			for (int i=0; i<numImagesToTakeFromThisList; i++) {
				if (iterator.hasNext()) mainList.add(iterator.next());
			}
		}
		
		// cast the hash set to an array list so that we can return the 
		// correct list type
		List<MediaFeedData> list = new ArrayList<MediaFeedData>(mainList);
		
		// randomize the list for presentation purposes
		Collections.shuffle(list);
		
		// We might have a few extra images, trim it to the exact length
		if (list.size() > numImagesRequested) {
			list.subList(numImagesRequested, list.size()).clear();
		}
		
		return new ArrayList<MediaFeedData>(list);
	}	

	private List<HashSet<MediaFeedData>> executeNetworkCalls(
		int numberOfNetworkCallsPerTag, int numThreads) 
			throws InterruptedException {
		
		final List<Callable<HashSet<MediaFeedData>>> partitions = 
	    		new ArrayList<Callable<HashSet<MediaFeedData>>>();
	    
		AtomicInteger threadNumber = new AtomicInteger(0);
		for (String tag : tags) {
			for (int i=0; i<numberOfNetworkCallsPerTag; i++) {
				final String thisTag = tag;
				final int threadNum = threadNumber.get();
		    	partitions.add(new Callable<HashSet<MediaFeedData>>() {
		    		public HashSet<MediaFeedData> call() throws Exception {
		    			HashSet<MediaFeedData> tagSubList = new HashSet<MediaFeedData>();
						try {
				    		long startTime = System.currentTimeMillis();
							tagSubList.addAll(instagram.getRecentMediaTags(thisTag).getData());
			    			long endTime = System.currentTimeMillis();
			    			float elapsed = (float)(endTime - startTime);
			    			float pace = elapsed / ((float) tagSubList.size());
			    			
			    			final StringBuilder s = new StringBuilder(); 
			    			s.append("\nThread " + threadNum + " ran for: " + elapsed/1000);
			    			s.append(" seconds and produced " + tagSubList.size() + " unique image metadata objects\n");
			    			s.append("Thread " + threadNum + " took " + pace + " milliseconds per image metadata object\n");
			    			s.append("---------------------------------------------------\n");
			    			
			    			System.out.println(s.toString());
			    			outputArea.append(s.toString());			
						} catch (InstagramException e) {
							e.printStackTrace();
						}
						return tagSubList;
		    		}        
		    	});
		    	threadNumber.incrementAndGet();

			}
		}
		
	    final ExecutorService executorPool = Executors.newFixedThreadPool(numThreads);    
	    
	    // run each callable, capture the results in a list of futures
	    final List<Future<HashSet<MediaFeedData>>> futuresList = 
	    		executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
	    
	    // iterate over the list of futures, retrieving it and adding to the main list
		List<HashSet<MediaFeedData>> tagLists = new ArrayList<HashSet<MediaFeedData>>();
	    for(final Future<HashSet<MediaFeedData>> thisFutureList : futuresList) {
	    	try {
	    		HashSet<MediaFeedData> thisList = thisFutureList.get();
	    		tagLists.add(thisList);  // this will block the UI
	    	
	    	} catch (ExecutionException e) {
				e.printStackTrace();
			}
	    }
	    
	    executorPool.shutdown();

	    return tagLists;
	}
}

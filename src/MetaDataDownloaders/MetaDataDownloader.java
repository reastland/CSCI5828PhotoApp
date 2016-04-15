package MetaDataDownloaders;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.apache.commons.collections4.list.TreeList;
import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.MediaFeedData;

public abstract class MetaDataDownloader {

	protected final int jInstagramBlockSize = 20;
	protected int numImagesRequested;
	protected Boolean subclassConstructorCompletedSuccessfully;
	protected Instagram instagram;
	protected JTextArea outputArea;
	protected JFrame frame;
	protected String[] methodChain;
	protected String[][] methodArgument; // each method can have up to 5 arguments
	protected String filename;

	public MetaDataDownloader (Instagram in, int n, JTextArea textArea, String[] args) {
		instagram = in;
		numImagesRequested = n;
		outputArea = textArea;
		
		methodChain = new String[10];
		methodArgument = new String[10][5];
		for (int i=0; i<10; i++) {
			methodChain[i] = "empty";
			methodArgument[i][0] = "empty";
			methodArgument[i][1] = "empty";
			methodArgument[i][2] = "empty";
			methodArgument[i][3] = "empty";
			methodArgument[i][4] = "empty";
		}
		
		subclassConstructorCompletedSuccessfully = true;
	}
	
	public String getFilename() {return filename;}
	
	public List<MediaFeedData> fetchImageMetaData(
			final Boolean verbose, List<MediaFeedData> existingList) 
					throws InterruptedException {
		 
		if (!subclassConstructorCompletedSuccessfully) {
			// return an empty list
			return new TreeList<MediaFeedData>();
		}
			
		// Testing reveals about 10% redundancy in the images retrieved.
		// Pad this to 20% to ensure a complete set with no duplicates
		final int numThreads = (int) ((numImagesRequested / jInstagramBlockSize) * 1.2) + 1;
		
	    final List<Callable<List<MediaFeedData>>> partitions = 
	    		new ArrayList<Callable<List<MediaFeedData>>>();
		   
	    for(int i=0; i<numThreads; i++) {
	    	final int threadNum = i;
	    	partitions.add(new Callable<List<MediaFeedData>>() {
	    		public List<MediaFeedData> call() throws Exception {
	    			return createList(threadNum, verbose);
	    		}        
	    	});
	    }
	    
		// this is an I/O intensive process. For every block, spin a new thread to 
		// maximize CPU utilization
	    final ExecutorService executorPool = Executors.newFixedThreadPool(numThreads);    
	    
	    // run each callable, capture the results in a list of futures
	    final List<Future<List<MediaFeedData>>> futuresList = 
	    		executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
	    
	    // iterate over the list of futures, retrieving it and adding to the main list
		HashSet<MediaFeedData> masterListOfUniqueElements = new HashSet<MediaFeedData>();
	    for(final Future<List<MediaFeedData>> thisList : futuresList) {
	    	try {
	    		masterListOfUniqueElements.addAll(thisList.get());  // this will block the UI
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
	    }
	    
	    executorPool.shutdown();

		System.out.println("All threads completed");
		
		// Trim any excess images from the back of the list
		System.out.println("merged unique list size: " + masterListOfUniqueElements.size());
		int excessImages = masterListOfUniqueElements.size() - numImagesRequested;
		if (excessImages > 0) {
			int i=0;
			Iterator<MediaFeedData> iterator = masterListOfUniqueElements.iterator();
			while (iterator.hasNext()) {
				iterator.next();
				
				if (i < excessImages) iterator.remove();
				else break;
				
				i++;
			}
			
		}		
		
		// if the user chose to append these results to an existing list
		// do so here
		if (existingList != null) {
			masterListOfUniqueElements.addAll(existingList);
		}
		
		String s = "Final Data List Length: " + masterListOfUniqueElements.size() + "\n\n";
		outputArea.append("\n\n" + s);
		
		if (verbose) {
			System.out.println(s);
		}
		
		// convert the hashset to a list and return it
		return new ArrayList<MediaFeedData>(masterListOfUniqueElements);
	}
	
	protected Object getMethodChainWithArguments(Object o, int objNum) {return null;}
	
	
	private List<MediaFeedData> createList(int threadNumber, Boolean verbose) {
//		List<MediaFeedData> uniqueList = SetUniqueList.setUniqueList(new TreeList<MediaFeedData>());
		HashSet<MediaFeedData> uniqueList = new HashSet<MediaFeedData>();
				
		long startTime = System.currentTimeMillis();			
	
		// this list will almost certainly have duplicate values in it
		// declare locally and populate it from the server call
		// filter it adding only unique values to the final list
		List<MediaFeedData> list_mfd = new TreeList<MediaFeedData>(); 
				
		list_mfd = get_list_mfd();
		
		uniqueList.addAll(list_mfd);		
		
		if (verbose) {
			long endTime = System.currentTimeMillis();
	//		System.out.println("Start Time: " + startTime);
	//		System.out.println("End Time: " + endTime);
			float elapsed = (float)(endTime - startTime);
			float pace = elapsed / ((float) uniqueList.size());
			
			final StringBuilder s = new StringBuilder(); 
			s.append("\nThread " + threadNumber + " ran for: " + elapsed/1000 + " seconds and produced " + uniqueList.size() + " unique image metadata objects\n");
			s.append("Thread " + threadNumber + " took " + pace + " milliseconds per image metadata object\n");
			s.append("---------------------------------------------------\n");
			
			System.out.println(s.toString());
			outputArea.append(s.toString());			
		}	
		
		return list_mfd;
	}

	@SuppressWarnings("unchecked")
	private List<MediaFeedData> get_list_mfd() {
		try {
			// the first method call in the chain is always on the object 'instagram'
			Object o = instagram;
			
			/* when we pass an argument to the function, we need to call a different version
			 * of the getMethod() argument, obviously one that accepts and argument.
			 * The subclass contract is that it will use the string 'empty' to denote any
			 * methods that do not take an argument. For those that do, this field will be
			 * set to a string that contains the argument.
			 */
			for (int i=0; i<methodChain.length; i++) {
				
				String methodName = methodChain[i];
				
				if (methodName.equals("empty")) {
				
					// we've reached the end of the method chain. Get the list
					return (List<MediaFeedData>) o;

				} else {
					o = getMethodChainWithArguments(o, i);
				}
			}							
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}

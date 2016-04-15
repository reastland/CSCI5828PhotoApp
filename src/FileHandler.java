import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import jsonHandlers.MediaFeedDataJsonReader;
import jsonHandlers.MediaFeedDataJsonStringBuilder;

import org.apache.commons.collections4.list.TreeList;
import org.jinstagram.entity.users.feed.MediaFeedData;

public class FileHandler {

	public static Boolean writeMetaDataToFile(List<MediaFeedData> imagesMetaDataList, Boolean append) {
		
		BufferedWriter bw;
		
		try {
			File file = new File("metadata");
			
			if (!file.exists() || !append) {
				file.createNewFile();
				System.out.println("created new file");
			}
				
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
			MediaFeedDataJsonStringBuilder jsonBuilder;
			int i =0;
			
			bw.write("[");
			for (MediaFeedData data : imagesMetaDataList) {
				if (i++ > 0) bw.write(",");
				jsonBuilder = new MediaFeedDataJsonStringBuilder(data);
				
				String jsonString = jsonBuilder.mfdToJsonObject().toString();
//				bw.write(jsonBuilder.mfdToJsonObject().toString());
				System.out.println(jsonString);
				bw.write(jsonString);
			}

			bw.write("]");
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		System.out.println("Meta Data Written to File: metadata");
		
		return true;
	}
	
	public static List<MediaFeedData> readMetaDataFromFile() {
		
		File file = new File("metadata");
		List<MediaFeedData> list = new TreeList<MediaFeedData>();

		if (!file.exists())
			return list;
		
		String string = readFileContentsAsString(file);
		
		MediaFeedDataJsonReader jsonReader = new MediaFeedDataJsonReader(string);
		
		return jsonReader.parseJsonString();
		
	}
	
	private static String readFileContentsAsString(File file) {
		
		StringBuilder fileContents = new StringBuilder();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
 
			String currentLine;
	 
			while ((currentLine = br.readLine()) != null) {
				fileContents.append(currentLine);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return fileContents.toString();
	}
}

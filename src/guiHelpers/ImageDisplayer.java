package guiHelpers;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;


public class ImageDisplayer {

	private static BufferedImage img;
	private static Dimension dimension;
	
	public static Image displayImageFromFile(File file, Dimension d){
		
		try {
			dimension = d;
			img = ImageIO.read(file);
		} catch (IOException e) {}
		
		return scaleImageToFit();	
	}
	
	public static Image displayImageFromUrl(String stringUrl, Dimension d){

		try {
			dimension = d;
			URL url = new URL(stringUrl);
		    img = ImageIO.read(url);
		} catch (IOException e) {}
		
		return scaleImageToFit();
	}
	
	private static Image scaleImageToFit() {
		
		if (dimension.getWidth() > dimension.getHeight()) {
			return img.getScaledInstance(
					(int)dimension.getWidth(), 
					(int)dimension.getWidth(), 
			        Image.SCALE_SMOOTH);
		} else {
			return img.getScaledInstance(
					(int)dimension.getHeight(), 
					(int)dimension.getHeight(), 
			        Image.SCALE_SMOOTH);
		}
	}
}

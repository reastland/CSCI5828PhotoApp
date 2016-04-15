package guiHelpers;

import java.awt.Image;

import org.jinstagram.entity.users.feed.MediaFeedData;

public class Tuple {

	private String id;
	private MediaFeedData data;
	private Image image;
	
	public Tuple(MediaFeedData data, Image image) {
		this.id = data.getId();
		this.data = data;
		this.image = image;
	}
	
	public Tuple getTuple() {return this;}
	public String getId() {return id;};
	public MediaFeedData getData() {return data;}
	public Image getImage() {return image;}
}

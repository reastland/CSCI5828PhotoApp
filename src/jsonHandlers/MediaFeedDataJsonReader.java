package jsonHandlers;

import java.util.ArrayList;
import java.util.List;

import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.common.Caption;
import org.jinstagram.entity.common.Comments;
import org.jinstagram.entity.common.FromTagData;
import org.jinstagram.entity.common.ImageData;
import org.jinstagram.entity.common.Images;
import org.jinstagram.entity.common.Likes;
import org.jinstagram.entity.common.Location;
import org.jinstagram.entity.common.User;
import org.jinstagram.entity.common.VideoData;
import org.jinstagram.entity.common.Videos;
import org.jinstagram.entity.users.feed.MediaFeedData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MediaFeedDataJsonReader {

	String jsonString;
	Gson gson = new GsonBuilder().serializeNulls().create();
	
	public MediaFeedDataJsonReader(String jString) {
		jsonString = jString;
	}
	
	public List<MediaFeedData> parseJsonString() {
		
		List<MediaFeedData> mfd_list = new ArrayList<MediaFeedData>();
	
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(jsonString);		
		JsonArray jsonArrayMediaFeedData = jsonElement.getAsJsonArray();
		
		for (JsonElement element_mfd : jsonArrayMediaFeedData) {
			
			JsonObject jsonObjMediaFeedData = element_mfd.getAsJsonObject();
			MediaFeedData mfd = jsonDecodeMediaFeedData(jsonObjMediaFeedData);
			
			mfd_list.add(mfd);
		}
		
		return mfd_list;
	}

	private MediaFeedData jsonDecodeMediaFeedData(JsonObject jObj) {
		MediaFeedData mfd = new MediaFeedData();
		
		Caption caption = getCaptionFromJsonObject(jObj.getAsJsonObject("caption"));
		Comments comments = getCommentsFromJsonObject(jObj.getAsJsonObject("comments"));
		String createdAt = jObj.get("createdTime").getAsString();
		String id = jObj.get("id").getAsString();
		String imageFilter = jObj.get("imageFilter").getAsString();
		Images images = getImagesFromJsonObject(jObj.getAsJsonObject("images"));
		Videos videos = getVideosFromJsonObject(jObj.getAsJsonObject("videos"));
		Likes likes = getLikesFromJsonObject(jObj.getAsJsonObject("likes"));
		String link = jObj.get("link").getAsString();
		Location location = getLocationFromJsonObject(jObj.getAsJsonObject("location"));
		List<String> tags = getTagsFromJsonObject(jObj.getAsJsonObject("tags"));
		String type = jObj.get("type").getAsString();
		User user = getUserFromJsonObject(jObj.getAsJsonObject("user"));
		Boolean user_has_liked = getUserHasLikedFromJsonObject(jObj.get("user_has_liked").getAsInt());
		
		mfd.setCaption(caption);
		mfd.setComments(comments);
		mfd.setCreatedTime(createdAt);
		mfd.setId(id);
		mfd.setImageFilter(imageFilter);
		mfd.setImages(images);
		mfd.setVideos(videos);
		mfd.setLikes(likes);
		mfd.setLink(link);
		mfd.setLocation(location);
		mfd.setTags(tags);
		mfd.setType(type);
		mfd.setUser(user);
		mfd.setUserHasLiked(user_has_liked);
		
		return mfd;
	}
	
	private JsonObject removeNullValues(JsonObject jObj) {

		// there may be null values in an object. This will safely remove them
		// without the program crashing. There is no simple way to check for null
		// values in gson, unfortunately.
		
		String noNulls = gson.toJson(jObj);
		noNulls = noNulls.replaceAll("null","\"\"");
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(noNulls);		
		return jsonElement.getAsJsonObject();
	}
	
	private Caption getCaptionFromJsonObject(JsonObject objCaption) {
		Caption caption = new Caption();
		
		objCaption = removeNullValues(objCaption);
		
		if (!objCaption.toString().equals("{}")) { // checking for empty object
			caption.setCreatedTime(objCaption.get("createdTime").getAsString());
			caption.setCreatedTime(objCaption.get("id").getAsString());
		
			if (objCaption.getAsJsonObject("from") != null) {
				JsonObject objFrom = objCaption.getAsJsonObject("from");
				objFrom = removeNullValues(objFrom);
				FromTagData fromTagData = new FromTagData();
				fromTagData.setFullName(objFrom.get("full_name").getAsString());
				fromTagData.setId(objFrom.get("id").getAsString());
				fromTagData.setProfilePicture(objFrom.get("profile_picture").getAsString());
				fromTagData.setUsername(objFrom.get("username").getAsString());
				caption.setFrom(fromTagData);
			}
			
			caption.setCreatedTime(objCaption.get("text").getAsString());
		}
		
		return caption;
	}
	
	private Comments getCommentsFromJsonObject(JsonObject objComments) {
		Comments comments = new Comments();
		 
		objComments = removeNullValues(objComments);
		
		if (!objComments.toString().equals("{}")) { // checking for empty object
			JsonArray arrComments = objComments.get("comments").getAsJsonArray();
			
			List<CommentData> commentDataList = new ArrayList<CommentData>();
			
			for (JsonElement element : arrComments) {
				
				JsonObject comment = element.getAsJsonObject();
				comment = removeNullValues(comment);
				
				JsonObject objFrom = comment.getAsJsonObject("fromTagData");
				if (!objFrom.toString().equals("{}")) { // checking for empty object

					objFrom = removeNullValues(objFrom);
					
					CommentData commentData = new CommentData();
					
					FromTagData fromTagData = new FromTagData();
					fromTagData.setFullName(objFrom.get("full_name").getAsString());
					fromTagData.setId(objFrom.get("id").getAsString());
					fromTagData.setProfilePicture(objFrom.get("profile_picture").getAsString());
					fromTagData.setUsername(objFrom.get("username").getAsString());
					
					commentData.setCommentFrom(fromTagData);
					commentData.setCreatedTime(comment.get("createdTime").getAsString());
					commentData.setId(comment.get("id").getAsString());
					commentData.setText(comment.get("text").getAsString());
				
					commentDataList.add(commentData);
				}
			}
			
			comments.setComments(commentDataList);
			comments.setCount(objComments.get("count").getAsInt());
		}
		
		return comments;
	}
	
	private Images getImagesFromJsonObject(JsonObject objImages) {
		Images images = new Images();
		
		if (!objImages.toString().equals("{}")) {
//		if (objImages.get("lowResolution") != null) {
			JsonObject objLowResolution = objImages.get("lowResolution").getAsJsonObject();
			objLowResolution = removeNullValues(objLowResolution);
			
			ImageData lowResolution = new ImageData();
			lowResolution.setImageHeight(objLowResolution.get("imageHeight").getAsInt());
			lowResolution.setImageUrl(objLowResolution.get("imageUrl").getAsString());
			lowResolution.setImageWidth(objLowResolution.get("width").getAsInt());
			images.setLowResolution(lowResolution);
//		}
//		
//		if (objImages.get("standardResolution") != null) {
			JsonObject objStandardResolution = objImages.get("standardResolution").getAsJsonObject();
			objStandardResolution = removeNullValues(objStandardResolution);
			
			ImageData standardResolution = new ImageData();
			standardResolution.setImageHeight(objStandardResolution.get("imageHeight").getAsInt());
			standardResolution.setImageUrl(objStandardResolution.get("imageUrl").getAsString());
			standardResolution.setImageWidth(objStandardResolution.get("width").getAsInt());
			images.setStandardResolution(standardResolution);
//		}
//		
//		if (objImages.get("thumbnail") != null) {
			JsonObject objThumbnail = objImages.get("thumbnail").getAsJsonObject();
			objThumbnail = removeNullValues(objThumbnail);
			ImageData thumbnail = new ImageData();
			thumbnail.setImageHeight(objThumbnail.get("imageHeight").getAsInt());
			thumbnail.setImageUrl(objThumbnail.get("imageUrl").getAsString());
			thumbnail.setImageWidth(objThumbnail.get("width").getAsInt());
			images.setThumbnail(thumbnail);
//		}
		}
		
		return images;
	}
	
	private Videos getVideosFromJsonObject(JsonObject objVideos) {
		Videos videos = new Videos();
		
		if (!objVideos.toString().equals("{}")) {
//		if (objVideos.get("lowResolution") != null) {
			JsonObject objLowResolution = objVideos.get("lowResolution").getAsJsonObject();
			VideoData lowResolution = new VideoData();
			lowResolution.setHeight(objLowResolution.get("height").getAsInt());
			lowResolution.setUrl(objLowResolution.get("url").getAsString());
			lowResolution.setWidth(objLowResolution.get("width").getAsInt());
			videos.setLowResolution(lowResolution);
//		}
//		
//		if (objVideos.get("standardResolution") != null) {
			JsonObject objStandardResolution = objVideos.get("standardResolution").getAsJsonObject();
			VideoData standardResolution = new VideoData();
			standardResolution.setHeight(objStandardResolution.get("height").getAsInt());
			standardResolution.setUrl(objStandardResolution.get("url").getAsString());
			standardResolution.setWidth(objStandardResolution.get("width").getAsInt());
			videos.setStandardResolution(standardResolution);
//		}	
		}
		return videos;
	}
		
	private Likes getLikesFromJsonObject(JsonObject objLikes) {
		Likes likes = new Likes();				
		
		objLikes = removeNullValues(objLikes);
		
		if (!objLikes.toString().equals("{}")) {
			JsonArray arrUsers = objLikes.get("likesUserList").getAsJsonArray();
			List<User> userList = new ArrayList<User>();
			
			for (JsonElement element : arrUsers) {
				
				JsonObject objUser = element.getAsJsonObject();
				objUser = removeNullValues(objUser);
				
				User user = new User();											
				user.setBio(objUser.get("bio").getAsString());
				user.setFullName(objUser.get("fullName").getAsString());
				user.setId(objUser.get("id").getAsString());
				user.setProfilePictureUrl(objUser.get("profile_picture").getAsString());
				user.setUserName(objUser.get("userName").getAsString());
				user.setWebsiteUrl(objUser.get("website").getAsString());
				
				userList.add(user);
			}
			
			likes.setCount(objLikes.get("count").getAsInt());
			likes.setLikesUserList(userList);
		}
		
		return likes;
	}
	
	private Location getLocationFromJsonObject(JsonObject objLocation) {
		Location location = new Location();
		
		objLocation = removeNullValues(objLocation);
		
		if (!objLocation.toString().equals("{}")) {
			location.setId(objLocation.get("id").getAsString());
			location.setName(objLocation.get("name").getAsString());
			location.setLatitude(objLocation.get("latitude").getAsDouble());
			location.setLongitude(objLocation.get("longitude").getAsDouble());
		}
		
		return location;
	}

	private List<String> getTagsFromJsonObject(JsonObject objTags) {
		List<String> tags = new ArrayList<String>();
		
		if (!objTags.toString().equals("{}")) {
			JsonArray arrTags = objTags.get("tags").getAsJsonArray();
			for (JsonElement element : arrTags) {
				tags.add(element.getAsJsonObject().getAsString());
			}
		}
		
		return tags;
	}
	
	private User getUserFromJsonObject(JsonObject objUser) {
		User user = new User();
		
		if (!objUser.toString().equals("{}")) {
			user.setBio(objUser.get("bio").getAsString());
			user.setFullName(objUser.get("fullName").getAsString());
			user.setId(objUser.get("id").getAsString());
			user.setProfilePictureUrl(objUser.get("profile_picture").getAsString());
			user.setUserName(objUser.get("userName").getAsString());
			user.setWebsiteUrl(objUser.get("website").getAsString());
		}
		
		return user;
	}
	
	private Boolean getUserHasLikedFromJsonObject(int userHasLiked) {
		return (userHasLiked == 1) ? true : false;
	}


}

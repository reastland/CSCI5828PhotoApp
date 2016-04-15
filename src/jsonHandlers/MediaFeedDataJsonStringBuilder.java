package jsonHandlers;

import java.util.ArrayList;
import java.util.List;

import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.common.User;
import org.jinstagram.entity.users.feed.MediaFeedData;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class MediaFeedDataJsonStringBuilder {

	MediaFeedData mfd;
	
	public MediaFeedDataJsonStringBuilder(MediaFeedData mfd) {
		this.mfd = mfd;
	}
	
	public JsonObject mfdToJsonObject() {
		
		JsonObject objMediaFeedData = new JsonObject();

//		objMediaFeedData.addProperty("test", "test value");
		
		if (mfd != null) {
			objMediaFeedData.add("caption", getCaptionObject());
			objMediaFeedData.add("comments", getCommentsObject());
			objMediaFeedData.addProperty("createdTime", mfd.getCreatedTime());
			objMediaFeedData.addProperty("id", mfd.getId());
			objMediaFeedData.addProperty("imageFilter", mfd.getImageFilter());
			objMediaFeedData.add("images", getImagesObject());
			objMediaFeedData.add("videos", getVideosObject());
			objMediaFeedData.add("likes", getLikesObject());
			objMediaFeedData.addProperty("link", mfd.getLink());
			objMediaFeedData.add("location", getLocationObject());
			objMediaFeedData.add("tags", getTagsObject());
			objMediaFeedData.addProperty("type", mfd.getType());
			objMediaFeedData.add("user", getUserObject());
			objMediaFeedData.addProperty("user_has_liked", (mfd.isUserHasLiked()) ? "1" : "0");
		}
		
		return objMediaFeedData;
	}
	
	private JsonObject getCaptionObject() {
		JsonObject caption = new JsonObject();
		
		JsonObject fromTagData = new JsonObject();
		
		if (mfd.getCaption() != null) {
			fromTagData.addProperty("full_name", mfd.getCaption().getFrom().getFullName());
			fromTagData.addProperty("id", mfd.getCaption().getFrom().getId());
			fromTagData.addProperty("profile_picture", mfd.getCaption().getFrom().getProfilePicture());
			fromTagData.addProperty("username", mfd.getCaption().getFrom().getUsername());
			
			caption.addProperty("createdTime", mfd.getCaption().getCreatedTime());
			caption.add("from", fromTagData);
			caption.addProperty("id", mfd.getCaption().getId());
			caption.addProperty("text", mfd.getCaption().getText());
		}
				
		return caption;
	}
	
	private JsonObject getCommentsObject() {
		JsonObject comments = new JsonObject();
		
		if (mfd.getComments() != null) {
			JsonArray commentDataArray = new JsonArray();
			List<CommentData> commentDataList = mfd.getComments().getComments();
			for (CommentData data : commentDataList) {
				JsonObject commentData = new JsonObject();
				
				JsonObject fromTagData = new JsonObject();
				
				if (mfd.getCaption() != null) {
					fromTagData.addProperty("full_name", data.getCommentFrom().getFullName());
					fromTagData.addProperty("id", data.getCommentFrom().getId());
					fromTagData.addProperty("profile_picture", data.getCommentFrom().getProfilePicture());
					fromTagData.addProperty("username", data.getCommentFrom().getUsername());
				}
				
				commentData.add("fromTagData", fromTagData);
				commentData.addProperty("createdTime", data.getCreatedTime());
				commentData.addProperty("id", data.getId());
				commentData.addProperty("text", data.getText());
				commentDataArray.add(commentData);
			}
			
			comments.add("comments", commentDataArray);
			comments.addProperty("count", mfd.getComments().getCount());
		}
		
		return comments;
	}
	
	private JsonObject getImagesObject() {
		JsonObject images = new JsonObject();
		
		if (mfd.getImages() != null) {
			JsonObject lowResolution = new JsonObject();
			if (mfd.getImages().getLowResolution() != null) {
				lowResolution.addProperty("imageHeight", mfd.getImages().getLowResolution().getImageHeight());
				lowResolution.addProperty("imageUrl", mfd.getImages().getLowResolution().getImageUrl());
				lowResolution.addProperty("width", mfd.getImages().getLowResolution().getImageWidth());
			}
			
			JsonObject standardResolution = new JsonObject();
			if (mfd.getImages().getStandardResolution() != null) {
				standardResolution.addProperty("imageHeight", mfd.getImages().getStandardResolution().getImageHeight());
				standardResolution.addProperty("imageUrl", mfd.getImages().getStandardResolution().getImageUrl());
				standardResolution.addProperty("width", mfd.getImages().getStandardResolution().getImageWidth());
			}
			
			JsonObject thumbnail = new JsonObject();
			if (mfd.getImages().getThumbnail() != null) {
				thumbnail.addProperty("imageHeight", mfd.getImages().getThumbnail().getImageHeight());
				thumbnail.addProperty("imageUrl", mfd.getImages().getThumbnail().getImageUrl());
				thumbnail.addProperty("width", mfd.getImages().getThumbnail().getImageWidth());
			}
			
			images.add("lowResolution", lowResolution);
			images.add("standardResolution", standardResolution);
			images.add("thumbnail", thumbnail);
		}
		
		return images;
	}
	
	private JsonObject getVideosObject() {
		JsonObject videos = new JsonObject();
		
		if (mfd.getVideos() != null) {
			JsonObject lowResolution = new JsonObject();
			if (mfd.getVideos().getLowResolution() != null) {
				lowResolution.addProperty("url", mfd.getVideos().getLowResolution().getUrl());
				lowResolution.addProperty("width", mfd.getVideos().getLowResolution().getWidth());
				lowResolution.addProperty("height", mfd.getVideos().getLowResolution().getHeight());
			}
			
			JsonObject standardResolution = new JsonObject();
			if (mfd.getVideos().getStandardResolution() != null) {
				standardResolution.addProperty("url", mfd.getVideos().getStandardResolution().getUrl());
				standardResolution.addProperty("width", mfd.getVideos().getStandardResolution().getWidth());
				standardResolution.addProperty("height", mfd.getVideos().getStandardResolution().getHeight());
			}
			
			videos.add("lowResolution", lowResolution);
			videos.add("standardResolution", standardResolution);
		}
		
		return videos;
	}
	
	private JsonObject getLikesObject() {
		JsonObject likes = new JsonObject();
		
		if (mfd.getLikes() != null) {
			likes.addProperty("count", mfd.getLikes().getCount());
			
			List<User> userList = mfd.getLikes().getLikesUserList();
			JsonArray userArray = new JsonArray();
			for (User user : userList) {
				JsonObject userObject = new JsonObject();
				userObject.addProperty("bio", user.getBio());
				userObject.addProperty("fullName", user.getFullName());
				userObject.addProperty("id", user.getId());
				userObject.addProperty("profile_picture", user.getProfilePictureUrl());
				userObject.addProperty("userName", user.getUserName());
				userObject.addProperty("website", user.getWebsiteUrl());
	
				userArray.add(userObject);
			}
			
			likes.add("likesUserList", userArray);
		}
		
		return likes;
	}
	
	private JsonObject getLocationObject() {
		JsonObject location = new JsonObject();
		
		if (mfd.getLocation() != null) {
			location.addProperty("id", mfd.getLocation().getId());
			location.addProperty("name", mfd.getLocation().getName());
			location.addProperty("latitude", mfd.getLocation().getLatitude());
			location.addProperty("longitude", mfd.getLocation().getLongitude());
		}
		
		return location;
	}
	
	private JsonObject getTagsObject() {
		JsonObject tags = new JsonObject();
		
		if (mfd.getTags() != null) {
			List<String> tagsList = new ArrayList<String>();
			JsonArray tagsArray = new JsonArray();
			for (String s : tagsList) {
				tagsArray.add(new JsonPrimitive(s));
			}
			
			tags.add("tags", tagsArray);
		}
		
		return tags;
	}
	
	private JsonObject getUserObject() {
		JsonObject user = new JsonObject();

		if (mfd.getUser() != null) {
			
			if (mfd.getUser().getBio() != null)
				user.addProperty("bio", mfd.getUser().getBio());
			else user.addProperty("bio", "");
			
			if (mfd.getUser().getFullName() != null)
				user.addProperty("fullName", mfd.getUser().getFullName());
			else user.addProperty("fullName", "");
			
			if (mfd.getUser().getId() != null)
				user.addProperty("id", mfd.getUser().getId());
			else user.addProperty("id", "");
			
			if (mfd.getUser().getProfilePictureUrl() != null)
				user.addProperty("profile_picture", mfd.getUser().getProfilePictureUrl());
			else user.addProperty("profile_picture", "");
			
			if (mfd.getUser().getUserName() != null)
				user.addProperty("userName", mfd.getUser().getUserName());
			else user.addProperty("userName", "");
			
			if (mfd.getUser().getWebsiteUrl() != null)
				user.addProperty("website", mfd.getUser().getWebsiteUrl());
			else user.addProperty("website", "");
		}
		
		return user;
	}

}

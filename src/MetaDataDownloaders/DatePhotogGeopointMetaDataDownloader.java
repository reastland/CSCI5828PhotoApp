package MetaDataDownloaders;

import javax.swing.JTextArea;

import org.jinstagram.Instagram;

public class DatePhotogGeopointMetaDataDownloader extends MetaDataDownloader {

	public DatePhotogGeopointMetaDataDownloader(Instagram in,
			int n, JTextArea textArea, String[] args) {
		super(in, n, textArea, args);
		// TODO Auto-generated constructor stub
	}	
	/**
     * Search for media in a given area.
     *
     * @param latitude Latitude of the center search coordinate.
     * @param longitude Longitude of the center search coordinate.
     * @return a MediaFeed object.
     * @throws InstagramException if any error occurs
     */
//    private MediaFeed searchMediaDatePhotogGeopoint(double latitude, double longitude, Date maxTimeStamp, Date minTimeStamp, String user) throws InstagramException {
//        Map<String, String> params = new HashMap<String, String>();
//
//        params.put(QueryParam.LATITUDE, Double.toString(latitude));
//        params.put(QueryParam.LONGITUDE, Double.toString(longitude));
//
//        if(maxTimeStamp != null) {
//            params.put(QueryParam.MAX_TIMESTAMP, String.valueOf(maxTimeStamp.getTime()/1000));
//        }
//
//        if(minTimeStamp != null) {
//            params.put(QueryParam.MIN_TIMESTAMP, String.valueOf(minTimeStamp.getTime()/1000));
//        }
//
//        params.put(QueryParam.SEARCH_QUERY, user);
//
//        MediaFeed mediaFeed = createInstagramObject(Verbs.GET, MediaFeed.class, Methods.MEDIA_SEARCH, params);
//
//        return mediaFeed;
//    }

	
}

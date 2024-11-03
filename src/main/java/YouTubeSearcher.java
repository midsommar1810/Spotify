import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class YouTubeSearcher {

    private static final String API_KEY = "AIzaSyAvmH-_ZybS-GkIiXDOrLsMQE0Le8oWKf0"; // Replace with your YouTube Data API key
    private static final String YOUTUBE_SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";

    /**
     * Searches for a YouTube video by its name and returns the video ID of the first result.
     *
     * @param videoName The name of the video to search for.
     * @return The video ID of the first matching video, or null if no video is found.
     * @throws Exception if there is an issue with the API request.
     */
    public static String searchYouTubeVideo(String videoName) throws Exception {
        HttpResponse<JsonNode> response = Unirest.get(YOUTUBE_SEARCH_URL)
                .queryString("part", "snippet")
                .queryString("q", videoName)
                .queryString("type", "video")
                .queryString("maxResults", "1")
                .queryString("key", API_KEY)
                .asJson();

        if (response.getStatus() == 200) {
            JSONObject jsonResponse = response.getBody().getObject();
            JSONArray items = jsonResponse.getJSONArray("items");

            if (items.length() > 0) {
                JSONObject video = items.getJSONObject(0);
                return video.getJSONObject("id").getString("videoId");
            }
        } else {
            System.out.println("Error: " + response.getStatusText());
        }
        return null; // Return null if no video is found or if there’s an error
    }

    public static void main(String[] args) {
        try {
            String videoName = "see you again wiz khalifa";
            String videoId = searchYouTubeVideo(videoName);
            if (videoId != null) {
                System.out.println("Video ID: " + videoId);
            } else {
                System.out.println("No video found for the given name.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

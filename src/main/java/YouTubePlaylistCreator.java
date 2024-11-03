import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class YouTubePlaylistCreator {
    private final YouTube youtubeService;
    private static String accessToken;
    private static final String API_KEY = "AIzaSyAvmH-_ZybS-GkIiXDOrLsMQE0Le8oWKf0"; // Replace with your YouTube Data API key
    private static final String YOUTUBE_SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";

    public YouTubePlaylistCreator(YouTube youtubeService, String accessToken) {
        this.youtubeService = youtubeService;
        this.accessToken = accessToken;
    }

    public String createYouTubePlaylist(String title, String description) throws IOException {
        PlaylistSnippet playlistSnippet = new PlaylistSnippet();
        playlistSnippet.setTitle(title);
        playlistSnippet.setDescription(description);

        PlaylistStatus playlistStatus = new PlaylistStatus();
        playlistStatus.setPrivacyStatus("public");

        Playlist youTubePlaylist = new Playlist();
        youTubePlaylist.setSnippet(playlistSnippet);
        youTubePlaylist.setStatus(playlistStatus);

        YouTube.Playlists.Insert playlistInsertCommand = youtubeService.playlists()
                .insert("snippet,status", youTubePlaylist);
        Playlist playlistInserted = playlistInsertCommand.execute();

        return playlistInserted.getId();
    }

    public void addTracksToPlaylist(String playlistId, List<String> tracks) throws Exception {
        for (String track : tracks) {
//            String videoId = searchYouTubeVideo(track);
//            if (videoId != null) {
//                addToYouTubePlaylist(playlistId, videoId);
//            }
            String videoId2 = searchYouTubeVideo2(track);
            if (videoId2 != null) {
                addToYouTubePlaylist(playlistId, videoId2);
            }
        }
    }

    private String searchYouTubeVideo(String query) throws Exception {
        HttpResponse<String> response = Unirest.get("https://www.googleapis.com/youtube/v3/search")
                .header("Authorization", "Bearer " + accessToken)
                .queryString("part", "snippet")
                .queryString("q", query)
                .queryString("maxResults", 1)
                .queryString("type", "video")
                .asString();
        System.out.println(response.getStatus());
        if (response.getStatus() == 200) {
            String jsonResponse = response.getBody();
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");
            if (items.size() > 0) {
                return items.get(0).getAsJsonObject().getAsJsonObject("id").get("videoId").getAsString();
            }
        }
        return null;
    }

    private void addToYouTubePlaylist(String playlistId, String videoId) throws IOException {
        PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
        playlistItemSnippet.setPlaylistId(playlistId);

        ResourceId resourceId = new ResourceId();
        resourceId.setKind("youtube#video");
        resourceId.setVideoId(videoId);

        playlistItemSnippet.setResourceId(resourceId);

        PlaylistItem playlistItem = new PlaylistItem();
        playlistItem.setSnippet(playlistItemSnippet);

        YouTube.PlaylistItems.Insert playlistItemsInsertCommand = youtubeService.playlistItems()
                .insert("snippet", playlistItem);
        playlistItemsInsertCommand.execute();
    }

    public static String searchYouTubeVideo2(String videoName) throws Exception {
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
}

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class SpotifyFetch {
    private static final String CLIENT_ID = "0cdb670e82a943e6b810c5ccd2cf8b9f";
    private static final String CLIENT_SECRET = "4f6e8d8f791a43049c0efa7b5cf6057b";
    private static String accessToken;

    public SpotifyFetch() {}

    public boolean authenticateSpotify() throws Exception {
        HttpResponse<String> response = Unirest.post("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("grant_type", "client_credentials")
                .asString();

        if (response.getStatus() == 200) {
            JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
            accessToken = json.get("access_token").getAsString();
            return true;
        } else {
            System.err.println("Failed to authenticate with Spotify: " + response.getBody());
            return false;
        }
    }

    public static String fetchPlaylistTracks(String playlistId) throws Exception {
        HttpResponse<String> response = Unirest.get("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks")
                .header("Authorization", "Bearer " + accessToken)
                .asString();

        if (response.getStatus() == 200) {
            JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
            JsonArray items = json.getAsJsonArray("items");
            StringBuilder trackNames = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                JsonObject track = items.get(i).getAsJsonObject().getAsJsonObject("track");
                String trackName = track.get("name").getAsString();
                String artistName = track.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString();
                trackNames.append((i + 1) + ". " + trackName + " by " + artistName + "\n");
            }
            return trackNames.toString();
        } else {
            System.err.println("Failed to fetch playlist tracks: " + response.getBody());
            return "Failed to retrieve tracks.";
        }
    }
}

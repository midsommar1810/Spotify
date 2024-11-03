import com.google.api.services.youtube.YouTube;

import java.util.Arrays;
import java.util.List;

public class MainClass {
    public static void main(String[] args) {
        try {
            // Spotify Authentication and Fetching Tracks
            SpotifyFetch spotifyFetch = new SpotifyFetch();
            if (spotifyFetch.authenticateSpotify()) {
                System.out.println("Spotify authentication successful!");
                String playlistId = "3Dm2Z8CW1megCWIe66UBFj"; // Replace with your Spotify playlist ID
                String tracks = spotifyFetch.fetchPlaylistTracks(playlistId);
                System.out.println("Spotify Playlist Tracks:\n" + tracks);

                // Convert String tracks to List<String>
                List<String> trackList = Arrays.asList(tracks.split("\n"));

                // YouTube Authentication and Playlist Creation
                YouTube youtubeService = YouTubeAuth.authenticate("client_secret.json");
                YouTubePlaylistCreator youTubePlaylistCreator = new YouTubePlaylistCreator(youtubeService, "AIzaSyAvmH-_ZybS-GkIiXDOrLsMQE0Le8oWKf0");

                String youTubePlaylistId = youTubePlaylistCreator.createYouTubePlaylist("My YouTube Playlist", "A playlist created from Spotify tracks");
                youTubePlaylistCreator.addTracksToPlaylist(youTubePlaylistId, trackList);

                System.out.println("YouTube Playlist created successfully!");
            } else {
                System.err.println("Failed to authenticate with Spotify.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class MainClass {
    public static void main(String[] args) {
        try {
            SpotifyFetch spotifyFetch = new SpotifyFetch();

            // Authenticate
            if (spotifyFetch.authenticateSpotify()) {
                System.out.println("Authentication successful!");

                // Fetch playlist tracks
                String playlistId = "3Dm2Z8CW1megCWIe66UBFj"; // Replace with your Spotify playlist ID
                String tracks = SpotifyFetch.fetchPlaylistTracks(playlistId);
                System.out.println("Playlist Tracks:\n" + tracks);
            } else {
                System.err.println("Failed to authenticate with Spotify.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

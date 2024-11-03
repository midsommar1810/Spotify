import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SpotifyPlaylistViewer {

    private static final String CLIENT_ID = "0cdb670e82a943e6b810c5ccd2cf8b9f"; // Replace with your Spotify Client ID
    private static final String CLIENT_SECRET = "4f6e8d8f791a43049c0efa7b5cf6057b"; // Replace with your Spotify Client Secret
    private static String accessToken;

    public static void main(String[] args) {
        // Set up the main frame
        JFrame frame = new JFrame("Spotify Playlist Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout(10, 10));

        // Top panel for playlist URL input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout(5, 5));
        JLabel urlLabel = new JLabel("Enter Spotify Playlist URL:");
        JTextField urlField = new JTextField();
        JButton fetchButton = new JButton("Fetch Playlist");

        inputPanel.add(urlLabel, BorderLayout.NORTH);
        inputPanel.add(urlField, BorderLayout.CENTER);
        inputPanel.add(fetchButton, BorderLayout.EAST);

        // Center panel for displaying track names
        JTextArea trackDisplay = new JTextArea();
        trackDisplay.setLineWrap(true);
        trackDisplay.setWrapStyleWord(true);
        trackDisplay.setEditable(false);
        trackDisplay.setFont(new Font("Arial", Font.PLAIN, 14));

        // Wrap the text area in a scroll pane
        JScrollPane scrollPane = new JScrollPane(trackDisplay);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Bottom panel for status messages or error output
        JPanel statusPanel = new JPanel();
        JLabel statusLabel = new JLabel("Status: Waiting for playlist URL...");
        statusPanel.add(statusLabel);

        // Add panels to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Fetch Button action listener
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playlistUrl = urlField.getText().trim();
                try {
                    if (authenticateSpotify()) {
                        String playlistId = extractPlaylistId(playlistUrl);
                        if (playlistId != null) {
                            String trackNames = fetchPlaylistTracks(playlistId);
                            trackDisplay.setText(trackNames);
                            statusLabel.setText("Status: Playlist loaded successfully.");
                        } else {
                            trackDisplay.setText("");
                            statusLabel.setText("Status: Invalid Playlist URL.");
                        }
                    } else {
                        statusLabel.setText("Status: Authentication failed.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    trackDisplay.setText("");
                    statusLabel.setText("Status: Error fetching playlist.");
                }
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    private static boolean authenticateSpotify() throws Exception {
        HttpResponse<String> response = Unirest.post("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("grant_type", "client_credentials")
                .asString();

        if (response.getStatus() == 200) {
            JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
            accessToken = json.get("access_token").getAsString();
            return true;
        }
        return false;
    }

    private static String extractPlaylistId(String playlistUrl) {
        try {
            String[] parts = playlistUrl.split("/");
            String idPart = parts[parts.length - 1];
            return idPart.split("\\?")[0];
        } catch (Exception e) {
            return null;
        }
    }

    private static String fetchPlaylistTracks(String playlistId) throws Exception {
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
                trackNames.append((i + 1) + ". " + trackName + "\n");
            }
            return trackNames.toString();
        } else {
            return "Failed to retrieve tracks.";
        }
    }
}

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class WebPageSizeFetcher {

    private static final int DEFAULT_SIZE = 1024; // Default size in bytes if fetching fails.
    private static final String MASTER_NODE_HOST = "localhost"; // Replace with MasterNode's hostname/IP
    private static final int MASTER_NODE_PORT = 8080; // Port of the MasterNode

    /**
     * Fetches the size of the content at the given URL and sends it to the MasterNode.
     *
     * @param urlString The URL of the webpage to fetch.
     */
    public void fetchAndSendPageSize(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            // Extract content size from the header.
            String contentLength = connection.getHeaderField("Content-Length");
            int size = (contentLength != null) ? Integer.parseInt(contentLength) : DEFAULT_SIZE;

            System.out.println("Fetched size for URL: " + urlString + " -> " + size + " bytes.");

            // Send the URL and size to the MasterNode
            sendToMasterNode(urlString, size);

        } catch (Exception e) {
            System.err.println("Error fetching size for URL: " + urlString + " -> " + e.getMessage());
            sendToMasterNode(urlString, DEFAULT_SIZE); // Send with default size if fetching fails
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Sends the URL and size to the MasterNode.
     *
     * @param urlString The URL of the webpage.
     * @param size      The size of the webpage content in bytes.
     */
    private void sendToMasterNode(String urlString, int size) {
        try (Socket socket = new Socket(MASTER_NODE_HOST, MASTER_NODE_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            // Send the URL
            writer.println(urlString);

            System.out.println("Sent to MasterNode: URL=" + urlString + ", Size=" + size);

        } catch (Exception e) {
            System.err.println("Error sending to MasterNode: " + e.getMessage());
        }
    }

    /**
     * Main method for testing.
     */
    public static void main(String[] args) {
        WebPageSizeFetcher sizeFetcher = new WebPageSizeFetcher();

        // Example URLs to test.
        String[] testUrls = {
            "https://www.example.com",
            "https://www.nonexistentwebsite12345.com",
            "https://www.google.com",
            "https://www.youtube.com"
        };

        for (String url : testUrls) {
            sizeFetcher.fetchAndSendPageSize(url);
        }
    }
}

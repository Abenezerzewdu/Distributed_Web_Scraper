import java.io.*;
import java.net.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraperServer {
    private static final int SERVER_PORT = 8083; // Change this for different instances
    private static final String SERVER_HOST = "localhost"; // Replace with actual host if needed

    public static void main(String[] args) {
        System.out.println("WebScraperServer is running on port " + SERVER_PORT);

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClientRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String url = reader.readLine();
            System.out.println("Received URL for scraping: " + url);

            // Perform the scraping
            String scrapedContent = scrapeUrl(url);

            // Send the result back to MasterNode
            writer.println(scrapedContent);
            System.out.println("Scraping complete for: " + url);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String scrapeUrl(String url) {
        StringBuilder result = new StringBuilder();
        try {
            // Connect to the URL and parse the HTML
            Document doc = Jsoup.connect(url).get();

            // Extract the title
            String title = doc.title();
            result.append("Title: ").append(title).append("\n");

            // Extract links (anchor tags)
            Elements links = doc.select("a[href]");
            int count = 0;
            for (Element link : links) {
                if (count < 2) { // Get only 2 links
                    result.append("Link: ").append(link.attr("abs:href")).append("\n");
                    count++;
                } else {
                    break;
                }
            }

            // Optionally, you can extract other components like meta tags, headers, etc.
            Elements metaTags = doc.select("meta[name]");
            for (Element metaTag : metaTags) {
                result.append("Meta: ").append(metaTag.attr("name")).append(" = ").append(metaTag.attr("content")).append("\n");
            }

        } catch (IOException e) {
            result.append("Error fetching URL: ").append(e.getMessage());
        }
        return result.toString();
    }
}
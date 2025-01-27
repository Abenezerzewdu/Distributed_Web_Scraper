import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebScraper extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Web Scraper");

        // Create UI components
        Label urlLabel = new Label("Enter URL:");
        TextField urlField = new TextField();
        Button scrapeButton = new Button("Scrape");
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);

        // Layout setup
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(urlLabel, urlField, scrapeButton, resultArea);

        // Scrape button action
        scrapeButton.setOnAction(e -> {
            String url = urlField.getText();
            if (url.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "URL cannot be empty!");
                return;
            }

            resultArea.clear();
            resultArea.appendText("Scraping started for URL: " + url + "\n");

            try {
                // Fetch and parse the HTML document
                int timeoutMillis = 30000;
                Document document = Jsoup.connect(url).timeout(timeoutMillis).get();

                // Extract and display various content
                resultArea.appendText("\n=== Links ===\n");
                Elements links = document.select("a[href]");
                for (Element link : links) {
                    resultArea.appendText(link.attr("href") + " - " + link.text() + "\n");
                }

                resultArea.appendText("\n=== Images ===\n");
                Elements images = document.select("img[src]");
                for (Element image : images) {
                    resultArea.appendText("Image: " + image.attr("src") + " (alt: " + image.attr("alt") + ")\n");
                }

                resultArea.appendText("\n=== Metadata ===\n");
                Elements metaTags = document.select("meta[name], meta[property]");
                for (Element metaTag : metaTags) {
                    resultArea.appendText(metaTag.attr("name") + " | " + metaTag.attr("property") + ": " + metaTag.attr("content") + "\n");
                }

                resultArea.appendText("\n=== Headings ===\n");
                Elements headings = document.select("h1, h2, h3, h4, h5, h6");
                for (Element heading : headings) {
                    resultArea.appendText(heading.tagName() + ": " + heading.text() + "\n");
                }

                resultArea.appendText("\n=== Paragraphs ===\n");
                Elements paragraphs = document.select("p");
                for (Element paragraph : paragraphs) {
                    resultArea.appendText(paragraph.text() + "\n");
                }

                resultArea.appendText("\n=== Tables ===\n");
                Elements tables = document.select("table");
                for (Element table : tables) {
                    Elements rows = table.select("tr");
                    for (Element row : rows) {
                        Elements cells = row.select("th, td");
                        for (Element cell : cells) {
                            resultArea.appendText(cell.text() + "\t");
                        }
                        resultArea.appendText("\n");
                    }
                }

                resultArea.appendText("\nScraping completed successfully!");

            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to scrape the URL. Details: " + ex.getMessage());
            }
        });

        // Create and set the scene
        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

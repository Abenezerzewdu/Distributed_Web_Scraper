
import java.util.List;

public class WebScrapingTask implements Comparable<WebScrapingTask> {
    private String url;                // The URL to scrape
    private long estimatedSize;        // Estimated size of the content
    private String pageTitle;          // The title of the page
    private String metaDescription;    // Meta description of the page
    private List<String> imageUrls;    // List of image URLs

    // Constructor
    public WebScrapingTask(String url, long estimatedSize, String pageTitle, String metaDescription, List<String> imageUrls) {
        this.url = url;
        this.estimatedSize = estimatedSize;
        this.pageTitle = pageTitle;
        this.metaDescription = metaDescription;
        this.imageUrls = imageUrls;
    }

    // Getters
    public String getUrl() {
        return url;
    }

    public long getEstimatedSize() {
        return estimatedSize;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    // Compare method to sort tasks by estimated size
    @Override
    public int compareTo(WebScrapingTask other) {
        return Long.compare(this.estimatedSize, other.estimatedSize);
    }
}
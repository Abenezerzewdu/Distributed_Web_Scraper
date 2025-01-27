import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class MasterNode {
    private static final int MASTER_PORT = 8080;
    private static final String MASTER_NODE_HOST = "localhost"; // Replace with MasterNode's hostname/IP

    // Priority queue to hold URLs sorted by size and priority (YouTube URLs first)
    private static final PriorityBlockingQueue<URLTask> taskQueue = new PriorityBlockingQueue<>(
        10,
        Comparator.comparing((URLTask task) -> !task.isYouTube()) // Prioritize YouTube (false < true)
                  .thenComparingLong(URLTask::getSize)            // Then by size
    );

    // List of server nodes
    private static final List<String> serverNodes = Arrays.asList(
        "localhost:8081",
        "localhost:8082",
        "localhost:8083"
    );

    public static void main(String[] args) {
        System.out.println("MasterNode is running on port " + MASTER_PORT);

        // Start the task distributor thread
        new Thread(MasterNode::distributeTasks).start();

        try (ServerSocket serverSocket = new ServerSocket(MASTER_PORT)) {
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
            System.out.println("Received URL: " + url);

            // Get content size and add to the queue
            long size = getContentSize(url);
            taskQueue.offer(new URLTask(url, size));
            logQueueContents(); // Log the current state of the queue

            writer.println("URL added to the queue with size: " + size);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long getContentSize(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return connection.getContentLengthLong();
        } catch (IOException e) {
            return Long.MAX_VALUE; // Default to max value if size is unknown
        }
    }

    private static void distributeTasks() {
        int serverIndex = 0;
        while (true) {
            try {
                URLTask task = taskQueue.poll();
                if (task == null) {
                    Thread.sleep(1000); // Wait if no tasks are available
                    continue;
                }

                String server = serverNodes.get(serverIndex);
                String[] serverDetails = server.split(":");
                String host = serverDetails[0];
                int port = Integer.parseInt(serverDetails[1]);

                String result = sendTaskToServer(host, port, task.getUrl());
                System.out.println("Result from server " + server + ": " + result);
                logQueueContents(); // Log the current state of the queue

                // Round-robin distribution
                serverIndex = (serverIndex + 1) % serverNodes.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String sendTaskToServer(String host, int port, String url) {
        try (
            Socket socket = new Socket(host, port);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            writer.println(url); // Send URL to the server
            return reader.readLine(); // Receive result from the server
        } catch (IOException e) {
            return "Error communicating with server " + host + ":" + port;
        }
    }

    private static void logQueueContents() {
        System.out.println("Current Queue State:");
        for (URLTask task : taskQueue) {
            System.out.println(task);
        }
        System.out.println("----- End of Queue -----");
    }

    private static class URLTask {
        private final String url;
        private final long size;

        public URLTask(String url, long size) {
            this.url = url;
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public long getSize() {
            return size;
        }

        public boolean isYouTube() {
            return url.contains("youtube.com");
        }

        @Override
        public String toString() {
            return "URLTask{url='" + url + "', size=" + size + ", priority=" + (isYouTube() ? "YouTube" : "Normal") + "}";
        }
    }
}

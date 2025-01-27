import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HeartbeatSender implements Runnable {
    private final String serverId;
    private final String managerHost;
    private final int managerPort;

    public HeartbeatSender(String serverId, String managerHost, int managerPort) {
        this.serverId = serverId;
        this.managerHost = managerHost;
        this.managerPort = managerPort;
    }

    @Override
    public void run() {
        while (true) {
            try (Socket socket = new Socket(managerHost, managerPort);
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                writer.println("HEARTBEAT:" + serverId); // Send heartbeat
            } catch (IOException e) {
                System.err.println("Error sending heartbeat: " + e.getMessage());
            }

            try {
                Thread.sleep(5000); // Send heartbeat every 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Server {

    public Consumer<Socket> getConsumer() {
        return (clientSocket -> {
            try (PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true)) {
                toClient.println("Hello from server! | client - " + clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        int port = 8000;
        int poolSize = 100;  

        Server server = new Server();
        ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            while (true) {
                try {
                    Socket acceptedSocket = serverSocket.accept();
                    System.out.println("Accepted: " + acceptedSocket.getRemoteSocketAddress() + " " + acceptedSocket.getInetAddress());

                    threadPool.submit(() -> server.getConsumer().accept(acceptedSocket));
                } catch (IOException e) {
                    System.err.println("Accept failed: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
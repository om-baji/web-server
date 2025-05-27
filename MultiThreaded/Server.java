import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server {

    public Consumer<Socket> getConsumer() {
        return (clientSocket -> {
            try {
                PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream());
                toClient.println("Hello from server!");
                toClient.close();
                clientSocket.close();   
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public static void main(String[] args) {
        int port = 8000;
        Server server = new Server();
        try {
            ServerSocket socket = new ServerSocket(port);
            socket.setSoTimeout(10000);
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket acceptedSocket = socket.accept();
                System.out.println("Socket accepted " + acceptedSocket.getRemoteSocketAddress());
                Thread thread = new Thread(() -> server.getConsumer().accept(acceptedSocket));
                thread.start();
            }   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

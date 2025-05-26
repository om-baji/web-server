import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public void run() throws IOException{
        try {
            int port = 8000;
            ServerSocket socket = new ServerSocket(port);
            socket.setSoTimeout(10000);
            while (true) {
                System.out.println("Server listerning on port " + port);
                Socket acceptedSocket = socket.accept();

                System.out.println("Accepted connection " + acceptedSocket.getRemoteSocketAddress());
                PrintWriter toClient = new PrintWriter(acceptedSocket.getOutputStream());
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(acceptedSocket.getInputStream()));

                toClient.println("Hello from server");  
                toClient.close();
                fromClient.close();
                acceptedSocket.close(); 
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
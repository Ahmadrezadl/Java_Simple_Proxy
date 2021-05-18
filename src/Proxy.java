import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Proxy {
    private ServerSocket serverSocket;
    public Proxy(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        while(true)
        {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New Request Received");
                RequestHandler handler = new RequestHandler(socket);
                Thread thread = new Thread(handler);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

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
                RequestHandler handler = new RequestHandler();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

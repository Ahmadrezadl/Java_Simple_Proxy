import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private static final int DEFAULT_PORT = 1234;

    public static void main(String[] args) {
        int port;
        try{
            port = Integer.parseInt(args[0]);
        }
        catch (Exception e) {
            System.out.println("Can't read port from application args.\nrunning on default port : 1234");
            port = DEFAULT_PORT;
        }
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/",new RequestHanlder());
            server.start();
        } catch (IOException e) {
            System.out.println("Cannot open server on port " + port);
            System.exit(-1);
        }
    }
}

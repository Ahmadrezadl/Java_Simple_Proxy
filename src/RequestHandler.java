import java.io.*;
import java.net.*;

public class RequestHandler implements Runnable{
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private String fullUrl;
    private String method;

    public RequestHandler(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            String [] methodAndUrl = reader.readLine().split("\\s+");
            method = methodAndUrl[0];
            fullUrl = methodAndUrl[1];
            System.out.println(method + " " + fullUrl);
            if(fullUrl.matches(".*soft98.*"))
            {
                return;
            }
            if(!fullUrl.startsWith("http")){
                fullUrl = "http://" + fullUrl;
            }
            if(method.equals("CONNECT")){
                handleConnect();
            }
            else{
                handleRequest();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest() {
        try {
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            for (int i = 0; i < 5; i++) {
                System.out.println(reader.readLine());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleConnect() throws IOException {
        String [] urlAndPort = fullUrl.replaceFirst("htt((p)|(ps))://" , "").split(":");
        String url = urlAndPort[0];
        int port = Integer.parseInt(urlAndPort[1]);
        for(int i=0;i<5;i++){
            System.out.println(reader.readLine());
        }
        InetAddress address = InetAddress.getByName(url);
        Socket proxy = new Socket(address, port);
        String line = "HTTP/1.0 200 Connection established\r\nProxy-Agent: JavaProxy/1.0\r\n\r\n";
        writer.write(line);
        writer.flush();
        DataForwarder forwarder1 = new DataForwarder(socket.getInputStream(), proxy.getOutputStream());
        Thread thread1 = new Thread(forwarder1);
        thread1.start();
        DataForwarder forwarder2 = new DataForwarder(proxy.getInputStream(), socket.getOutputStream());
        Thread thread2 = new Thread(forwarder2);
        thread2.start();
    }
}

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
//http://ahmadrezadownload.loxblog.com/
    private void handleRequest()  {
        try {
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
//            var bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            for(int i = 0; i <10;i++)
//            System.out.println(bf.readLine());
            int contentLength = 0;
            while (true) {
                String s = reader.readLine();
                System.out.println(s);
                if(s.equals(""))break;
                String key = s.split(":",2)[0];
                String value = s.split(":",2)[1].substring(1);
                connection.setRequestProperty(key,value);
                if(key.equals("Content-Length"))
                    contentLength = Integer.parseInt(value);

            }
            for (int i = 0; i < contentLength; i++) {
                connection.getOutputStream().write(reader.read());
            }
            connection.getOutputStream().flush();
            System.out.println("ANSWERING...");
            String line = connection.getHeaderField(null) + "\r\n";
            for(var entry : connection.getHeaderFields().entrySet())
            {
                if(entry.getKey() == null)continue;
                String value = "";
                for(String s : entry.getValue())
                    value += s+",";
                value = value.substring(0,value.length()-1);
                line += entry.getKey() + ": " + value + "\r\n";
            }
            line+="\r\n";
            byte [] res = connection.getInputStream().readAllBytes();
            line+= res.length + "\r\n";

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(line.getBytes());
            baos.write(res);

            System.out.println(baos.toString());
            socket.getOutputStream().write(baos.toByteArray());


            socket.getOutputStream().flush();
            socket.getOutputStream().close();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleConnect() throws IOException {
        System.out.println(fullUrl);
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

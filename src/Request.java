import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class Request {

    private final Headers headers;
    private final InputStream body;
    private final String url;
    private final String method;

    public Request(HttpExchange exchange) throws IOException {
        url = exchange.getRequestURI().toString();
        method = exchange.getRequestMethod();
        headers = exchange.getRequestHeaders();
        body = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(body));
        String line;
        while ((line=reader.readLine()) != null && line.length()!=0)
        {
            System.out.println(line);

        }

    }


    public void forward() {

    }

    @Override
    public String toString() {
        return method + " " + url;
    }
}

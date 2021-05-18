import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class DataForwarder implements Runnable{
    private final InputStream input;
    private final OutputStream output;

    public DataForwarder(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[4096];
            int read;
            do {
                read = input.read(buffer);
                if (read > 0) {
                    output.write(buffer, 0, read);
                    if (input.available() < 1) {
                        output.flush();
                    }
                }
            } while (read >= 0);
        }
        catch (SocketTimeoutException ste) {
            // TODO: handle exception
        }
        catch (IOException e) {
            System.out.println("Proxy to client HTTPS read timed out");
            e.printStackTrace();
        }
    }
}

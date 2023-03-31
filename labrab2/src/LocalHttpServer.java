import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalHttpServer {
    public static final int RESPONSE_LENGTH = 400;
    private final int port;
    private final int pool_size;
    private boolean stopped;

    public LocalHttpServer(int port, int pool_size) {
        this.port = port;
        this.pool_size = pool_size;
    }

    public void run() {
        try {
            var server_socket = new ServerSocket(port);
            var pool = Executors.newFixedThreadPool(this.pool_size);
            while (!stopped) {
                var socket = server_socket.accept();
                System.out.println("Socket accepted");
                pool.submit(() -> processSocket(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processSocket(Socket socket) {
        try (socket;
             var input_stream = new DataInputStream(socket.getInputStream());
             var output_stream = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Request: " + new String(input_stream.readNBytes(RESPONSE_LENGTH)));

            Thread.sleep(1000);

            var body = Files.readAllBytes(Path.of("resources", "index.html"));
            var headers = """
                    HTTP/1.1 200 OK
                    content-type: text/html
                    content-length: %s
                    """.formatted(body.length).getBytes();
            var line_separator = System.lineSeparator().getBytes();

            output_stream.write(headers);
            output_stream.write(line_separator);
            output_stream.write(body);
            output_stream.write(line_separator);
            output_stream.write(line_separator);
        }
        // TODO: log error msg
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}

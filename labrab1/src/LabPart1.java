import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class LabPart1 {
    public static void sendAndReceiveFromServer(String host, int port) {
        try {
            // create socket using host and port
            var socket = new Socket();
            // using connect so we can set timeout for connecting
            socket.connect(
                    new InetSocketAddress(
                            Inet4Address.getByName(host),
                            port
                    ),
            2000);

            // create io streams
            var out_stream = new DataOutputStream(socket.getOutputStream());
            var in_stream = new DataInputStream(socket.getInputStream());

            String request = "HEAD / HTTP/2.0\r\n" +
                    "Host: " + host + "\r\n" +
                    "Connection: close\r\n\r\n";
            out_stream.write(request.getBytes());
            System.out.println(new String(in_stream.readAllBytes(), StandardCharsets.UTF_8));

            in_stream.close();
            out_stream.close();

            socket.close();
        }
        catch (UnknownHostException e) {
            System.err.println("Неизвестный хост");
        }
        catch (ConnectException e) {
            System.err.println("Время ожидания истекло");
        }
        catch (EOFException e) {
            System.err.println("Разрыв соеденения");
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

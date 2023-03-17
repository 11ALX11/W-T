import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class DatagramServer {
    public int port = 0;

    public DatagramServer(int port) { this.port = port; }
    public void startServer() {
        try (var datagram_server_socket = new DatagramSocket(this.port)) {
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true) {
                long timer = System.nanoTime();
                datagram_server_socket.receive(packet);
                System.out.print(System.nanoTime() - timer); System.out.println("ns");

                byte[] bytes = new byte[2048];
                for (int i = 0; i+1 < buffer.length; i++) {
                    bytes[i] = buffer[i+1];
                }

                if (buffer[0] == 'i') {
                    int number = ByteBuffer.wrap(bytes).getInt();
                    System.out.println(number);
                }
                else if (buffer[0] == 's') {
                    String str = new String(bytes);
                    System.out.println(str);
                }
                else {
                    String str = new String(bytes);
                    System.out.println(str);
                }
            }
        }
        catch (SocketException e) {
            System.err.println(e.getMessage());
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

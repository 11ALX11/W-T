import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class DatagramClient {
    public int port;
    public String address = "localhost";

    public DatagramClient(int port) { this.port = port; }
    public DatagramClient(String address, int port) { this.address = address; this.port = port; }

    public void startClient() {
        try (var datagram_socket = new DatagramSocket()) {
            char type = 's'; // 's';
            byte[] buffer;
            byte[] bytes;

            if ( type == 'i') {
                int request = 101101;
                bytes = ByteBuffer.allocate(4).putInt(request).array();
                buffer = new byte[5];
                buffer[0] = 'i';
            }
            else {
                String request = "String!\n";
                bytes = request.getBytes();
                buffer = new byte[bytes.length + 1];
                buffer[0] = 's';
            }

            System.arraycopy(bytes, 0, buffer, 1, bytes.length);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, new InetSocketAddress(this.address, this.port));

            for (int i = 0; i < 20; i++) {
                datagram_socket.send(packet);
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

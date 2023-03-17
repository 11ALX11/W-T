import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

public class SimpleClient {
    public static void startClientConnection(String address, int port) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter address (or empty):");
        String addr = scanner.nextLine();
        if (!addr.equalsIgnoreCase("")) {
            address = addr;
        }

        System.out.println("Enter port (or 0):");
        int prt = scanner.nextInt();
        if (prt != 0) {
            port = prt;
        }

        var socket = new Socket();
        try {
            socket.connect(
                    new InetSocketAddress(
                            address,
                            port
                    ),
                    2000
            );

            var out_stream = new DataOutputStream(socket.getOutputStream());
            var in_stream = new DataInputStream(socket.getInputStream());

            Date date = new Date();
            while (scanner.hasNextLine()) {
                String request = scanner.nextLine();
                out_stream.writeUTF(request);
                String response = in_stream.readUTF();
                System.out.println(date.toString() + ": Response from server: " + response);
            }

            in_stream.close();
            out_stream.close();
        }
        catch (UnknownHostException e) {
            System.err.println("Неизвестный адрес");
        }
        catch (SocketTimeoutException e) {
            System.err.println("Время ожидания истекло");
        }
        catch (EOFException e) {
            System.out.println("Разрыв соеденения");
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        finally {
            try {
                socket.close();
            }
            catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}

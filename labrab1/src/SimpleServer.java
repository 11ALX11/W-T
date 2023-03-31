import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

public class SimpleServer implements Runnable {
    private int state = 0; // 0 - normal; 1 - pause;
    protected volatile boolean is_stop = false;
    protected Socket client_socket;
    protected SimpleServer parent;
    int port;

    public SimpleServer(int port) {
        this.port = port;
    }
    public SimpleServer(Socket client_socket, SimpleServer parent) {
        this.client_socket = client_socket;
        this.parent = parent;
    }

    public void startServer() {
        try (var server_socket = new ServerSocket(this.port)) {
            logData("Server started on " + this.port + " port.");
            server_socket.setSoTimeout(2000);

            int cnt = 0;
            ArrayList<Thread> threads = new ArrayList<>();
            ArrayList<Socket> client_sockets = new ArrayList<>();
            while (!isStop()) {
                try {
                    Socket client_socket = server_socket.accept();
                    logData(client_socket.toString() + " connection.");
                    client_sockets.add(cnt, client_socket);
                    threads.add(cnt, new Thread(new SimpleServer(client_socket, this)));
                    threads.get(cnt++).start();
                }
                catch (SocketTimeoutException e) {
                    // Timeout for checking isStop()
                    // Continue while
                }
            }

            for (int i = 0; i < threads.size(); i++) {
                // close sockets and interrupt threads
                client_sockets.get(i).close();
                threads.get(i).interrupt();
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

        logData("Server stop.");
    }

    public void startSingleServerThread(Socket client_socket) {

        try {

            // to client
            var out_stream = new DataOutputStream(client_socket.getOutputStream());
            // from client
            var in_stream = new DataInputStream(client_socket.getInputStream());

//            Scanner scanner = new Scanner(System.in);
            Date date = new Date();
            String server_response = "";

            byte[] bytes = new byte[2048];
            in_stream.read(bytes);
            String client_request = new String(bytes, StandardCharsets.UTF_8)
                    .replace("\0", "")
                    .replace("\n", "")
                    .replace("\r", "");

            System.out.println("Client request: " + client_request);
            long timer = System.nanoTime();

            while (!client_request.equalsIgnoreCase(";close") &&
                   !client_request.equalsIgnoreCase(";stop"
            )) {
                if (parent.getState() == 0 && client_request.equalsIgnoreCase(";pause")) {
                    parent.setState(1);

                    server_response = "Server is paused.";
                    logData(server_response);
                    server_response += "\n";
                }
                else if (client_request.equalsIgnoreCase(";unpause")) {
                    if (parent.getState() == 1) {
                        parent.setState(0);

                        server_response = "Server is unpaused now.";
                        logData(server_response);
                        server_response += "\n";
                    }
                    else {
                        server_response = "Server isn't paused.\n";
                        logData(server_response);
                    }
                }
                else if (parent.getState() == 0 && client_request.equalsIgnoreCase(";time")) {
                    server_response = date.toString();
                    logData("Called ';time': " + server_response + ".");
                    server_response += "\n";
                }
                else if (parent.getState() == 0 && client_request.startsWith(";")) {
                    server_response = "Wrong command!\n";
                }
                else if (parent.getState() == 0) {
                    server_response = client_request + "_SERVER\n";//scanner.nextLine();
                }
                else if (parent.getState() == 1) {
                    server_response = "";
                }

                out_stream.writeUTF(server_response);
                if (parent.getState() != 1) logData("Request completed in " + (System.nanoTime() - timer) + "ns");

//                client_request = in_stream.readUTF();
                bytes = new byte[2048];
                in_stream.read(bytes);
                client_request = new String(bytes, StandardCharsets.UTF_8)
                        .replace("\0", "")
                        .replace("\n", "")
                        .replace("\r", "");
                System.out.println("Client request: " + client_request);

                timer = System.nanoTime();
            }

            if (client_request.equalsIgnoreCase(";stop")) {
                logData("Called ';stop'.");
                parent.stopServer();
            }
            if (client_request.equalsIgnoreCase(";close")) {
                logData("Called ';close'.");
            }

            out_stream.close();
            in_stream.close();

            client_socket.close();
        }
        catch (SocketException e) {
            System.out.println("Сокет закрыт");
        }
        catch (EOFException e) {
            System.out.println("Разрыв соеденения");
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        finally {
            try {
                client_socket.close();
                logData(client_socket + " connection end.");
            }
            catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean isStop() {
        return this.is_stop;
    }
    public void stopServer() {
        this.is_stop = true;
    }

    public void setState(int new_state) {
        this.state = new_state;
        logData("State set to " + new_state);
    }
    public int getState() { return this.state; }

    protected static synchronized void logData(String data) {
        try(FileWriter writer = new FileWriter("log.txt", true))
        {
            Date date = new Date();
            writer.write(date + ": " + data + "\n");
            writer.flush();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        this.startSingleServerThread(this.client_socket);
    }

}

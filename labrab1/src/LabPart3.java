public class LabPart3 {
    public static void startServer(int port) {
        new DatagramServer(port).startServer();
    }

    public static void startClient(String address, int port) {
        new DatagramClient(address, port).startClient();
    }
}

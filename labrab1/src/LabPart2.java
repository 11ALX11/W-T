public class LabPart2 {
    public static void startServer(int port) {
        new SimpleServer(port).startServer();
    }

    public static void startClient(String address, int port) {
        SimpleClient.startClientConnection(address, port);
    }
}

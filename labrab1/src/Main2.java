public class Main2 {
    public static void main(String[] args) {
        String address = "localhost";
        int port = 6544;
        if (args.length > 0 && args[0].equalsIgnoreCase("-h")) {
            LabPart2.startServer(port);
        } else LabPart2.startClient(address, port);
    }
}

public class Main3 {
    public static void main(String[] args) {
        String address = "localhost";
        int port = 6542;
        if (args.length > 0 && args[0].equalsIgnoreCase("-h")) {
            LabPart3.startServer(port);
        } else {
            LabPart3.startClient(address, port);
        }
    }
}

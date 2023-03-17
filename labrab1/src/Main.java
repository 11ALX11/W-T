public class Main {
    public static void main(String[] args) {
        String HOST1 = "www.google.com";
        String HOST2 = "developer.mozilla.org";
        int PORT = 443; //http: 80; https: 443; telnet: 23; ssh: 22.

        LabPart1.executeLP(HOST1, PORT);
        System.out.print("\n\n\n");
        LabPart1.executeLP(HOST2, 80);
        System.out.print("\n\n\n");
        LabPart1.executeLP("www.bstu.by", 23);
        System.out.print("\n\n\n");
        LabPart1.executeLP("www.bstu.by", 22);

    }
}
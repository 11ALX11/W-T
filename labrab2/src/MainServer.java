public class MainServer {
    public static void main(String[] args) {
        new LocalHttpServer(9000, 10).run();
    }
}

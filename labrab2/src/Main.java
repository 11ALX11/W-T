import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        try {
            new LocalHttpClient().startClient();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static java.net.http.HttpRequest.BodyPublishers.ofFile;

public class LocalHttpClient {
    public void startClient() throws FileNotFoundException, ExecutionException, InterruptedException {
        var http_client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9000"))
                .header("content-type", "application/json")
                .POST(ofFile(Path.of("resources", "example.json")))
                .build();

        var response1 = http_client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        var response2 = http_client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        var response3 = http_client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response3.get().body());
    }
}

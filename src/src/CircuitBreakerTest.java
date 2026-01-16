import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CircuitBreakerTest implements experiment {

    private static final String URL = "http://localhost:8080/search";
    private static final String TOKEN =
            "";
    
    @Override
    public void perform() throws InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        for (int i = 0; i < 11; i++) {
            try {
                String query = generateQuery(i);

                String body = """
                        {
                          "query": "%s",
                          "queryType": "Test"
                        }
                        """.formatted(query);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(URL))
                        .timeout(Duration.ofSeconds(2))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + TOKEN)
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("[REQ " + i + "] status=" + response.statusCode());

            } catch (Exception e) {
                System.out.println("[REQ " + i + "] FAILED : " + e.getClass().getSimpleName());
            }

            Thread.sleep(100);
        }

        System.out.println("==== Test finish ====");
        System.out.println("check your server log");
    }

    private static String generateQuery(int index) {
        char a = (char) ('a' + index);
        char b = (char) ('b' + index);
        char c = (char) ('c' + index);
        return "" + a + b + c;
    }
}

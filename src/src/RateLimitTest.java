import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RateLimitTest implements experiment {
    private static final String URL = "http://localhost:8080/search";
    private static final String TOKEN =
            "";

    @Override
    public void perform() throws InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        List<CompletableFuture<HttpResponse<String>>> futures = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            final int idx = i;
            CompletableFuture<HttpResponse<String>> future = new CompletableFuture<>();
            futures.add(future);

            scheduler.schedule(() -> {
                try {
                    String query = generateQuery(idx);

                    String body = """
                            {
                              "query": "%s",
                              "queryType": "Keyword"
                            }
                            """.formatted(query);

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(URL))
                            .timeout(Duration.ofSeconds(5))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + TOKEN)
                            .POST(HttpRequest.BodyPublishers.ofString(body))
                            .build();

                    HttpResponse<String> response =
                            client.send(request, HttpResponse.BodyHandlers.ofString());

                    future.complete(response);

                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }, idx * 90, TimeUnit.MILLISECONDS); // 11 req / 1초
        }

        scheduler.shutdown();
        scheduler.awaitTermination(2, TimeUnit.SECONDS);

        HttpResponse<String> last = futures.get(10).join();

        System.out.println("===== LAST RESPONSE =====");
        System.out.println("STATUS : " + last.statusCode());
        System.out.println("BODY");
        System.out.println(last.body());
    }

    private static String generateQuery(int index) {
        char a = (char) ('a' + index);
        char b = (char) ('b' + index);
        char c = (char) ('c' + index);
        return "" + a + b + c;
    }
}

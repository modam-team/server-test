import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AverageResponseTimeTest implements experiment {

    private static final String URL = "";
    private static final String TOKEN =
            "";

    private static final String[] QUERIES = {
            "자바",
            "파이썬",
            "러스트",
            "빨강색",
            "주황색",
            "노랑색",
            "초록색",
            "파랑색",
            "남색",
            "보라색"
    };

    @Override
    public void perform() throws InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        long totalTimeMs = 0;

        for (int i = 0; i < QUERIES.length; i++) {
            try {
                String body = """
                        {
                          "query": "%s",
                          "queryType": "Test"
                        }
                        """.formatted(QUERIES[i]);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(URL))
                        .timeout(Duration.ofSeconds(5))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + TOKEN)
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                long start = System.nanoTime();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                long end = System.nanoTime();
                long elapsedMs = (end - start) / 1_000_000;

                totalTimeMs += elapsedMs;

                System.out.println(
                        "[REQ " + (i + 1) + "] query=" + QUERIES[i]
                                + " status=" + response.statusCode()
                                + " time=" + elapsedMs + "ms"
                );

            } catch (Exception e) {
                System.out.println(
                        "[REQ " + (i + 1) + "] query=" + QUERIES[i]
                                + " FAILED : " + e.getClass().getSimpleName()
                );
            }

            Thread.sleep(100);
        }

        double average = totalTimeMs / (double) QUERIES.length;

        System.out.println("==== Test finish ====");
        System.out.println("Average response time = " + average + " ms");
    }
}


import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws Exception {
        //experiment ex = new RateLimitTest();
        experiment ex = new CircuitBreakerTest();
        ex.perform();
    }
}

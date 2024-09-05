package Requests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Network service to execute HTTP requests
 */
public class Service {
    HttpClient client;
    HttpRequest request;

    /**
     * Instantiates a new HTTP client on construction
     */
    public Service() {
        this.client = HttpClient.newHttpClient();
    }

    /**
     * Builds & executes an HTTP request using the CoinGecko API for the coin passed as param
     * @param coin String
     * @return String
     * @throws IOException Network Exception
     * @throws InterruptedException Network Exception
     */
    public String fetchData(String coin, String interval) throws IOException, InterruptedException {
        this.request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.coingecko.com/api/v3/coins/"+coin+"/ohlc?vs_currency=usd&days="+interval))
                .build();
        HttpResponse<String> response = client.send(this.request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}

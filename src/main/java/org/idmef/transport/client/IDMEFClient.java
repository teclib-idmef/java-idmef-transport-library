package org.idmef.transport.client;

import org.idmef.IDMEFObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Client part of the IDMEF transport.
 *
 * This implementation provides:
 * <ul>
 *     <li>IDMEF message sending over HTTP</li>
 * </ul>
 *
 */
public class IDMEFClient {
    private HttpClient httpClient;
    private String url;

    /**
     * Initialize a IDMEFClient
     *
     * @param url the URL of the server, for instance "http://127.0.0.1:9999"
     */
    public IDMEFClient(String url) {
        HttpClient.Builder builder = HttpClient.newBuilder();

        builder.version(HttpClient.Version.HTTP_1_1);
        builder.followRedirects(HttpClient.Redirect.NORMAL);
        builder.connectTimeout(Duration.ofSeconds(20));

        httpClient = builder.build();

        this.url = url;
    }

    /**
     * Send a IDMEF message other HTTP using a POST request
     *
     * Call is synchronous and will block until message is sent.
     *
     * @param message the message to send
     * @return the HTTP response
     * @throws IOException if an I/O error occurred
     * @throws InterruptedException if call was interrupted
     */
    public HttpResponse<String> send(IDMEFObject message) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        builder.timeout(Duration.ofSeconds(30));
        builder.header("Content-Type", "application/json");
        builder.POST(HttpRequest.BodyPublishers.ofByteArray(message.serialize()));

        HttpRequest request = builder.build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}

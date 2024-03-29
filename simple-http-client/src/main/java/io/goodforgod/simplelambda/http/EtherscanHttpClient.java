package io.goodforgod.simplelambda.http;

import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpClient;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpRequest;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpResponse;
import io.goodforgod.http.common.HttpMethod;
import io.goodforgod.http.common.HttpStatus;
import io.goodforgod.http.common.exception.HttpStatusException;
import io.goodforgod.http.common.uri.URIBuilder;
import java.net.URI;
import java.time.Duration;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.08.2021
 */
public class EtherscanHttpClient {

    private final URI baseUri;
    private final Converter converter;
    private final SimpleHttpClient httpClient;

    public EtherscanHttpClient(Converter converter, SimpleHttpClient httpClient) {
        this.converter = converter;
        this.httpClient = httpClient;
        this.baseUri = URIBuilder.of("https://api.etherscan.io").path("/api")
                .param("module", "block")
                .param("action", "getblockreward")
                .build();
    }

    public EtherscanBlock getBlockByNumber(int blockNumber) {
        final URI uri = URIBuilder.of(baseUri)
                .param("blockno", blockNumber)
                .build();

        final SimpleHttpRequest request = SimpleHttpRequest.builder(uri)
                .method(HttpMethod.GET)
                .timeout(Duration.ofSeconds(10))
                .build();

        final SimpleHttpResponse httpResponse = httpClient.execute(request);
        if (!httpResponse.status().equals(HttpStatus.OK)) {
            throw new HttpStatusException(httpResponse.status(), "Error retrieving block");
        }

        final EtherscanBlockResponse response = converter.fromString(httpResponse.bodyAsString(), EtherscanBlockResponse.class);
        if (response.status().equals("1")) {
            return response.result();
        } else {
            final int statusCode = Integer.parseInt(response.status());
            throw new HttpStatusException(HttpStatus.valueOf(statusCode), response.message());
        }
    }
}

package io.goodforgod.simplelambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 10.09.2022
 */
public class LambdaHandler implements RequestHandler<Request, Response> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private MinioClient httpClient;

    @Override
    public Response handleRequest(Request request, Context context) {
        logger.info("Processing User with name: {}", request.name());

        if (httpClient == null) {
            final String region = getEnvOrThrow("S3_REGION");
            this.httpClient = MinioClient.builder()
                    .endpoint("s3.amazonaws.com")
                    .region(region)
                    .build();
        }

        try {
            final String bucket = getEnvOrThrow("S3_BUCKET");
            final String value = String.format("{\"name\":\"%s\"}", request.name());
            final byte[] valueAsBytes = value.getBytes(StandardCharsets.UTF_8);
            this.httpClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(request.name())
                    .contentType("application/json")
                    .stream(new ByteArrayInputStream(valueAsBytes), valueAsBytes.length, -1)
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return new Response(UUID.randomUUID().toString(), "S3 Object put - " + request.name());
    }

    private static String getEnvOrThrow(String env) {
        final String regionStr = System.getenv(env);
        if (regionStr == null) {
            throw new IllegalArgumentException(env + " env is not set!");
        }

        return regionStr;
    }
}

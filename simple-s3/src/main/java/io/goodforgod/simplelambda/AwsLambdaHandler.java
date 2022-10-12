package io.goodforgod.simplelambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 10.09.2022
 */
public class AwsLambdaHandler implements RequestHandler<Request, Response> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AmazonS3 s3Client;

    @Override
    public Response handleRequest(Request request, Context context) {
        logger.info("Processing User with name: {}", request.name());

        if (s3Client == null) {
            final String region = getEnvOrThrow("S3_REGION");
            this.s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .enableForceGlobalBucketAccess()
                    .build();
        }

        try {
            final String bucket = getEnvOrThrow("S3_BUCKET");
            final String value = String.format("{\"name\":\"%s\"}", request.name());
            final byte[] valueAsBytes = value.getBytes(StandardCharsets.UTF_8);
            final ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(valueAsBytes.length);
            metadata.setContentType("application/json");
            try (var stream = new ByteArrayInputStream(valueAsBytes)) {
                this.s3Client.putObject(new PutObjectRequest(bucket, request.name(), stream, metadata));
            }
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

package io.goodforgod.simplelambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.graalvm.hint.annotation.ReflectionHint;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.logging.impl.SimpleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 10.09.2022
 */
@ReflectionHint(types = { LogFactory.class, LogFactoryImpl.class, SimpleLog.class })
public class AmazonV2LambdaHandler implements RequestHandler<Request, Response> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private S3Client s3Client;

    @Override
    public Response handleRequest(Request request, Context context) {
        logger.info("Processing User with name: {}", request.name());

        if (s3Client == null) {
            final Region region = Region.of(getEnvOrThrow("S3_REGION"));
            this.s3Client = S3Client.builder()
                    .region(region)
                    .credentialsProvider(AnonymousCredentialsProvider.create())
                    .build();
        }

        try {
            final String bucket = getEnvOrThrow("S3_BUCKET");
            final String value = String.format("{\"name\":\"%s\"}", request.name());
            final byte[] valueAsBytes = value.getBytes(StandardCharsets.UTF_8);
            final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .key(request.name())
                    .bucket(bucket)
                    .contentType("application/json")
                    .build();
            this.s3Client.putObject(putObjectRequest, RequestBody.fromBytes(valueAsBytes));
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

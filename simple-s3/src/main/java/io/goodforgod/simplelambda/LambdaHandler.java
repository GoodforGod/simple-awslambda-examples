package io.goodforgod.simplelambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
            this.httpClient = MinioClient.builder()
                    .endpoint("https://play.min.io")
                    .region("")
                    .build();
        }

        try {
            final byte[] nameBytes = request.name().getBytes(StandardCharsets.UTF_8);
            this.httpClient.putObject(PutObjectArgs.builder()
                    .bucket("names")
                    .object(request.name() + "-" + UUID.randomUUID())
                    .contentType("json")
                    .stream(new ByteArrayInputStream(nameBytes), nameBytes.length, nameBytes.length)
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        return new Response(UUID.randomUUID().toString(), "S3 Object put- " + request.name());
    }
}

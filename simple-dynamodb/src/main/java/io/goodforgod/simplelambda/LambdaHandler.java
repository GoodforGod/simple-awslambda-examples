package io.goodforgod.simplelambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.graalvm.hint.annotation.ReflectionHint;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.logging.impl.SimpleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 31.07.2021
 */
@ReflectionHint(types = { LogFactory.class, LogFactoryImpl.class, SimpleLog.class })
public class LambdaHandler implements RequestHandler<Request, Response> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DynamoDbClient client = DynamoDbClient.builder()
            .httpClient(ApacheHttpClient.create())
            .region(getRegion())
            .build();

    @Override
    public Response handleRequest(Request request, Context context) {
        logger.info("Processing User with name: {}", request.name());

        final String id = UUID.randomUUID().toString();
        final PutItemResponse response = client.putItem(PutItemRequest.builder()
                .tableName("Names")
                .item(Map.of("id", AttributeValue.builder().s(id).build(),
                        "name", AttributeValue.builder().s(request.name()).build()))
                .build());

        logger.info("DDB response code: {}", response.sdkHttpResponse().statusCode());
        return new Response(id, "Hello - " + request.name());
    }

    private static Region getRegion() {
        final String regionStr = System.getenv("DYNAMODB_REGION");
        if (regionStr == null) {
            throw new IllegalArgumentException("DYNAMODB_REGION env is not set!");
        }

        return Region.regions().stream()
                .filter(r -> r.id().equals(regionStr))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("AWS_REGION is not invalid value: " + regionStr));
    }
}

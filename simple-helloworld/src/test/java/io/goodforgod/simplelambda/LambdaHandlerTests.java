package io.goodforgod.simplelambda;

import io.goodforgod.aws.lambda.simple.testing.AwsLambdaAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class LambdaHandlerTests extends Assertions {

    @Test
    void eventHandled() {
        final Request request = new Request("Steeven King");
        final Response response = AwsLambdaAssertions.ofEntrypoint(new LambdaEntrypoint())
                .inputJson(request)
                .expectJson(Response.class);

        assertEquals("Hello - Steeven King", response.message());
    }
}

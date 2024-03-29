package io.goodforgod.simplelambda.micronaut;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.aws.lambda.simple.micronaut.MicronautInputLambdaEntrypoint;
import io.goodforgod.graalvm.hint.annotation.InitializationHint;
import io.goodforgod.graalvm.hint.annotation.NativeImageHint;
import io.goodforgod.simplelambda.micronaut.aop.PrintMethod;
import io.goodforgod.slf4j.simplelogger.SimpleLogger;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Singleton;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 31.07.2021
 */
@NativeImageHint(entrypoint = MicronautInputLambdaEntrypoint.class, name = "application")
@InitializationHint(types = SimpleLogger.class)
@Introspected
@Singleton
public class LambdaHandler implements RequestHandler<Request, Response> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PrintMethod
    @Override
    public Response handleRequest(Request request, Context context) {
        logger.info("Processing User with name: {}", request.name());
        return new Response(UUID.randomUUID().toString(), "Hello - " + request.name());
    }
}

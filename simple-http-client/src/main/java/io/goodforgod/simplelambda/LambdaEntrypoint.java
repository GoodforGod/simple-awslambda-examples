package io.goodforgod.simplelambda;

import io.goodforgod.aws.lambda.simple.AbstractInputLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.http.SimpleHttpClient;
import io.goodforgod.aws.lambda.simple.runtime.SimpleRuntimeContext;
import io.goodforgod.graalvm.hint.annotation.NativeImageHint;
import io.goodforgod.simplelambda.http.EtherscanHttpClient;
import java.util.function.Consumer;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 16.09.2021
 */
@NativeImageHint(entrypoint = LambdaEntrypoint.class, name = "application")
public class LambdaEntrypoint extends AbstractInputLambdaEntrypoint {

    private static final LambdaEntrypoint ENTRYPOINT = new LambdaEntrypoint();

    public static void main(String[] args) {
        ENTRYPOINT.run(args);
    }

    @Override
    protected Consumer<SimpleRuntimeContext> setupInCompileTime() {
        return context -> {
            final Converter converter = context.getBean(Converter.class);
            final SimpleHttpClient httpClient = context.getBean(SimpleHttpClient.class);
            final EtherscanHttpClient etherscanHttpClient = new EtherscanHttpClient(converter, httpClient);
            final LambdaHandler lambda = new LambdaHandler(etherscanHttpClient);
            context.registerBean(lambda);
        };
    }
}

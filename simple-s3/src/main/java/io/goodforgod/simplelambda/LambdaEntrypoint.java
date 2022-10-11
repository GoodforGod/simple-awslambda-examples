package io.goodforgod.simplelambda;

import io.goodforgod.aws.lambda.simple.AbstractInputLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.runtime.SimpleRuntimeContext;
import io.goodforgod.graalvm.hint.annotation.NativeImageHint;
import io.goodforgod.graalvm.hint.annotation.ReflectionHint;
import io.minio.PutObjectArgs;
import io.minio.messages.ErrorResponse;
import java.util.function.Consumer;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.logging.impl.SimpleLog;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 10.09.2022
 */
@NativeImageHint(entrypoint = LambdaEntrypoint.class, name = "application")
@ReflectionHint(
        types = { PutObjectArgs.class, ErrorResponse.class, },
        typeNames = { "org.simpleframework.xml.core.ElementLabel" })
@ReflectionHint(types = { LogFactory.class, LogFactoryImpl.class, SimpleLog.class })
public class LambdaEntrypoint extends AbstractInputLambdaEntrypoint {

    private static final LambdaEntrypoint ENTRYPOINT = new LambdaEntrypoint();

    public static void main(String[] args) {
        ENTRYPOINT.run(args);
    }

    @Override
    protected Consumer<SimpleRuntimeContext> setupInCompileTime() {
        return context -> context.registerBean(new AwsLambdaHandler());
    }
}

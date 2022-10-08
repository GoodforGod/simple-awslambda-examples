package io.goodforgod.simplelambda;

import io.goodforgod.aws.lambda.simple.AbstractInputLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.runtime.SimpleRuntimeContext;
import io.goodforgod.graalvm.hint.annotation.*;
import java.sql.DriverManager;
import java.util.function.Consumer;
import org.mariadb.jdbc.Driver;
import org.mariadb.jdbc.internal.com.send.authentication.SendPamAuthPacket;
import org.mariadb.jdbc.internal.failover.impl.MastersFailoverListener;
import org.mariadb.jdbc.internal.failover.impl.MastersReplicasListener;
import org.mariadb.jdbc.internal.util.scheduler.DynamicSizedSchedulerImpl;
import org.mariadb.jdbc.util.Options;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 16.09.2021
 */
@NativeImageHint(entrypoint = LambdaEntrypoint.class,
        name = "application",
        options = { NativeImageOptions.ALLOW_INCOMPLETE_CLASSPATH, NativeImageOptions.REPORT_UNSUPPORTED })
@InitializationHint(value = InitializationHint.InitPhase.BUILD,
        types = DriverManager.class)
@InitializationHint(value = InitializationHint.InitPhase.RUNTIME,
        types = {
                DynamicSizedSchedulerImpl.class,
                MastersReplicasListener.class,
                MastersFailoverListener.class,
                SendPamAuthPacket.class })
@ReflectionHint(types = { Driver.class, DriverManager.class, Options.class })
@ResourceHint(include = {
        "META-INF/services/java.sql.Driver",
        "META-INF/services/org.mariadb.jdbc.authentication.AuthenticationPlugin",
        "META-INF/services/org.mariadb.jdbc.credential.CredentialPlugin",
        "META-INF/services/org.mariadb.jdbc.tls.TlsSocketPlugin",
})
public class LambdaEntrypoint extends AbstractInputLambdaEntrypoint {

    private static final LambdaEntrypoint ENTRYPOINT = new LambdaEntrypoint();

    public static void main(String[] args) {
        ENTRYPOINT.run(args);
    }

    @Override
    protected Consumer<SimpleRuntimeContext> setupInCompileTime() {
        return context -> context.registerBean(new LambdaHandler());
    }
}

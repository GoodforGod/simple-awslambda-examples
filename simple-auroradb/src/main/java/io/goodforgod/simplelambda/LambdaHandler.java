package io.goodforgod.simplelambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.graalvm.hint.annotation.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.util.UUID;
import org.mariadb.jdbc.Driver;
import org.mariadb.jdbc.internal.com.send.authentication.SendPamAuthPacket;
import org.mariadb.jdbc.internal.failover.impl.MastersFailoverListener;
import org.mariadb.jdbc.internal.failover.impl.MastersReplicasListener;
import org.mariadb.jdbc.internal.util.scheduler.DynamicSizedSchedulerImpl;
import org.mariadb.jdbc.util.DefaultOptions;
import org.mariadb.jdbc.util.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 31.07.2021
 */
@NativeImageHint(options = { NativeImageOptions.ALLOW_INCOMPLETE_CLASSPATH, NativeImageOptions.REPORT_UNSUPPORTED })
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
public class LambdaHandler implements RequestHandler<Request, Response> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String SQL = """
            INSERT INTO users(id, name)
            VALUES (?, ?);
            """;

    public LambdaHandler() {
        final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public Response handleRequest(Request request, Context context) {
        logger.info("Processing User with name: {}", request.name());
        final String id = UUID.randomUUID().toString();

        final String jdbcUrl = getJDBC();
        final Properties info = getProperties();

        logger.info("Getting connection..");
        try (final Connection connection = DriverManager.getConnection(jdbcUrl, info)) {
            logger.info("Got connection..");
            try (final PreparedStatement statement = connection.prepareStatement(SQL)) {
                statement.setString(1, id);
                statement.setString(2, request.name());
                logger.info("Executing request save statement..");
                statement.executeUpdate();
                logger.info("Statement executed..");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        return new Response(id, "Hello - " + request.name());
    }

    private Properties getProperties() {
        final String user = getEnvOrThrow("AURORA_USER");
        final String pass = getEnvOrThrow("AURORA_PASS");

        final Properties properties = new Properties();
        properties.put(DefaultOptions.USER.getOptionName(), user);
        properties.put(DefaultOptions.PASSWORD.getOptionName(), pass);
        properties.put(DefaultOptions.CONNECT_TIMEOUT.getOptionName(), 2000);
        return properties;
    }

    private String getJDBC() {
        final String endpoint = getEnvOrThrow("AURORA_ENDPOINT");
        final String database = getEnvOrThrow("AURORA_DB");
        return "jdbc:mariadb:aurora//" + endpoint + ":3306/" + database;
    }

    private static String getEnvOrThrow(String env) {
        final String regionStr = System.getenv(env);
        if (regionStr == null) {
            throw new IllegalArgumentException(env + " env is not set!");
        }

        return regionStr;
    }
}

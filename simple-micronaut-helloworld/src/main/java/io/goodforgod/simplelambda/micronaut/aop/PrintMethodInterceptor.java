package io.goodforgod.simplelambda.micronaut.aop;

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.03.2022
 */
@Introspected
@Singleton
public class PrintMethodInterceptor implements MethodInterceptor<Object, Object> {

    private static final Logger logger = LoggerFactory.getLogger(PrintMethodInterceptor.class);

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        logger.info("Executing method: {}", context.getMethodName());
        return context.proceed();
    }
}

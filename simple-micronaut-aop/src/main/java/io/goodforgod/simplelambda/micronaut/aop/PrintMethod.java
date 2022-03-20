package io.goodforgod.simplelambda.micronaut.aop;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.micronaut.aop.Around;
import io.micronaut.context.annotation.Type;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.03.2022
 */
@Documented
@Retention(RUNTIME)
@Target({ ElementType.METHOD })
@Around
@Type(PrintMethodInterceptor.class)
public @interface PrintMethod {}

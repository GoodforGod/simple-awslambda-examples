package io.goodforgod.simplelambda;

import io.goodforgod.graalvm.hint.annotation.ReflectionHint;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 10.09.2022
 */
@ReflectionHint
public record Response(String id, String message) {}

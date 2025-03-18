package com.diamssword.greenresurgence.utils;

@FunctionalInterface
public interface TriFunction<T, U,V,R> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    R accept(T t, U u,V v);
}

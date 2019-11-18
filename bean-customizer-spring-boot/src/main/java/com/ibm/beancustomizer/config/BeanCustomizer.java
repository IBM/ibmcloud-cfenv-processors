package com.ibm.beancustomizer.config;

/**
 * Simple interface for typed bean customizers.
 * Will only be invoked for beans of type 'getType()'
 */
public interface BeanCustomizer<T> {
    boolean accepts(Object bean, String beanName);

    T postProcessBeforeInit(T original);

    T postProcessAfterInit(T original);
}
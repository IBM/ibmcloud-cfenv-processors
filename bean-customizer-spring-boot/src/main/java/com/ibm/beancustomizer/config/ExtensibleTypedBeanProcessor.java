package com.ibm.beancustomizer.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple attempt to avoid having every bean post processor processe every bean.
 * Instead, this one will only process beans it knows it has BeanCustomizers for.
 */
class TypedBeanPostProcessor implements BeanPostProcessor {
    private final List<BeanCustomizer> beanCustomizers;

    public TypedBeanPostProcessor(List<BeanCustomizer> beanCustomizers) {
        this.beanCustomizers = beanCustomizers;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        List<BeanCustomizer> collect = beanCustomizers.stream()
                .filter(bc -> bc.accepts(bean, beanName))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            return bean;
        } else if (collect.size() == 1) {
            return collect.get(0).postProcessAfterInit(bean);
        } else {
            // Throw error
            throw new BeanCreationException("Multiple bean customizers found");
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        List<BeanCustomizer> collect = beanCustomizers.stream()
                .filter(bc -> bc.accepts(bean, beanName))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            return bean;
        } else if (collect.size() == 1) {
            return collect.get(0).postProcessBeforeInit(bean);
        } else {
            // Throw error
            throw new BeanCreationException("Multiple bean customizers found");
        }
    }
}
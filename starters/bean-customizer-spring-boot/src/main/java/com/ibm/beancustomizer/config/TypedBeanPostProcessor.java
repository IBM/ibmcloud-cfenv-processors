package com.ibm.beancustomizer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(TypedBeanPostProcessor.class);

    private final List<BeanCustomizer> beanCustomizers;

    public TypedBeanPostProcessor(List<BeanCustomizer> beanCustomizers) {
        this.beanCustomizers = beanCustomizers;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        List<BeanCustomizer> beanCustomizers = this.beanCustomizers.stream()
                .filter(bc -> bc.accepts(bean, beanName))
                .collect(Collectors.toList());
        logger.debug("For beanName = [{}] there are the following postProcessAfterInitialization bean customizers = [{}]", beanName, beanCustomizers);
        if (beanCustomizers.isEmpty()) {
            return bean;
        } else if (beanCustomizers.size() == 1) {
            return beanCustomizers.get(0).postProcessAfterInit(bean);
        } else {
            throw new BeanCreationException("Multiple bean customizers found");
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        List<BeanCustomizer> beanCustomizers = this.beanCustomizers.stream()
                .filter(bc -> bc.accepts(bean, beanName))
                .collect(Collectors.toList());
        logger.debug("For beanName = [{}] there are the following postProcessBeforeInitialization bean customizers = [{}]", beanName, beanCustomizers);
        if (beanCustomizers.isEmpty()) {
            return bean;
        } else if (beanCustomizers.size() == 1) {
            return beanCustomizers.get(0).postProcessBeforeInit(bean);
        } else {
            throw new BeanCreationException("Multiple bean customizers found");
        }
    }
}
package com.hairbook.config;

import jakarta.validation.Validator;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

@Configuration
public class ValidationConfig {

    /**
     * Configures the bean validation factory to be aware of Spring's dependency
     * injection.
     * This allows custom ConstraintValidator implementations to use @Autowired.
     */
    @Bean
    public Validator validator(final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setConstraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory));
        return factory;
    }
}

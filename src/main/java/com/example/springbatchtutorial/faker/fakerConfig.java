package com.example.springbatchtutorial.faker;

import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class fakerConfig {
    @Bean
    public Faker faker() {
        return new Faker();
    }
}

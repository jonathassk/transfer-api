package com.example.transfer.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class Resilience4jConfig {

    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }


    public RetryRegistry retryRegistry() {
        return RetryRegistry.ofDefaults();
    }


}

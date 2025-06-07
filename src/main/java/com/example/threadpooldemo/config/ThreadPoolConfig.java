package com.example.threadpooldemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync // 잊지 마세요! This enables Spring's asynchronous method execution capabilities
public class ThreadPoolConfig {

    @Bean("taskExecutor") // Give our executor a specific name
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Get the number of available CPU cores
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        
        // A reasonable starting configuration
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2); // Max threads
        executor.setQueueCapacity(500); // Bounded queue
        executor.setKeepAliveSeconds(60); // Time to live for idle threads
        executor.setThreadNamePrefix("user-processing-"); // Meaningful thread names
        
        // Use CallerRunsPolicy for back-pressure
        // This is a safe choice to prevent the system from being overwhelmed
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // Initialize the executor
        executor.initialize();
        return executor;
    }
}
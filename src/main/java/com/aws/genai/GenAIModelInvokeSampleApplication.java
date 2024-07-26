package com.aws.genai;

import com.aws.genai.service.LLMInvokeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(LLMProperties.class)
@ComponentScan(basePackages = "com.aws")
public class GenAIModelInvokeSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenAIModelInvokeSampleApplication.class, args);
    }

    @Bean
    CommandLineRunner init(LLMInvokeService LLMInvokeService) {
        return (args) -> {
            LLMInvokeService.deleteAll();
            LLMInvokeService.init();
        };
    }
}

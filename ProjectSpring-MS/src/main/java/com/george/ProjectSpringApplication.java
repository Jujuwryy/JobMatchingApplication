package com.george;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition
@EnableAspectJAutoProxy 
public class ProjectSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectSpringApplication.class, args);
    }
}
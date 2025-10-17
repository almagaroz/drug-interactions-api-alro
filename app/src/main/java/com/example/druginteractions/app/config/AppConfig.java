package com.example.druginteractions.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.example.druginteractions.domain",
    "com.example.druginteractions.adapters.openfda"
})
public class AppConfig {}

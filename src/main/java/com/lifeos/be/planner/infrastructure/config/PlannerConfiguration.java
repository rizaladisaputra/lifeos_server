package com.lifeos.be.planner.infrastructure.config;

import com.lifeos.be.planner.domain.service.PlannerDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlannerConfiguration {

    @Bean
    public PlannerDomainService plannerDomainService() {
        return new PlannerDomainService();
    }
}

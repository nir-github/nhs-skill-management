package com.practice.skill.management;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
@SpringBootConfiguration
public class TestConfiguration {
    
    @Bean
    @Primary
    public NettyReactiveWebServerFactory netty() {
        
        return new NettyReactiveWebServerFactory();
    }
    
}
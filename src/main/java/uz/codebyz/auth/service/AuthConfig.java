package uz.codebyz.auth.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(VerificationProperties.class)
public class AuthConfig {
}

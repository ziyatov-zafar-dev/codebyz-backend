package uz.codebyz.notify;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SendGridProperties.class)
public class NotifyConfig {
}

package uz.codebyz.message.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * /files/** -> local filesystem "files/"
 *
 * Örn: files/messages/2025/12/21/153000/uuid.mp4
 * URL:  /files/messages/2025/12/21/153000/uuid.mp4
 */
@Configuration
public class LocalFilesWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:files/");
    }
}

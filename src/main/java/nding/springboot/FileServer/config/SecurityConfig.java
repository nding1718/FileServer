package nding.springboot.FileServer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Java 8 added the concept of default methods in interfaces. Then the spring team updated the framework to make full
 * use of the new Java language features. So, the WebMvcConfigurer interface, starting with Spring 5, contains default
 * implementations for all its methods. As a result. the abstract adapter WebMvcConfigurerAdapter was marked as
 * deprecated. And we can just start using the interface directly.
 */
@EnableWebMvc
@Configuration
public class SecurityConfig implements WebMvcConfigurer {
    /**
     * (1) "/**" is Ant-style path patterns. And "**" matches zero or more 'directories' in a path
     * (2) "*" will allow all origins. (a origin includes Protocol, Host and  Port)
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*") ;
    }
}

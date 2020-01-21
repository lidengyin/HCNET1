package cn.hctech2006.hcnet.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //所有的头信息
                .allowedHeaders("*")
                //所有的方法
                .allowedMethods("*")
                .maxAge(3600)
                //允许携带cookie参数
                .allowCredentials(true)
                //所有的域
                .allowedOrigins("*");

    }
}














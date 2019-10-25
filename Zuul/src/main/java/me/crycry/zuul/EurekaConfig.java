package me.crycry.zuul;

import com.netflix.zuul.ZuulFilter;
import me.crycry.zuul.auth.AuthFilter;
import me.crycry.zuul.auth.AuthInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class EurekaConfig extends WebMvcConfigurerAdapter{

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/file.html").setViewName("file");
        registry.addViewController("/fm.html").setViewName("fm");
        registry.addViewController("/data.html").setViewName("data");
        registry.addViewController("/user.html").setViewName("user");
        registry.addViewController("/field.html").setViewName("field");
        registry.addViewController("/fff.html").setViewName("field");
        registry.addViewController("/fm_pick.html").setViewName("fm_pick");
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register.html").setViewName("register");
        registry.addViewController("/result.html").setViewName("field_result");
        registry.addViewController("/help.html").setViewName("help");

    }
/*
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor());
    }

    @Bean
    AuthInterceptor authInterceptor(){
        return new AuthInterceptor();
    }

    @Bean
    ZuulFilter authFilter(){
        return  new AuthFilter();
    }
*/
    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

}

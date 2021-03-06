package com.wu.redisson.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 集成swagger2 接口管理文档
 *
 * @author yuxue
 * @date 2018-09-07
 */
@Configuration
@EnableSwagger2
//@ConditionalOnProperty(name = "enable", havingValue = "true", prefix = "swagger")
public class Swagger2 {

    @Value("${eureka.client.service-url.defaultZone:eureka}")
    private String eurekaHost;

    @Autowired
    private ServletContext servletContext;

    @Bean
    public Docket createRestApi() {
        List<Parameter> pars = new ArrayList<Parameter>();

        Docket d = new Docket(DocumentationType.SWAGGER_2);
        d.apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wu.redisson"))
                //.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();

        // 判断是否添加请求前缀 // 只有注册到eureka的服务，才要添加
        if (!"eureka".equals(eurekaHost)) {
            d.globalOperationParameters(pars)
                    .pathProvider(new RelativePathProvider(servletContext) {
                        @Override
                        public String getApplicationBasePath() {
                            return "/resdisson-api";
                        }
                    });
        }
        return d;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("分布式锁测试API")
                .description("springboot-redisson restful api POWER BY swagger")
                .version("1.0.0")
                .build();
    }
}

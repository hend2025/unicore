package com.unicore;

import cn.hsa.hsaf.auth.security.utils.SpringContextUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@MapperScan("com.unicore.mapper")
@ComponentScan(basePackages={ "cn.aeye", "com.aeye", "com.unicore", "cn.hsa.hsaf.auth.security.utils", "cn.hsa.hsaf.cache.redis"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SpringContextUtils.class))
@EnableCaching
@ImportResource({
        "classpath:config/adapt/cache/generic/cache-single.xml",
})
public class UnicoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(UnicoreApplication.class, args);
    }
}

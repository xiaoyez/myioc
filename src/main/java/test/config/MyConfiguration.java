package test.config;

import com.ioc.context.RequiredScope;
import com.ioc.context.annotation.*;
import test.entity.User;

@Configuration
@ComponentScan(basePackage = "test.entity")
public class MyConfiguration {

    @Bean
    @Scope(RequiredScope.SINGLETON)
    public User user()
    {
        return new User(1001,"xiaoye");
    }
}

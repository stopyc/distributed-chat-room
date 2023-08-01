package org.example.config;

import org.example.handler.MyExtendAccessDeniedHandler;
import org.example.handler.MyExtendAuthenticationEntryPointHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author YC104
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {


    public static final String RESOURCE_ID = "res1";

    @Autowired
    private MyExtendAuthenticationEntryPointHandler myExtendAuthenticationEntryPointHandler;

    @Autowired
    private MyExtendAccessDeniedHandler myExtendAccessDeniedHandler;

    @Autowired
    TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        //资源id
        resources.resourceId(RESOURCE_ID)
                //token存储
                .tokenStore(tokenStore)
                //无状态,不用session
                .stateless(true)
                //token异常类重写
                .authenticationEntryPoint(myExtendAuthenticationEntryPointHandler)
                //权限不足异常类重写
                .accessDeniedHandler(myExtendAccessDeniedHandler);
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                //这个配置是对客户端的权限进行判断,目前不用配置
                //.antMatchers("/**").access("#oauth2.hasScope('all')")

                //这个表示公共资源,全部需要以/public/....进行url命名即可(即不用带token)
                .mvcMatchers("/*/public/**").permitAll()
                .mvcMatchers("/*/inner/**").permitAll()
                .mvcMatchers("/ws/**").permitAll()
                .mvcMatchers("/*/msg/**").permitAll()
                .mvcMatchers("/msg/**").permitAll()
                //这个表示需要携带token
                .antMatchers("/**").authenticated()
                //关闭跨站攻击
                .and().csrf().disable()
                //不使用session进行存储
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

}
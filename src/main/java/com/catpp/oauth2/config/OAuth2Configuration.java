package com.catpp.oauth2.config;

import com.catpp.oauth2.common.CustomAuthenticationEntryPoint;
import com.catpp.oauth2.common.CustomLogoutSuccessHandler;
import com.catpp.oauth2.enums.AuthoritiesEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;
import java.util.Map;

/**
 * com.catpp.oauth2.config
 *
 * @Author cat_pp
 * @Date 2018/12/28
 * @Description 配置安全资源服务器，类内添加一个子类用于配置资源服务器
 */
@Configuration
public class OAuth2Configuration {

    /**
     * 配置资源服务器
     */
    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Autowired
        private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

        @Autowired
        private CustomLogoutSuccessHandler customLogoutSuccessHandler;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.exceptionHandling()
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .and()
                    .logout()
                    .logoutUrl("/oauth/logout")
                    .logoutSuccessHandler(customLogoutSuccessHandler)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/hello/say/").permitAll()
                    .antMatchers("/secure/**").authenticated();
        }
    }

    /**
     * 开启OAuth2的验证服务器
     */
    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter
            implements EnvironmentAware {

        private static final String ENV_OAUTH = "authentication.oauth";
        private static final String PROP_CLIENTID = "clientid";
        private static final String PROP_SECRET = "secret";
        private static final String PROP_TOKEN_VALIDITY_SECONDS = "tokenValidityInSeconds";

        // springboot1.x方法
//        private RelaxedPropertyResolver propertyResolver;

        // springboot2.x方法
        private Binder binder;
        private Map<String, String> propertyMap;

        @Autowired
        private DataSource dataSource;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Bean
        public TokenStore tokenStore() {
            return new JdbcTokenStore(dataSource);
        }

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                    .withClient(propertyMap.get(PROP_CLIENTID))
                    .scopes("read", "write")
                    .authorities(AuthoritiesEnum.ROLE_ADMIN.name(), AuthoritiesEnum.ROLE_USER.name())
                    .authorizedGrantTypes("password", "refresh_token")
//                    .secret(propertyMap.get(PROP_SECRET))
                    // springboot2.x需要对密码进行加密
                    .secret(passwordEncoder.encode(propertyMap.get(PROP_SECRET)))
                    .accessTokenValiditySeconds(Integer.parseInt(propertyMap.get(PROP_TOKEN_VALIDITY_SECONDS)));
        }

        @Override
        public void setEnvironment(Environment environment) {
            // springboot1.x方法
            // this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_OAUTH);
            // springboot2.x方法
            this.binder = Binder.get(environment);
            this.propertyMap = this.binder.bind(ENV_OAUTH, Map.class).get();
        }
    }
}

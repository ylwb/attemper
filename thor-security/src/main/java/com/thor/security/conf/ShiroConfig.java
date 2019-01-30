package com.thor.security.conf;

import com.thor.sdk.common.constant.ThorAPIPath;
import com.thor.security.shiro.CustomRealm;
import com.thor.security.shiro.JWTFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置
 * @auth ldang
 */
@Configuration
public class ShiroConfig {

    @Bean
    public SessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;
    }

    @Bean
    public SecurityManager securityManager(SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(new CustomRealm());
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        Map<String, Filter> filters = new HashMap<>();
        String jwtFilter = "jwtFilter";
        filters.put(jwtFilter, new JWTFilter());
        bean.setFilters(filters);

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put(ThorAPIPath.Login.LOGIN_BY_USERNAME_PWD, "anon");  //登录
        filterMap.put(ThorAPIPath.Token.REFRESH, "anon");  //刷新令牌
        filterMap.put("/doc.html", "anon");  //swagger2-bootstrap-ui主页
        filterMap.put("/webjars/**", "anon");  //swagger2-bootstrap-ui资源
        filterMap.put("/swagger-resources", "anon");  //swagger2元信息
        filterMap.put("/v2/api-docs", "anon");  //swagger2拿应用接口的请求
        filterMap.put("/kaptcha", "anon");
        filterMap.put("/**", jwtFilter);
        bean.setFilterChainDefinitionMap(filterMap);

        return bean;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        proxyCreator.setUsePrefix(true);
        return proxyCreator;
    }
}
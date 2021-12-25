/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.core.boot;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.apache.fineract.infrastructure.core.filters.ResponseCorsFilter;
import org.apache.fineract.infrastructure.security.filter.TenantAwareBasicAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * This Configuration replaces what formerly was in web.xml.
 *
 * @see <a href=
 *      "http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-convert-an-existing-application-to-spring-boot">#howto-convert-an-existing-application-to-spring-boot</a>
 */
@Configuration
@Profile("basicauth")
public class WebXmlConfiguration {

    @Autowired
    private TenantAwareBasicAuthenticationFilter basicAuthenticationProcessingFilter;

    @Bean
    public ServletRegistrationBean jersey() {
        ServletRegistrationBean<SpringServlet> jerseyServletRegistration = new ServletRegistrationBean<SpringServlet>();
        jerseyServletRegistration.setServlet(new SpringServlet());
        jerseyServletRegistration.addUrlMappings("/api/v1/*");
        jerseyServletRegistration.setName("jersey-servlet");
        jerseyServletRegistration.setLoadOnStartup(1);
        jerseyServletRegistration.addInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
        jerseyServletRegistration.addInitParameter("com.sun.jersey.spi.container.ContainerResponseFilters",
                ResponseCorsFilter.class.getName());
        jerseyServletRegistration.addInitParameter("com.sun.jersey.config.feature.DisableWADL", "true");
        // debugging for development:
        // jerseyServletRegistration.addInitParameter("com.sun.jersey.spi.container.ContainerRequestFilters",
        // LoggingFilter.class.getName());
        return jerseyServletRegistration;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean<TenantAwareBasicAuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<TenantAwareBasicAuthenticationFilter>();
        filterRegistrationBean.setFilter(basicAuthenticationProcessingFilter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

}

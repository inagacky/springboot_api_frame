package com.inagacky.api_sample_app.configure;

import org.modelmapper.ModelMapper;
import com.inagacky.api_sample_app.util.constants.ApiRoutingConstants;
import com.inagacky.api_sample_app.presentation.http.Interceptor.AuthorizationInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * アプリケーションの設定
 */
@Configuration
public class SampleConfigurer implements WebMvcConfigurer {

    /**
     * validatorの登録
     * @return
     */
    @Bean(name="validator")
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource());

        return localValidatorFactoryBean;
    }

    /**
     * メッセージ定義ファイルの登録
     * @return
     */
    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename("classpath:ValidationMessages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");

        return reloadableResourceBundleMessageSource;
    }

    /**
     * パスワードエンコーダの登録
     * @return
     */
    @Bean(name = "passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * モデルマッパーの登録
     * @return
     */
    @Bean(name = "modelMapper")
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * 認可インターセプタの登録
     * @return
     */
    @Bean(name = "authorizationInterceptor")
    public AuthorizationInterceptor authorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor())
                .addPathPatterns(ApiRoutingConstants.API_BASE_PATH + "/**");
    }
}

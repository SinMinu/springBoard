package springBoard.springBoard.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import springBoard.springBoard.domain.member.service.LoginService;
import springBoard.springBoard.global.login.filter.JsonUsernamePasswordAuthenticationFilter;
import springBoard.springBoard.global.login.handler.LoginFailureHandler;
import springBoard.springBoard.global.login.handler.LoginSuccessJWTProvideHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;
    private final LoginService loginService;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .formLogin().disable()      // 1- formLogin 인증방법 비활성화
                .httpBasic().disable()      // 2- httpBasic 인증방법 비활성화(특정 리소스에 접근할 때 username과 password 물어봄)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/signUp","/").permitAll()
                .anyRequest().authenticated();

        http.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){       //1 - PasswordEncoder 등록
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(){       //2 - AuthenticationManager 등록
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();       //DaoAuthenticationProvider 사용
        provider.setPasswordEncoder(passwordEncoder());     //PasswordEncoder로는 PasswordEncoderFactories.createDelegatingPasswordEncoder() 사용
        return new ProviderManager(provider);
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter(){
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        return jsonUsernamePasswordLoginFilter();
    }

    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
        return new LoginSuccessJWTProvideHandler();
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }
}

package tw.com.wess.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import tw.com.wess.security.filter.TokenAuthenticationFilter;
import tw.com.wess.security.handler.CustomAccessDeniedHandler;
import tw.com.wess.security.handler.CustomAuthenticationEntryPoint;
import tw.com.wess.security.manager.UrlAuthorizationManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	
	private final TokenAuthenticationFilter tokenAuthenticationFilter;//2.
    private final UrlAuthorizationManager urlAuthorizationManager;//3.
    private final CustomAccessDeniedHandler customAccessDeniedHandler;//5.
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;//5.

    public SecurityConfig(TokenAuthenticationFilter tokenAuthenticationFilter, 
                          UrlAuthorizationManager urlAuthorizationManager, 
                          CustomAccessDeniedHandler customAccessDeniedHandler,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.urlAuthorizationManager = urlAuthorizationManager;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

//    //0.不擋任何權限
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable()) // 開發階段先關閉 CSRF 防護
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll() // 🟢 關鍵：允許所有請求無條件通過！
//            );
//        return http.build();
//    }
    
    
    //4.進行權限控制
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 開發階段先關閉 CSRF 防護
            
            .exceptionHandling(exceptions -> exceptions
            		// 負責處理 401 錯誤 (完全沒帶 Token 或 Token 失效)
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    // 負責處理 403 錯誤 (有帶 Token 但權限不夠，也就是你剛剛寫好的那支)
                    .accessDeniedHandler(customAccessDeniedHandler)
                )
            
            // 1. 安插解析 Token 的 Filter
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 2. 核心授權邏輯
            .authorizeHttpRequests(auth -> auth
                // 公開網址 (登入頁等) 直接放行
                .requestMatchers("/api/auth/login", "/public/**").permitAll()
                
                // 🟢 其餘所有網址，全部丟給我們寫的 UrlAuthorizationManager 比對！
                .anyRequest().access(urlAuthorizationManager)
            );

        return http.build();
    }
    
}

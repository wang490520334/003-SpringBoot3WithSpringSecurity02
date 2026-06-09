package tw.com.wess.security.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                         AuthenticationException authException) throws IOException, ServletException {
        
        // 1. 設定 HTTP 狀態碼為 401 Unauthorized (未授權/未登入)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // 2. 設定回應格式為 JSON 與編碼
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 3. 自訂你想吐給前端的 JSON 錯誤訊息
        String jsonResponse = "{"
                + "\"status\": 401,"
                + "\"error\": \"Unauthorized\","
                + "\"message\": \"抱歉，您的權限不足，無法執行此操作或存取該資源。\""
                + "}";
                
        // 4. 將 JSON 寫入 Response
        response.getWriter().write(jsonResponse);
    }
}

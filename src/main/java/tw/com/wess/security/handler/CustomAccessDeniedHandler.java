package tw.com.wess.security.handler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        // 1. 設定 HTTP 狀態碼為 403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        // 2. 設定回應格式為 JSON 與編碼
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 3. 自訂你想吐給前端的 JSON 錯誤訊息
        // 實務上也可以用 ObjectMapper 把一個 Java DTO 轉成 JSON 輸出
        String jsonResponse = "{"
                + "\"status\": 403,"
                + "\"error\": \"Forbidden\","
                + "\"message\": \"抱歉，您的權限不足，無法執行此操作或存取該資源。\""
                + "}";
                
        // 4. 將 JSON 寫入 Response
        response.getWriter().write(jsonResponse);
    }
    
}    

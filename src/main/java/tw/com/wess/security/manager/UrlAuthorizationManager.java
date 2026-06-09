package tw.com.wess.security.manager;

import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class UrlAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    // 使用 Spring 內建的路徑比對器，支援 /** 和 /* 語法
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public AuthorizationDecision authorize(Supplier<? extends Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        
        // 1. 取得 Authentication 物件
        Authentication authentication = authenticationSupplier.get(); //authentication 即 2.TokenAuthenticationFilter 包裝所有權限回傳的 UsernamePasswordAuthenticationToken
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false); // 沒登入直接拒絕
        }

        // 2. 取得當前真實發生的 Request 資訊 (組合格式：METHOD:URL)
        String currentMethod = context.getRequest().getMethod();//如 GET
        String currentUrl = context.getRequest().getRequestURI();//如 api/admins
        String currentRequestApi = currentMethod + ":" + currentUrl; //如 GET:/api/admins

        // 3. 超級管理員特權：如果身上有萬用貼紙，直接放行
        boolean isSuperAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SUPER_ADMIN"));
        if (isSuperAdmin) return new AuthorizationDecision(true);

        // 4. 動態比對核心邏輯
        boolean hasPermission = authentication.getAuthorities().stream()// authentication.getAuthorities() 內容如  [GET:/api/users, DELETE:/api/users/**, GET:/api/admins]
                .map(GrantedAuthority::getAuthority)
                .anyMatch(allowedApiPattern -> antPathMatcher.match(allowedApiPattern, currentRequestApi));

        return new AuthorizationDecision(hasPermission);
    }
}

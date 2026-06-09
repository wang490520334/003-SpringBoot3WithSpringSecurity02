package tw.com.wess.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tw.com.wess.service.UserPermissionService;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final UserPermissionService userPermissionService;

    public TokenAuthenticationFilter(UserPermissionService userPermissionService) {//自動注入 
        this.userPermissionService = userPermissionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        String username = extractUsernameFromToken(authHeader);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 1. 直接取得該使用者允許的 API 清單 (例如：["GET:/api/users", "DELETE:/api/users/**", "GET:/api/admins"])
            List<String> allowedApis = userPermissionService.getUserAllowedApis(username);

            // 2. 直接把它們當作權限貼紙貼上去
            List<SimpleGrantedAuthority> authorities = allowedApis.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractUsernameFromToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // 模擬 JWT 解析出帳號
        }
        return null;
    }
}

package tw.com.wess.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserPermissionService {

    /**
     * 負責查詢：某個使用者帳號，允許存取哪些 API 路徑？
     * 格式約定為："HTTP_METHOD:URL_PATTERN"
     */
    public List<String> getUserAllowedApis(String username) {
        // 實務上：從 DB 的 User -> Role -> Permission 關聯表中撈取
        // 這裡的 Permission 表存放的直接就是 "GET:/api/users" 或 "DELETE:/api/users/**"
        
        if ("admin".equals(username)) {
            // 管理員可以讀取列表，也可以刪除任何 ID 的使用者
            return List.of("GET:/api/users", "DELETE:/api/users/**", "GET:/api/admins");
        } else if ("wess".equals(username)) {
            // 一般使用者只能讀取
            return List.of("GET:/api/users");
        }
        return List.of();
    }
}
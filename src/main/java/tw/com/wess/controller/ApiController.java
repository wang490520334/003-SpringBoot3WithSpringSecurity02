package tw.com.wess.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * 對應權限： "GET:/api/users"
     * 只有具備此權限的使用者 (例如 admin, wess) 才能進來
     * 
     * 測試  GET http://localhost:8080/api/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<String>> getAllUsers() {
        // 實務上這裡會呼叫 UserService 去資料庫撈取使用者清單
        List<String> mockUsers = List.of("王大明", "李小華", "張總");
        
        System.out.println("✅ 成功進入 GET /api/users 方法！");
        return ResponseEntity.ok(mockUsers);
    }

    /**
     * 對應權限： "DELETE:/api/users/**"
     * 只有具備此權限的使用者 (例如 admin) 才能進來
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        // 實務上這裡會呼叫 UserService 去資料庫刪除對應 ID 的使用者
        
        System.out.println("✅ 成功進入 DELETE /api/users/" + id + " 方法！");
        return ResponseEntity.ok("使用者 ID: " + id + " 已經成功刪除！");
    }

    /**
     * 額外加碼：測試一個沒有配發權限的 API
     * 如果資料庫沒有配發 "POST:/api/users" 給任何人，
     * 那任何帳號打這個 API 都會在 Manager 被擋下，根本進不來這個方法！
     */
    @PostMapping
    public ResponseEntity<String> createUser() {
        System.out.println("如果你看到這行，代表權限控管壞了！");
        return ResponseEntity.ok("新增使用者成功");
    }
    
    
    /**
     * 對應權限： "GET:/api/admins"
     * 只有具備此權限的使用者 (例如 admin ) 才能進來
     * 
     * 測試  GET http://localhost:8080/api/admins
     */
    @GetMapping("/admins")
    public ResponseEntity<List<String>> getAllAdmins() {
        // 實務上這裡會呼叫 AdminService 去資料庫撈取使用者清單
        List<String> mockUsers = List.of("王Admin", "李Admin", "張Admin");
        
        System.out.println("✅ 成功進入 GET /api/admins 方法！");
        return ResponseEntity.ok(mockUsers);
    }
    
    /**
     * 測試  GET http://localhost:8080/api/auth/login
     */
    @GetMapping("/auth/login")
    public ResponseEntity<String> authLogin() {
        System.out.println("我是登入頁！");
        return ResponseEntity.ok("我是登入頁");
    }
    
}

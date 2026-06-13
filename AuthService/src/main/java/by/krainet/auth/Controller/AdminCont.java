package by.krainet.auth.Controller;


import by.krainet.auth.Service.AdminService;
import by.krainet.common.dto.AdminEmailResponse;
import by.krainet.common.dto.UserDataResponse;
import by.krainet.common.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
public class AdminCont {
    private final AdminService adminService;

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDataResponse> getDataByUserId(
            @PathVariable Long id
    ){
        UserDataResponse response = adminService.getUserByUserId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDataResponse> updateDataByUserId(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request
    ){
        UserDataResponse response = adminService.updateDataByUserId(id,request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserPasswordByUserId(
            @PathVariable Long id,
            String newPassword
    ){
        adminService.updateUserPasswordByUserId(id,newPassword);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserByUserId(
            @PathVariable Long id
    ){
        adminService.deleteUserByUserId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/emails")
    @PreAuthorize("hasAnyRole('ADMIN','SERVICE')")
    public ResponseEntity<AdminEmailResponse> getAdminEmails(){
        return ResponseEntity.ok(adminService.getAdminEmails());
    }


}

package by.krainet.auth.Controller;


import by.krainet.auth.Service.UserService;
import by.krainet.common.dto.ApiResponse;
import by.krainet.common.dto.UserDataResponse;
import by.krainet.common.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserCont {
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDataResponse> me(@RequestHeader("Authorization") String token){
        UserDataResponse response = userService.getMe(token);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDataResponse> updateUserData (@RequestHeader("Authorization") String token,@RequestBody @Valid UserUpdateRequest request){
        UserDataResponse response = userService.updateUserData(token, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateCurrentUserPassword (
            @RequestHeader("Authorization") String token,
            String oldPassword,
            String newPassword
    ){
        userService.updateCurrentUserPassword(token, oldPassword, newPassword);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCurrentUser(@RequestHeader("Authorization") String token){
        userService.deleteCurrentUser(token);
        return ResponseEntity.noContent().build();
    }
}

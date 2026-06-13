package by.krainet.auth.Controller;

import by.krainet.auth.Service.AuthService;
import by.krainet.common.dto.*;
import by.krainet.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthCont {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(
            @RequestBody SignUpRequest request
    ){
        TokenResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login (
           @RequestBody SignInRequest request
    ){
        TokenResponse response = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader("X-Refresh-Token") String token){
        TokenResponse response = refreshTokenService.refresh(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@RequestHeader("X-Refresh-Token") String refreshToken){
        authService.logout(refreshToken);
        SecurityContextHolder.clearContext();
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}

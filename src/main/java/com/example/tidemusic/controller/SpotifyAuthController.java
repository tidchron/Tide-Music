package com.example.tidemusic.controller;

import com.example.tidemusic.service.SpotifyAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.net.URI;

@RestController
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;

    // 컨트롤러는 인증 로직(인증 URI 생성, 코드 교환 등)을 서비스에 위임
    public SpotifyAuthController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    // 1. 사용자에게 인증 페이지로 리다이렉트할 URI 제공
    @GetMapping("/spotify/auth")
    public ResponseEntity<String> getAuthUri() {
        try {
            URI uri = spotifyAuthService.getAuthorizationUri();
            return ResponseEntity.ok(uri.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("인증 URI 생성 중 오류 발생: " + e.getMessage());
        }
    }

    // 2. 스포티파이가 인증 후 리다이렉트하는 콜백 엔드포인트
    @GetMapping("/spotify/callback")
    public RedirectView callback(@RequestParam("code") String code,
                                 @RequestParam("state") String state,
                                 HttpServletResponse response) {
        try {
            // SpotifyAuthService에서 액세스 및 리프레시 토큰 획득
            AuthorizationCodeCredentials credentials = spotifyAuthService.exchangeCode(code);

            // 프론트엔드 URL로 리다이렉트
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("http://localhost:3000");
            return redirectView;
        } catch (Exception e) {
            // 오류 발생 시 프론트엔드로 리다이렉트하고 오류 메시지 전달
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("http://localhost:3000?error=" + e.getMessage());
            return redirectView;
        }
    }

    // 3. 인증 상태 체크 엔드포인트
    @GetMapping("/spotify/status")
    public ResponseEntity<?> getAuthStatus() {
        try {
            boolean authStatus = spotifyAuthService.isAuthenticated();
            return ResponseEntity.ok(authStatus);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("인증 상태 확인 실패: " + e.getMessage());
        }
    }

    // 액세스 토큰 반환 엔드포인트
    @GetMapping("/spotify/token")
    public ResponseEntity<String> getAccessTokenEndpoint() {
        try {
            String token = spotifyAuthService.getAccessToken();
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("토큰 반환 실패: " + e.getMessage());
        }
    }
}

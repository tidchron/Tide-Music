package com.example.tidemusic.controller;

import com.example.tidemusic.service.SpotifyAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    // 2. Spotify가 인증 후 리다이렉트하는 콜백 엔드포인트
    @GetMapping("/spotify/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code, @RequestParam("state") String state) {
        try {
            spotifyAuthService.exchangeCode(code);
            return ResponseEntity.ok("Spotify 인증 성공!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("인증 중 오류 발생: " + e.getMessage());
        }
    }
}

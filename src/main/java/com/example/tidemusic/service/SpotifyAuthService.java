package com.example.tidemusic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class SpotifyAuthService {

    private final SpotifyApi spotifyApi;

    // 1. 인증 URI 생성 (동기) 스포티파이 인증 페이지 표시
    public URI getAuthorizationUri() throws IOException, SpotifyWebApiException, ParseException {
        AuthorizationCodeUriRequest request = spotifyApi.authorizationCodeUri()
                .state("test-custom-state") // CSRF 공격 방지(콜백 시 값 검증)
                .scope("user-read-playback-state, user-modify-playback-state,user-read-currently-playing," +
                        "app-remote-control, streaming," +
                        "playlist-read-private, playlist-read-collaborative, playlist-modify-private, playlist-modify-public," +
                        "user-read-playback-position, user-top-read, user-read-recently-played," +
                        "user-library-modify, user-library-read," +
                        "user-read-email, user-read-private," +
                        "user-follow-read, user-library-modify")
                .show_dialog(true)
                .build();
        return request.execute();
    }

    // 2. 인가 코드 교환: 액세스 및 리프레시 토큰 획득
    public AuthorizationCodeCredentials exchangeCode(String code) throws IOException, SpotifyWebApiException, ParseException {
        AuthorizationCodeRequest request = spotifyApi.authorizationCode(code)
                .build();
        AuthorizationCodeCredentials credentials = request.execute();
        // 토큰 설정
        spotifyApi.setAccessToken(credentials.getAccessToken());
        spotifyApi.setRefreshToken(credentials.getRefreshToken());
        return credentials;
    }

    // 3. 리프레시 토큰을 이용해 액세스 토큰 갱신
    public AuthorizationCodeCredentials refreshAccessToken() throws IOException, SpotifyWebApiException, ParseException {
        AuthorizationCodeRefreshRequest request = spotifyApi.authorizationCodeRefresh()
                .build();
        AuthorizationCodeCredentials credentials = request.execute();
        spotifyApi.setAccessToken(credentials.getAccessToken());
        return credentials;
    }

    // 4. 인증 상태 확인
    public boolean isAuthenticated() {
        String token = spotifyApi.getAccessToken();
        return token != null && !token.isEmpty();
    }

    // 액세스 토큰 반환
    public String getAccessToken() {
        return spotifyApi.getAccessToken();
    }
}

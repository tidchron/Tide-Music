package com.example.tidemusic.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.net.URI;

@Configuration
public class SpotifyConfig {

    @Bean
    public SpotifyApi spotifyApi() {
        Dotenv dotenv = Dotenv.load();
        String clientId = dotenv.get("SPOTIFY_CLIENT_ID");
        String clientSecret = dotenv.get("SPOTIFY_CLIENT_SECRET");

        // 리다이렉트 URL은 추후 수정
        URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/spotify/callback");

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }
}

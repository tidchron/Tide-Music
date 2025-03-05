package com.example.tidemusic;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import java.io.IOException;

public class SpotifyTokenManager {

    static Dotenv dotenv = Dotenv.load();

    private static final String CLIENT_ID = dotenv.get("SPOTIFY_CLIENT_ID");
    private static final String CLIENT_SECRET = dotenv.get("SPOTIFY_CLIENT_SECRET");

    //spotify 객체 생성, Spotify 서버와 통신하는 역할, static final로 한 번 만들면 바뀌지 않도록 고정
    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(CLIENT_SECRET)
            .build();

    public static String getAccessToken() {
        try {
            ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                    .build();
            final ClientCredentials clientCredentials = clientCredentialsRequest
                    .execute();

            //spotify 객체의 accessToken 변수에 토큰값 저장
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            return clientCredentials.getAccessToken();

        } catch (IOException e) {
            System.err.println("입출력 오류 발생: " + e.getMessage());
            return null;
        } catch (ParseException e) {
            System.err.println("HTTP 파싱 오류 발생: " + e.getMessage());
            return null;
        } catch (SpotifyWebApiException e) {
            System.err.println("Spotify API 오류 발생: " + e.getMessage());
            return null;
        }
    }
}
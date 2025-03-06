package com.example.tidemusic.controller;

import com.example.tidemusic.service.SpotifyMusicService;
import com.google.gson.JsonArray;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.player.TransferUsersPlaybackRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spotify")
public class SpotifyMusicController {

    private final SpotifyMusicService spotifyMusicService;

    // 사용자 Top 트랙을 JSON 형태로 반환하는 엔드포인트
    @GetMapping("/top-tracks")
    public ResponseEntity<List<Track>> getTopTracks() {
        try {
            List<Track> topTracks = spotifyMusicService.getTopTracks();
            return ResponseEntity.ok(topTracks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 사용자가 좋아요한 트랙을 JSON 형태로 반환하는 엔드포인트
    @GetMapping("/saved-tracks")
    public ResponseEntity<List<SavedTrack>> getSavedTracks() {
        try {
            List<SavedTrack> savedTracks = spotifyMusicService.getSavedTracks();
            return ResponseEntity.ok(savedTracks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 최근 재생한 트랙을 JSON 형태로 반환하는 엔드포인트
    @GetMapping("/recently-played")
    public ResponseEntity<List<PlayHistory>> getRecentlyPlayed() {
        try {
            List<PlayHistory> recentlyPlayed = spotifyMusicService.getRecentPlayed();
            return ResponseEntity.ok(recentlyPlayed);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 현재 활성화된 디바이스로 재생 전환
    @PostMapping("/transfer")
    public ResponseEntity<String> transferPlayback(@RequestParam("deviceId") String deviceId) {
        try {
            JsonArray deviceIds = new JsonArray();
            deviceIds.add(deviceId);
            TransferUsersPlaybackRequest request = spotifyMusicService.getSpotifyApi()
                    .transferUsersPlayback(deviceIds) // JsonArray를 인자로 전달
                    .build();
            request.execute();
            return ResponseEntity.ok("활성화된 디바이스로 전환: " + deviceId);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            return ResponseEntity.status(500).body("활성화된 디바이스로 전환 실패: " + e.getMessage());
        }
    }

    // 특정 트랙 재생
    @PostMapping("/play")
    public ResponseEntity<String> playTrack(@RequestParam("deviceId") String deviceId,
                                            @RequestParam("trackUri") String trackUri) {
        try {
            JsonArray uris = new JsonArray();
            uris.add(trackUri);
            StartResumeUsersPlaybackRequest request = spotifyMusicService.getSpotifyApi()
                    .startResumeUsersPlayback()
                    .device_id(deviceId)
                    .uris(uris)
                    .build();
            request.execute();
            return ResponseEntity.ok("재생 시작됨: " + trackUri);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            return ResponseEntity.status(500).body("재생 시작 실패: " + e.getMessage());
        }
    }

    // 재생 정지
    @PostMapping("/pause")
    public ResponseEntity<String> pausePlayback(@RequestParam("deviceId") String deviceId) {
        try {
            se.michaelthelin.spotify.requests.data.player.PauseUsersPlaybackRequest request =
                    spotifyMusicService.getSpotifyApi()
                            .pauseUsersPlayback()
                            .device_id(deviceId)
                            .build();
            request.execute();
            return ResponseEntity.ok("재생 정지됨");
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            return ResponseEntity.status(500).body("재생 정지 실패: " + e.getMessage());
        }
    }

    // 팔로우한 아티스트 조회 엔드포인트
    @GetMapping("/followed-artists")
    public ResponseEntity<List<Artist>> getFollowedArtists() {
        try {
            List<Artist> artists=spotifyMusicService.getFollowedArtists();
            return ResponseEntity.ok(artists);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 내 플레이리스트 조회 엔드포인트
    @GetMapping("/playlists")
    public ResponseEntity<List<PlaylistSimplified>> getUserPlaylists() {
        try {
            List<PlaylistSimplified> playlist = spotifyMusicService.getUserPlaylists();
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 플레이리스트 트랙 조회 엔드포인트
    @GetMapping("/playlists/{playlistId}/tracks")
    public ResponseEntity<List<PlaylistTrack>> getPlaylistTracks(@PathVariable String playlistId) {
        try {
            List<PlaylistTrack> tracks = spotifyMusicService.getPlaylistTracks(playlistId);
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            System.err.println("플레이리스트 트랙 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    // 플레이리스트 전체 재생
    @PostMapping("/play-playlist")
    public ResponseEntity<String> playPlaylist(@RequestParam("deviceId") String deviceId,
                                               @RequestParam("contextUri") String contextUri) {
        try {
            String message = spotifyMusicService.playPlaylist(contextUri, deviceId);
            return ResponseEntity.ok(message);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            return new ResponseEntity<>("재생 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

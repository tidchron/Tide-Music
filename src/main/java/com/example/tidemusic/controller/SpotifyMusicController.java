package com.example.tidemusic.controller;

import com.example.tidemusic.service.SpotifyMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SpotifyMusicController {

    private final SpotifyMusicService spotifyMusicService;

    // 사용자 Top 트랙을 JSON 형태로 반환하는 엔드포인트
    @GetMapping("/spotify/top-tracks")
    public ResponseEntity<?> getTopTracks() {
        try {
            List<Track> topTracks = spotifyMusicService.getTopTracks();
            return ResponseEntity.ok(topTracks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("사용자 Top 트랙 가져오기 실패: " + e.getMessage());
        }
    }

    // 사용자가 좋아요한 트랙을 JSON 형태로 반환하는 엔드포인트
    @GetMapping("/spotify/saved-tracks")
    public ResponseEntity<?> getSavedTracks() {
        try {
            List<SavedTrack> savedTracks = spotifyMusicService.getSavedTracks();
            return ResponseEntity.ok(savedTracks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("좋아요 표시한 트랙 가져오기 실패: " + e.getMessage());
        }
    }
}

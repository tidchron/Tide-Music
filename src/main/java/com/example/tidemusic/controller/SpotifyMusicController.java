package com.example.tidemusic.controller;

import com.example.tidemusic.service.SpotifyMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SpotifyMusicController {

    private final SpotifyMusicService spotifyMusicService;

    // 사용자 Top 트랙을 JSON 형태로 반환하는 엔드포인트
    @GetMapping("/spotify/top-tracks")
    public ResponseEntity<List<Track>> getTopTracks() {
        try {
            List<Track> topTracks = spotifyMusicService.getTopTracks();
            return ResponseEntity.ok(topTracks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 사용자가 좋아요한 트랙을 JSON 형태로 반환하는 엔드포인트
    @GetMapping("/spotify/saved-tracks")
    public ResponseEntity<List<SavedTrack>> getSavedTracks() {
        try {
            List<SavedTrack> savedTracks = spotifyMusicService.getSavedTracks();
            return ResponseEntity.ok(savedTracks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 최근 재생한 트랙을 JSON 형태로 반환하는 엔드포인트
    @GetMapping("/spotify/recently-played")
    public ResponseEntity<List<PlayHistory>> getRecentlyPlayed() {
        try {
            List<PlayHistory> recentlyPlayed = spotifyMusicService.getRecentPlayed();
            return ResponseEntity.ok(recentlyPlayed);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}

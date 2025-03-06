package com.example.tidemusic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SpotifyMusicService {

    private final SpotifyApi spotifyApi;

    // 사용자 Top 트랙 가져오기
    public List<Track> getTopTracks() throws IOException, SpotifyWebApiException, ParseException {
        GetUsersTopTracksRequest request = spotifyApi.getUsersTopTracks()
                .limit(10) // 가져올 트랙 수(최대 50)
                .time_range("medium_term") // 시간 범위 (long_term(1년), medium_term(6개월), short_term(4주))
                .build();
        Paging<Track> trackPaging = request.execute();
        return Arrays.asList(trackPaging.getItems());
    }

    // 좋아요 표시한 트랙 가져오기
    public List<SavedTrack> getSavedTracks() throws IOException, SpotifyWebApiException, ParseException {
        GetUsersSavedTracksRequest request = spotifyApi.getUsersSavedTracks()
                .limit(50) // 가져올 트랙 수(최대 50)
                .build();
        Paging<SavedTrack> savedTrackPaging = request.execute();
        return Arrays.asList(savedTrackPaging.getItems());
    }

    // 최근 재생한 트랙 가져오기
    public List<PlayHistory> getRecentPlayed() throws IOException, SpotifyWebApiException, ParseException {
        GetCurrentUsersRecentlyPlayedTracksRequest request=spotifyApi.getCurrentUsersRecentlyPlayedTracks()
                .limit(20) // 가져올 트랙 수(최대 50)
                .build();
        PagingCursorbased<PlayHistory> playHistoryPaging = request.execute();
        return Arrays.asList(playHistoryPaging.getItems());
    }
}

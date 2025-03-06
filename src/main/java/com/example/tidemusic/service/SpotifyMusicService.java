package com.example.tidemusic.service;

import com.google.gson.JsonArray;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.requests.data.follow.GetUsersFollowedArtistsRequest;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;

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
        GetCurrentUsersRecentlyPlayedTracksRequest request = spotifyApi.getCurrentUsersRecentlyPlayedTracks()
                .limit(10) // 가져올 트랙 수(최대 50)
                .build();
        PagingCursorbased<PlayHistory> playHistoryPaging = request.execute();
        return Arrays.asList(playHistoryPaging.getItems());
    }

    // 백엔드 컨트롤러에서 SpotifyApi 객체에 접근 가능
    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    // 팔로우한 아티스트 불러오기
    public List<Artist> getFollowedArtists() throws IOException, SpotifyWebApiException, ParseException {
        GetUsersFollowedArtistsRequest request = spotifyApi.getUsersFollowedArtists(ModelObjectType.ARTIST)
                .limit(50) // 최대값 50
                .build();
        PagingCursorbased<Artist> artistPaging = request.execute();
        return Arrays.asList(artistPaging.getItems());
    }

    // 내 플레이리스트 불러오기
    public List<PlaylistSimplified> getUserPlaylists() throws IOException, SpotifyWebApiException, ParseException {
        GetListOfCurrentUsersPlaylistsRequest request=spotifyApi.getListOfCurrentUsersPlaylists()
                .limit(50)
                .build();
        Paging<PlaylistSimplified> paging = request.execute();
        return Arrays.asList(paging.getItems());
    }

    // 플레이리스트 트랙 가져오기
    public List<PlaylistTrack> getPlaylistTracks(String playlistId) throws IOException, SpotifyWebApiException, ParseException {
        GetPlaylistsItemsRequest request = spotifyApi.getPlaylistsItems(playlistId)
                .limit(100)
                .build();
        Paging<PlaylistTrack> playlistTrackPaging = request.execute();
        return Arrays.asList(playlistTrackPaging.getItems());
    }

    // 특정 트랙 URI를 재생 (context_uri 사용)
    public String playTrack(String trackUri, String deviceId) throws IOException, SpotifyWebApiException, ParseException {
        StartResumeUsersPlaybackRequest request = spotifyApi.startResumeUsersPlayback()
                .device_id(deviceId)  // 디바이스 ID 설정
                .context_uri(trackUri) // context_uri 파라미터 사용
                .build();
        request.execute();
        return "재생 시작됨: " + trackUri;
    }

    // 플레이리스트 전체 재생
    public String playPlaylist(String contextUri, String deviceId) throws IOException, SpotifyWebApiException, ParseException {
        StartResumeUsersPlaybackRequest request = spotifyApi.startResumeUsersPlayback()
                .context_uri(contextUri)
                .device_id(deviceId) // 디바이스 ID 설정
                .build();
        request.execute();
        return "재생 시작됨: " + contextUri;
    }
}

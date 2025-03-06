import logo from './logo.svg';
import './App.css';
import React, {useState, useEffect} from 'react';
import axios from 'axios';

function App() {

    const [authUrl, setAuthUrl] = useState('');
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [topTracks, setTopTracks] = useState([]);
    const [savedTracks, setSavedTracks] = useState([]);
    const [recentlyPlayed, setRecentlyPlayed] = useState([]);
    const [deviceId, setDeviceId] = useState('');
    const [player, setPlayer] = useState(null);
    const [followedArtists, setFollowedArtists] = useState([]);
    const [playlists, setPlaylists] = useState([]);
    const [selectedPlaylistId, setSelectedPlaylistId] = useState(null);
    const [playlistTracks, setPlaylistTracks] = useState([]);

    // 인증 상태 확인 및 데이터 가져오기
    useEffect(() => {
        const checkAuthStatus = async () => {
            try {
                const response = await axios.get('http://localhost:8080/spotify/status');
                const authStatus = response.data;
                setIsAuthenticated(authStatus);

                if (authStatus) {
                    // 사용자 Top 트랙 가져오기
                    const topTracksResponse = await axios.get('http://localhost:8080/spotify/top-tracks');
                    setTopTracks(topTracksResponse.data);

                    // 좋아요 표시한 트랙 가져오기
                    const savedTracksResponse = await axios.get('http://localhost:8080/spotify/saved-tracks');
                    setSavedTracks(savedTracksResponse.data);

                    // 최근 재생한 트랙 가져오기
                    const recentlyPlayedResponse = axios.get('http://localhost:8080/spotify/recently-played');
                    setRecentlyPlayed((await recentlyPlayedResponse).data);

                    // 팔로우한 아티스트 가져오기
                    const followedArtistsResponse = await axios.get('http://localhost:8080/spotify/followed-artists');
                    setFollowedArtists(followedArtistsResponse.data);

                    // 내 플레이리스트 가져오기
                    const playlistsResponse = await axios.get('http://localhost:8080/spotify/playlists');
                    setPlaylists(playlistsResponse.data);
                } else {
                    // 인증되지 않았다면 인증 URI를 가져와 로그인하도록 유도
                    const authResponse = axios.get('http://localhost:8080/spotify/auth');
                    setAuthUrl((await authResponse).data);
                }
            } catch (error) {
                console.log('데이터 가져오기 실패: ', error)
            }
        };
        checkAuthStatus();
    }, []);

    // 스포티파이 플레이어 초기화(인증 상태 true일 때)
    useEffect(() => {
        if (isAuthenticated) {
            // 백엔드로부터 액세스 토큰 가져오기
            axios.get('http://localhost:8080/spotify/token', {withCredentials: true})
                .then(response => {
                    const token = response.data;
                    // 스포티파이 플레이어 초기화
                    const spotifyPlayer = new window.Spotify.Player({
                        name: 'Tide Music Player',
                        getOAuthToken: cb => {
                            cb(token);
                        },
                        volume: 0.5
                    });

                    // 이벤트 리스너 설정
                    spotifyPlayer.addListener('initialization_error', ({message}) => {
                        console.error(message);
                    });
                    spotifyPlayer.addListener('authentication_error', ({message}) => {
                        console.error(message);
                    });
                    spotifyPlayer.addListener('account_error', ({message}) => {
                        console.error(message);
                    });
                    spotifyPlayer.addListener('playback_error', ({message}) => {
                        console.error(message);
                    });

                    spotifyPlayer.addListener('ready', ({device_id}) => {
                        console.log('플레이어 준비 완료, device_id:', device_id);
                        setDeviceId(device_id);
                    });

                    spotifyPlayer.addListener('player_state_changed', state => {
                        console.log('플레이어 상태 변경: ', state);
                        // 필요 시 현재 재생 중인 트랙 상태 업데이트
                    });

                    spotifyPlayer.connect().then(success => {
                        if (success) {
                            console.log("플레이어 연결 성공");
                            setPlayer(spotifyPlayer);
                        } else {
                            console.error("플레이어 연결 실패");
                        }
                    });
                })
                .catch(error => {
                    console.error("토큰 가져오기 또는 플레이어 초기화 실패:", error);
                });
        }
    }, [isAuthenticated])

    const handleLogin = () => {
        // 백엔드 인증 URI로 리디렉션
        window.location.href = authUrl;
    };

    // SDK 플레이어를 통한 재생
    const handlePlay = (trackUri) => {
        if (deviceId) {
            axios.post(`http://localhost:8080/spotify/play?deviceId=${encodeURIComponent(deviceId)}&trackUri=${encodeURIComponent(trackUri)}`, {}, {withCredentials: true})
                .then(response => {
                    console.log(response.data);
                })
                .catch(error => {
                    console.error('재생 시작 실패: ', error);
                });
        } else {
            console.error("deviceId가 설정되지 않았습니다.");
        }
    };

    // SDK 플레이어를 통한 일시 정지 (togglePlay로도 정지 가능)
    const handlePause = () => {
        if (player) {
            player.togglePlay().then(() => {
                console.log('플레이어 토글 일시 정지');
            }).catch(err => console.error('플레이어 일시 정지 오류: ', err));
        }
    };

    // 선택된 플레이리스트 트랙 가져오는 함수
    const fetchPlaylistTracks = async (playlistId) => {
        try {
            const response = await axios.get(`http://localhost:8080/spotify/playlists/${playlistId}/tracks`);
            setPlaylistTracks(response.data);
            setSelectedPlaylistId(playlistId);
        } catch (error) {
            console.error('플레이리스트 트랙 가져오기 실패:', error);
        }
    };

    // 플레이리스트 전체 재생
    const handlePlayPlaylist = (playlistUri) => {
        if (deviceId) {
            axios.post(
                `http://localhost:8080/spotify/play-playlist?deviceId=${encodeURIComponent(deviceId)}&contextUri=${encodeURIComponent(playlistUri)}`,
                {},
                { withCredentials: true }
            )
                .then(response => {
                    console.log(response.data);
                })
                .catch(error => {
                    console.error('플레이리스트 전체 재생 실패:', error);
                });
        } else {
            console.error("deviceId가 설정되지 않았습니다.");
        }
    };

    return (
        <div className="App">
            <header className="App-header">
                <h1>Tide Music</h1>
                {isAuthenticated ? (
                    <div>
                        <h2>사용자 Top 트랙</h2>
                        <ul>
                            {topTracks.map(track => (
                                <li key={track.id}>
                                    <img src={track.album.images[0].url} alt={track.name}
                                         style={{width: '50px', height: '50px'}}/>
                                    {track.name} - {track.artists && track.artists.length > 0 && track.artists[0].name}
                                    ({track.album.name})
                                    <button onClick={() => handlePlay(track.uri)}>Play</button>
                                </li>
                            ))}
                        </ul>
                        <h2>좋아요 표시한 트랙</h2>
                        <ul>
                            {savedTracks.map(track => (
                                <li key={track.track.id}>
                                    <img src={track.track.album.images[0].url} alt={track.track.name}
                                         style={{width: '50px', height: '50px'}}/>
                                    {track.track.name} - {track.track.artists && track.track.artists.length > 0 && track.track.artists[0].name}
                                    ({track.track.album.name})
                                    <button onClick={() => handlePlay(track.track.uri)}>Play</button>
                                </li>
                            ))}
                        </ul>
                        <h2>최근 재생한 트랙</h2>
                        <ul>
                            {recentlyPlayed.map(track => (
                                <li key={track.track.id}>
                                    <img src={track.track.album.images[0].url} alt={track.track.name}
                                         style={{width: '50px', height: '50px'}}/>
                                    {track.track.name} - {track.track.artists && track.track.artists.length > 0 && track.track.artists[0].name}
                                    ({track.track.album.name})
                                    <button onClick={() => handlePlay(track.track.uri)}>Play</button>
                                </li>
                            ))}
                        </ul>
                        <h2>팔로우한 아티스트</h2>
                        <ul>
                            {followedArtists.map(artist => (
                                <li key={artist.id}>
                                    {artist.images && artist.images.length > 0 && (
                                        <img src={artist.images[0].url} alt={artist.name}
                                             style={{width: '50px', height: '50px'}}/>
                                    )}
                                    {artist.name}</li>
                            ))}
                        </ul>
                        <h2>내 플레이리스트</h2>
                        <ul>
                            {playlists.map(playlist => (
                                <li key={playlist.id}
                                    onClick={() => fetchPlaylistTracks(playlist.id)}>{playlist.name}
                                    <button onClick={() => handlePlayPlaylist(playlist.uri)}>전체 재생</button>
                                </li>
                            ))}
                        </ul>
                        {selectedPlaylistId && (
                            <div>
                                <h3>선택된 플레이리스트 트랙</h3>
                                <ul>
                                    {playlistTracks.map(track => (
                                        <li key={track.track.id}>
                                            <img src={track.track.album.images[0].url} alt={track.track.name}
                                                 style={{width: '50px', height: '50px'}}/>
                                            {track.track.name} - {track.track.artists && track.track.artists.length > 0 && track.track.artists[0].name}
                                            ({track.track.album.name})
                                            <button onClick={() => handlePlay(track.track.uri)}>Play</button>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        )}
                        <div>
                            <button onClick={handlePause}>일시 정지</button>
                        </div>
                    </div>
                ) : (
                    authUrl ? (
                        <button onClick={handleLogin}>Login with Spotify</button>
                    ) : (
                        <p>Loading...</p>
                    )
                )}
            </header>
        </div>
    )
}

export default App;

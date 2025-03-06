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

    useEffect(() => {
        // 인증 상태 확인 및 데이터 가져오기
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
    }, [])

const handleLogin = () => {
    // 백엔드 인증 URI로 리디렉션
    window.location.href = authUrl;
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
                            </li>
                        ))}
                    </ul>
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

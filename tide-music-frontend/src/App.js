import logo from './logo.svg';
import './App.css';
import React, {useState, useEffect} from 'react';
import axios from 'axios';

function App() {

    const [authUrl, setAuthUrl] = useState('');
    const [topTracks, setTopTracks] = useState([]);
    const [savedTracks, setSavedTracks] = useState([]);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        // 인증 상태 확인하기
        axios.get('http://localhost:8080/spotify/status')
            .then(response => {
                const authStatus = response.data;
                setIsAuthenticated(authStatus);

                // 인증된 상태라면 데이터 호출
                if (authStatus) {
                    // 사용자 Top 트랙 가져오기
                    axios.get('http://localhost:8080/spotify/top-tracks')
                        .then(response => {
                            // 데이터가 정상적으로 수신되면 인증된 상태로 설정
                            setTopTracks(response.data);
                        })
                        .catch(error => {
                            console.error('사용자 Top 트랙 가져오기 실패: ', error);
                        });

                    // 좋아요 표시한 트랙 가져오기
                    axios.get('http://localhost:8080/spotify/saved-tracks')
                        .then(response => {
                            setSavedTracks(response.data);
                        })
                        .catch(error => {
                            console.error('좋아요 표시한 트랙 가져오기 실패: ', error);
                        });
                } else {
                    // 인증되지 않았다면 인증 URI를 가져와 로그인하도록 유도
                    axios.get('http://localhost:8080/spotify/auth')
                        .then(response => {
                            setAuthUrl(response.data);
                        })
                        .catch(err => {
                            console.error('인증 URI 가져오기 실패: ', err);
                        });
                }
            })
        // 백엔드에서 인증 URI 가져오기
        axios.get('http://localhost:8080/spotify/auth')
            .then(response => {
                // 백엔드에서 받은 인증 URI를 state에 저장
                setAuthUrl(response.data);
            })
            .catch(error => {
                console.error('인증 URI 가져오기 실패: ', error);
            });
    }, []);

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
                                    {track.name} - {track.artists && track.artists.length > 0 && track.artists[0].name}
                                </li>
                            ))}
                        </ul>
                        <h2>좋아요 표시한 트랙</h2>
                        <ul>
                            {savedTracks.map(track => (
                                <li key={track.track.id}>
                                    {track.track.name} - {track.track.artists && track.track.artists.length > 0 && track.track.artists[0].name}
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

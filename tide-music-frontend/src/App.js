import logo from './logo.svg';
import './App.css';
import React, {useState, useEffect} from 'react';
import axios from 'axios';

function App() {

    const [authUrl, setAuthUrl] = useState('');

    useEffect(() => {
        // Spring Boot 백엔드에서 인증 URI 가져오기
        axios.get('http://localhost:8080/spotify/auth')
            .then(response => {
                // 백엔드에서 받은 인증 URI를 state에 저장
                setAuthUrl(response.data);
            })
            .catch(error => {
                console.error('인증 URI 가져오기 실패', error);
            });
    }, []);

    const handleLogin = () => {
        // 인증 URI로 리디렉션
        window.location.href = authUrl;
    };

    return (
        <div className="App">
            <header className="App-header">
                <h1>Tide Music</h1>
                {authUrl ? (
                    <button onClick={handleLogin}>Login with Spotify</button>
                ) : (
                    <p>Loading...</p>
                )}
            </header>
        </div>
    )
}

export default App;

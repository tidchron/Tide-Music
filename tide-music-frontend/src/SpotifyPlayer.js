import React, {useState, useEffect} from "react";

const SpotifyPlayer = ({token}) => {
    const [player, setPlayer] = useState(null);
    const [deviceId, setDeviceId] = useState('')

    useEffect(() => {
        // Spotify SDK가 window.Spotify에 로드되었는지 확인
        if (!window.Spotity || !token) return;

        // 스포티파이 플레이어 초기화
        const player = new window.Spotity.Player({
            name: 'Tide Music Player',
            getOAuthToken: cb => {
                cb(token);
            },
            volume: 0.5,
        })

        // 플레이어 이벤트 등록
        player.addListener('initialization_error', ({message}) => {
            console.error(message);
        });
        player.addListener('authentication_error', ({message}) => {
            console.error(message);
        });
        player.addListener('account_error', ({message}) => {
            console.error(message);
        });
        player.addListener('playback_error', ({message}) => {
            console.error(message);
        });

        // 플레이어 준비되면 디바이스 ID 설정
        player.addListener('ready', ({deviceId}) => {
            console.log('디바이스 ID 준비', deviceId);
            setDeviceId(deviceId);
        });

        // 플레이어 상태 변경 리스너
        player.addListener('player_state_changed', state => {
            console.log('플레이어 상태 변경', state);
        });

        // 플레이어 연결
        player.connect().then(success => {
            if (success) {
                console.log('웹 플레이어 스포티파이에 연결 성공!')
            }
        })

        // 컴포넌트 언마운트 시 정리
        setPlayer(player);
        return () => {
            if (player) {
                player.disconnect();
            }
        };
    }, [token]);

    return(
        <div>
            <h3>Spotify Player</h3>
            {deviceId ? <p>Device ID: {deviceId}</p> : <p>Loading player...</p>}
        </div>
    )
}
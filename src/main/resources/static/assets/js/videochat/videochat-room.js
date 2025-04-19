document.addEventListener('DOMContentLoaded', function() {
    // 기본 설정 변수
    const roomId = document.getElementById('roomId').textContent;
    const empNum = document.getElementById('empNum').value;

    // WebRTC 관련 변수
    const configuration = {
        iceServers: [
            { urls: 'stun:stun.l.google.com:19302' },
            { urls: 'stun:stun1.l.google.com:19302' }
        ]
    };

    // 로컬 미디어 스트림
    let localStream = null;

    // 화면 공유 스트림
    let screenStream = null;

    // PeerConnection 관리
    const peerConnections = {};

    // 참가자 관리
    const participants = {};

    // 로컬 스토리지에서 미디어 설정 불러오기
    const useCamera = localStorage.getItem('videoChat_useCamera') === 'true';
    const useMic = localStorage.getItem('videoChat_useMic') === 'true';

    // UI 요소
    const videoGrid = document.getElementById('videoGrid');
    const cameraToggleBtn = document.getElementById('cameraToggle');
    const micToggleBtn = document.getElementById('micToggle');
    const screenShareToggleBtn = document.getElementById('screenShareToggle');
    const chatToggleBtn = document.getElementById('chatToggle');
    const participantsToggleBtn = document.getElementById('participantsToggle');
    const leaveRoomBtn = document.getElementById('leaveRoomBtn');
    const sidePanel = document.getElementById('sidePanel');
    const participantsPanel = document.getElementById('participantsPanel');
    const chatPanel = document.getElementById('chatPanel');
    const participantsList = document.getElementById('participantsList');
    const chatMessages = document.getElementById('chatMessages');
    const chatInput = document.getElementById('chatInput');
    const sendChatBtn = document.getElementById('sendChatBtn');
    const panelTitle = document.getElementById('panelTitle');
    const closePanelBtn = document.getElementById('closePanelBtn');

    // WebSocket 연결
    let stompClient = null;

    // 하트비트 타이머
    let heartbeatInterval = null;

    // 페이지 이탈 시 처리
    window.addEventListener('beforeunload', function(e) {
        leaveRoom();
    });

    // 초기화 함수 호출
    initialize();

    // 초기화 함수
    async function initialize() {
        try {
            // 로컬 미디어 스트림 가져오기
            await getLocalMediaStream();

            // WebSocket 연결
            connectWebSocket();

            // 하트비트 시작
            startHeartbeat();

            // UI 이벤트 핸들러 설정
            setupUIHandlers();

        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            alert('화상회의 연결에 문제가 발생했습니다. 새로고침을 시도해주세요.');
        }
    }

    // 로컬 미디어 스트림 가져오기
    async function getLocalMediaStream() {
        try {
            const constraints = {
                audio: useMic,
                video: useCamera ? {
                    width: { ideal: 1280 },
                    height: { ideal: 720 }
                } : false
            };

            localStream = await navigator.mediaDevices.getUserMedia(constraints);

            // 로컬 비디오 엘리먼트 생성 및 표시
            createVideoElement('local', empNum, true);

            // 초기 미디어 상태에 맞게 UI 업데이트
            updateMediaButtons();

        } catch (error) {
            console.error('미디어 스트림 가져오기 오류:', error);

            // 카메라/마이크 권한이 없어도 진행 가능하도록 빈 스트림 생성
            localStream = new MediaStream();

            // 카메라/마이크 버튼 비활성화
            if (error.name === 'NotAllowedError' || error.name === 'PermissionDeniedError') {
                cameraToggleBtn.disabled = true;
                micToggleBtn.disabled = true;
                cameraToggleBtn.classList.remove('active');
                micToggleBtn.classList.remove('active');
            }

            // 빈 비디오 엘리먼트 생성
            createVideoElement('local', empNum, true, true);
        }
    }

    // WebSocket 연결 설정
    function connectWebSocket() {
        const socket = new SockJS('/ws-video');
        stompClient = Stomp.over(socket);

        // 디버그 로그 비활성화
        stompClient.debug = null;

        stompClient.connect({}, function(frame) {
            console.log('WebSocket 연결됨:', frame);

            // 시그널링 메시지 구독
            stompClient.subscribe('/topic/videochat/signal/' + roomId, function(message) {
                handleSignalingMessage(JSON.parse(message.body));
            });

            // 채팅 메시지 구독
            stompClient.subscribe('/topic/videochat/chat/' + roomId, function(message) {
                handleChatMessage(JSON.parse(message.body));
            });

            // 참가자 변경 알림 구독
            stompClient.subscribe('/topic/videochat/participant/' + roomId, function(message) {
                handleParticipantEvent(JSON.parse(message.body));
            });

            // 참가자 목록 업데이트 구독
            stompClient.subscribe('/topic/videochat/participants/' + roomId, function(message) {
                updateParticipantsList(JSON.parse(message.body).participants);
            });

            // 참가자 입장 알림 전송
            sendParticipantEvent('join');

            // 참가자 목록 요청
            fetchParticipants();
        }, function(error) {
            console.error('WebSocket 연결 오류:', error);
            alert('WebSocket 연결에 실패했습니다. 새로고침을 시도해주세요.');
        });
    }

    // 하트비트 시작
    function startHeartbeat() {
        // 20초마다 하트비트 전송
        heartbeatInterval = setInterval(function() {
            fetch('/api/videochat/rooms/' + roomId + '/heartbeat', {
                method: 'POST'
            })
                .catch(error => console.error('하트비트 전송 오류:', error));
        }, 20000);
    }

    // 회의실 나가기
    function leaveRoom() {
        // 하트비트 중지
        if (heartbeatInterval) {
            clearInterval(heartbeatInterval);
            heartbeatInterval = null;
        }

        // 모든 미디어 트랙 중지
        if (localStream) {
            localStream.getTracks().forEach(track => track.stop());
        }

        if (screenStream) {
            screenStream.getTracks().forEach(track => track.stop());
        }

        // 모든 피어 연결 종료
        Object.keys(peerConnections).forEach(peerId => {
            cleanupPeer(peerId);
        });

        // WebSocket 연결 종료
        if (stompClient && stompClient.connected) {
            // 퇴장 알림 전송
            sendParticipantEvent('leave');

            // 연결 종료
            stompClient.disconnect();
        }

        // 서버에 퇴장 알림
        fetch('/api/videochat/rooms/' + roomId + '/leave', {
            method: 'POST'
        }).catch(error => console.error('퇴장 알림 오류:', error));
    }

    // 참가자 목록 가져오기
    function fetchParticipants() {
        fetch('/api/videochat/rooms/' + roomId + '/participants')
            .then(response => response.json())
            .then(data => {
                updateParticipantsList(data);
            })
            .catch(error => console.error('참가자 목록 가져오기 오류:', error));
    }

    // 참가자 목록 업데이트
    function updateParticipantsList(participantsList) {
        // 참가자 맵 업데이트
        participantsList.forEach(participant => {
            participants[participant.empNum] = participant;
        });

        // UI 업데이트
        const participantsListElement = document.getElementById('participantsList');
        if (participantsListElement) {
            participantsListElement.innerHTML = '';

            participantsList.forEach(participant => {
                const participantElement = document.createElement('li');
                participantElement.className = 'participant-item';

                // 현재 사용자인지 확인
                const isMe = participant.empNum === empNum;

                // 첫 글자로 아바타 생성
                const nameInitial = (participant.empName || participant.empNum).charAt(0).toUpperCase();

                participantElement.innerHTML = `
                    <div class="participant-avatar">${nameInitial}</div>
                    <div class="participant-name">${isMe ? participant.empName + ' (나)' : participant.empName || participant.empNum}</div>
                `;

                participantsListElement.appendChild(participantElement);
            });
        }
    }

    // 비디오 엘리먼트 생성
    function createVideoElement(id, name, isLocal, noVideo = false, stream = null) {
        // 이미 존재하는 비디오인지 확인
        const existingVideo = document.getElementById(`video-${id}`);
        if (existingVideo) {
            // 스트림만 업데이트
            if (stream) {
                existingVideo.srcObject = stream;
            }
            return;
        }

        console.log('비디오 엘리먼트 생성:', id, isLocal);

        // 비디오 컨테이너 생성
        const videoItem = document.createElement('div');
        videoItem.className = 'video-item';
        videoItem.id = `video-container-${id}`;

        // 비디오 엘리먼트 생성
        if (!noVideo) {
            const video = document.createElement('video');
            video.id = `video-${id}`;
            video.autoplay = true;
            video.playsInline = true;

            if (isLocal) {
                video.muted = true;
                video.srcObject = localStream;
            } else if (stream) {
                video.srcObject = stream;
            }

            videoItem.appendChild(video);
        } else {
            // 비디오가 없는 경우 플레이스홀더 표시
            const placeholder = document.createElement('div');
            placeholder.className = 'no-video-placeholder';
            placeholder.innerHTML = '<i class="fa-solid fa-user"></i>';
            videoItem.appendChild(placeholder);
        }

        // 참가자 정보 표시
        const participantInfo = document.createElement('div');
        participantInfo.className = 'participant-info';

        // 참가자 이름 표시 (자신이면 "나"로 표시)
        const displayName = isLocal ? '나' : (participants[id] ? participants[id].empName : id);
        participantInfo.textContent = displayName;

        videoItem.appendChild(participantInfo);

        // 비디오 그리드에 추가
        videoGrid.appendChild(videoItem);
    }

    // UI 이벤트 핸들러 설정
    function setupUIHandlers() {
        // 카메라 토글
        cameraToggleBtn.addEventListener('click', toggleCamera);

        // 마이크 토글
        micToggleBtn.addEventListener('click', toggleMicrophone);

        // 화면 공유 토글
        screenShareToggleBtn.addEventListener('click', toggleScreenShare);

        // 채팅 패널 토글
        chatToggleBtn.addEventListener('click', function() {
            togglePanel('chat');
        });

        // 참가자 목록 패널 토글
        participantsToggleBtn.addEventListener('click', function() {
            togglePanel('participants');
        });

        // 패널 닫기 버튼
        closePanelBtn.addEventListener('click', function() {
            sidePanel.style.display = 'none';
            chatToggleBtn.classList.remove('active');
            participantsToggleBtn.classList.remove('active');
        });

        // 채팅 전송 버튼
        sendChatBtn.addEventListener('click', sendChatMessage);

        // 채팅 입력 엔터키
        chatInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                sendChatMessage();
            }
        });

        // 회의실 나가기 버튼
        leaveRoomBtn.addEventListener('click', function() {
            if (confirm('회의에서 나가시겠습니까?')) {
                leaveRoom();
                window.location.href = '/videochat';
            }
        });
    }

    // 미디어 버튼 상태 업데이트
    function updateMediaButtons() {
        if (localStream) {
            const videoTracks = localStream.getVideoTracks();
            const audioTracks = localStream.getAudioTracks();

            // 비디오 버튼 상태 업데이트
            if (videoTracks.length > 0 && videoTracks[0].enabled) {
                cameraToggleBtn.classList.add('active');
            } else {
                cameraToggleBtn.classList.remove('active');
            }

            // 오디오 버튼 상태 업데이트
            if (audioTracks.length > 0 && audioTracks[0].enabled) {
                micToggleBtn.classList.add('active');
            } else {
                micToggleBtn.classList.remove('active');
            }
        }
    }

    // 카메라 토글
    function toggleCamera() {
        if (!localStream) return;

        const videoTracks = localStream.getVideoTracks();

        if (videoTracks.length === 0) {
            // 카메라가 없는 경우, 카메라 권한 요청
            navigator.mediaDevices.getUserMedia({ video: true })
                .then(stream => {
                    const videoTrack = stream.getVideoTracks()[0];
                    localStream.addTrack(videoTrack);

                    // 모든 피어 연결에 새 트랙 추가
                    Object.values(peerConnections).forEach(pc => {
                        const sender = pc.getSenders().find(s => s.track && s.track.kind === 'video');
                        if (sender) {
                            sender.replaceTrack(videoTrack);
                        } else {
                            pc.addTrack(videoTrack, localStream);
                        }
                    });

                    // 로컬 비디오 업데이트
                    const localVideo = document.querySelector('#local-video');
                    if (localVideo) {
                        localVideo.srcObject = localStream;
                    }

                    cameraToggleBtn.classList.add('active');
                })
                .catch(error => {
                    console.error('카메라 활성화 오류:', error);
                    alert('카메라를 활성화할 수 없습니다.');
                });
        } else {
            // 기존 비디오 트랙 토글
            const enabled = !videoTracks[0].enabled;
            videoTracks.forEach(track => {
                track.enabled = enabled;
            });

            if (enabled) {
                cameraToggleBtn.classList.add('active');
            } else {
                cameraToggleBtn.classList.remove('active');
            }
        }
    }

    // 마이크 토글
    function toggleMicrophone() {
        if (!localStream) return;

        const audioTracks = localStream.getAudioTracks();

        if (audioTracks.length === 0) {
            // 마이크가 없는 경우, 마이크 권한 요청
            navigator.mediaDevices.getUserMedia({ audio: true })
                .then(stream => {
                    const audioTrack = stream.getAudioTracks()[0];
                    localStream.addTrack(audioTrack);

                    // 모든 피어 연결에 새 트랙 추가
                    Object.values(peerConnections).forEach(pc => {
                        const sender = pc.getSenders().find(s => s.track && s.track.kind === 'audio');
                        if (sender) {
                            sender.replaceTrack(audioTrack);
                        } else {
                            pc.addTrack(audioTrack, localStream);
                        }
                    });

                    micToggleBtn.classList.add('active');
                })
                .catch(error => {
                    console.error('마이크 활성화 오류:', error);
                    alert('마이크를 활성화할 수 없습니다.');
                });
        } else {
            // 기존 오디오 트랙 토글
            const enabled = !audioTracks[0].enabled;
            audioTracks.forEach(track => {
                track.enabled = enabled;
            });

            if (enabled) {
                micToggleBtn.classList.add('active');
            } else {
                micToggleBtn.classList.remove('active');
            }
        }
    }

    // 화면 공유 토글
    async function toggleScreenShare() {
        try {
            if (!screenStream) {
                // 화면 공유 시작
                screenStream = await navigator.mediaDevices.getDisplayMedia({
                    video: true
                });

                // 화면 공유가 종료되었을 때의 이벤트 핸들러
                screenStream.getVideoTracks()[0].addEventListener('ended', () => {
                    stopScreenSharing();
                });

                // 화면 공유 트랙을 로컬 스트림에 추가
                const screenTrack = screenStream.getVideoTracks()[0];

                // 오디오 트랙 유지
                const audioTracks = localStream.getAudioTracks();

                // 새로운 스트림 생성
                const newStream = new MediaStream();

                // 오디오 트랙 추가
                audioTracks.forEach(track => newStream.addTrack(track));

                // 화면 공유 트랙 추가
                newStream.addTrack(screenTrack);

                // 로컬 스트림 교체
                localStream = newStream;

                // 로컬 비디오 엘리먼트 업데이트
                const localVideo = document.querySelector('#local-video');
                if (localVideo) {
                    localVideo.srcObject = localStream;
                }

                // 모든 피어 연결 업데이트
                Object.values(peerConnections).forEach(pc => {
                    const senders = pc.getSenders();
                    const videoSender = senders.find(sender =>
                        sender.track && sender.track.kind === 'video'
                    );

                    if (videoSender) {
                        videoSender.replaceTrack(screenTrack);
                    }
                });

                screenShareToggleBtn.classList.add('active');

                // 화면 공유 시작 알림
                sendParticipantEvent('screenShare', { sharing: true });

            } else {
                // 화면 공유 중지
                stopScreenSharing();
            }
        } catch (error) {
            console.error('화면 공유 오류:', error);

            if (error.name === 'NotAllowedError') {
                console.log('사용자가 화면 공유를 취소했습니다.');
            } else {
                alert('화면 공유를 시작할 수 없습니다: ' + error.message);
            }
        }
    }

    // 화면 공유 중지
    function stopScreenSharing() {
        if (!screenStream) return;

        // 화면 공유 트랙 중지
        screenStream.getTracks().forEach(track => track.stop());
        screenStream = null;

        // 카메라 비디오로 돌아가기
        navigator.mediaDevices.getUserMedia({
            video: true,
            audio: localStream.getAudioTracks().length > 0
        })
            .then(stream => {
                // 오디오 트랙 유지
                const audioTracks = localStream.getAudioTracks();

                // 새 스트림 생성
                const newStream = new MediaStream();

                // 오디오 트랙 추가
                audioTracks.forEach(track => newStream.addTrack(track));

                // 비디오 트랙 추가
                const videoTrack = stream.getVideoTracks()[0];
                if (videoTrack) {
                    newStream.addTrack(videoTrack);
                }

                // 로컬 스트림 교체
                localStream = newStream;

                // 로컬 비디오 엘리먼트 업데이트
                const localVideo = document.querySelector('#local-video');
                if (localVideo) {
                    localVideo.srcObject = localStream;
                }

                // 모든 피어 연결 업데이트
                Object.values(peerConnections).forEach(pc => {
                    const senders = pc.getSenders();
                    const videoSender = senders.find(sender =>
                        sender.track && sender.track.kind === 'video'
                    );

                    if (videoSender && videoTrack) {
                        videoSender.replaceTrack(videoTrack);
                    }
                });

                screenShareToggleBtn.classList.remove('active');

                // 화면 공유 종료 알림
                sendParticipantEvent('screenShare', { sharing: false });
            })
            .catch(error => {
                console.error('카메라 재활성화 오류:', error);

                // 오디오만 있는 스트림으로 전환
                const audioOnlyStream = new MediaStream(localStream.getAudioTracks());
                localStream = audioOnlyStream;

                // 로컬 비디오 엘리먼트 업데이트
                const localVideo = document.querySelector('#local-video');
                if (localVideo) {
                    localVideo.srcObject = localStream;
                }

                // 모든 피어 연결 업데이트
                Object.values(peerConnections).forEach(pc => {
                    const senders = pc.getSenders();
                    const videoSender = senders.find(sender =>
                        sender.track && sender.track.kind === 'video'
                    );

                    if (videoSender) {
                        pc.removeTrack(videoSender);
                    }
                });

                screenShareToggleBtn.classList.remove('active');
                cameraToggleBtn.classList.remove('active');

                // 화면 공유 종료 알림
                sendParticipantEvent('screenShare', { sharing: false });
            });
    }

    // 패널 토글 (채팅 또는 참가자 목록)
    function togglePanel(panelType) {
        const isCurrentPanel = sidePanel.style.display !== 'none' &&
            (panelType === 'chat' ? chatPanel.style.display !== 'none' : participantsPanel.style.display !== 'none');

        // 현재 표시중인 패널이면 닫기
        if (isCurrentPanel) {
            sidePanel.style.display = 'none';
            chatToggleBtn.classList.remove('active');
            participantsToggleBtn.classList.remove('active');
            return;
        }

        // 패널 표시
        sidePanel.style.display = 'flex';

        if (panelType === 'chat') {
            chatPanel.style.display = 'block';
            participantsPanel.style.display = 'none';
            panelTitle.textContent = '채팅';
            chatToggleBtn.classList.add('active');
            participantsToggleBtn.classList.remove('active');

            // 채팅 입력창에 포커스
            chatInput.focus();
        } else {
            chatPanel.style.display = 'none';
            participantsPanel.style.display = 'block';
            panelTitle.textContent = '참가자 목록';
            participantsToggleBtn.classList.add('active');
            chatToggleBtn.classList.remove('active');

            // 참가자 목록 새로고침
            fetchParticipants();
        }
    }

    // 채팅 메시지 전송
    function sendChatMessage() {
        const messageText = chatInput.value.trim();

        if (messageText) {
            const chatMessage = {
                sender: empNum,
                senderName: participants[empNum] ? participants[empNum].empName : '나',
                message: messageText,
                timestamp: new Date().toISOString()
            };

            stompClient.send('/app/videochat/chat/' + roomId, {}, JSON.stringify(chatMessage));

            // 입력창 비우기
            chatInput.value = '';
            chatInput.focus();
        }
    }

    // 채팅 메시지 처리
    function handleChatMessage(message) {
        const isFromMe = message.sender === empNum;
        const formattedTime = new Date(message.timestamp).toLocaleTimeString('ko-KR', {
            hour: '2-digit',
            minute: '2-digit'
        });

        const messageElement = document.createElement('div');
        messageElement.className = 'message' + (isFromMe ? ' self' : '');

        messageElement.innerHTML = `
            <div class="message-header">
                <span class="message-sender">${isFromMe ? '나' : message.senderName || message.sender}</span>
                <span class="message-time">${formattedTime}</span>
            </div>
            <div class="message-content">${message.message}</div>
        `;

        chatMessages.appendChild(messageElement);

        // 스크롤을 최신 메시지로 이동
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    // 참가자 이벤트 전송
    function sendParticipantEvent(type, data = {}) {
        const eventData = {
            type: type,
            sender: empNum,
            roomId: roomId,
            ...data
        };

        stompClient.send('/app/videochat/participant/' + roomId, {}, JSON.stringify(eventData));
    }

    // 참가자 이벤트 처리
    function handleParticipantEvent(event) {
        const participantId = event.sender;

        // 자신의 이벤트는 무시
        if (participantId === empNum) return;

        switch (event.type) {
            case 'join':
                // 새 참가자 입장
                console.log('새 참가자 입장:', participantId);

                // 새 참가자에게 오퍼 전송
                createPeerConnection(participantId);
                break;

            case 'leave':
                // 참가자 퇴장
                console.log('참가자 퇴장:', participantId);

                // 연결 종료 및 비디오 제거
                cleanupPeer(participantId);
                break;

            case 'screenShare':
                // 화면 공유 상태 변경
                console.log('화면 공유 상태 변경:', participantId, event.sharing);

                // 화면 공유 상태 UI 업데이트
                updateParticipantScreenShare(participantId, event.sharing);
                break;
        }
    }

    // 참가자 화면 공유 상태 업데이트
    function updateParticipantScreenShare(participantId, isSharing) {
        const videoContainer = document.getElementById(`video-container-${participantId}`);
        if (videoContainer) {
            if (isSharing) {
                videoContainer.classList.add('screen-sharing');
            } else {
                videoContainer.classList.remove('screen-sharing');
            }
        }
    }

    // 시그널링 메시지 전송
    function sendSignalingMessage(message) {
        stompClient.send('/app/videochat/signal/' + roomId, {}, JSON.stringify(message));
    }

    // 시그널링 메시지 처리
    function handleSignalingMessage(message) {
        const senderId = message.sender;

        // 자신의 메시지는 무시
        if (senderId === empNum) return;

        switch (message.type) {
            case 'offer':
                handleOfferMessage(senderId, message.offer);
                break;

            case 'answer':
                handleAnswerMessage(senderId, message.answer);
                break;

            case 'candidate':
                handleCandidateMessage(senderId, message.candidate);
                break;
        }
    }

// Offer 메시지 처리
    async function handleOfferMessage(senderId, offer) {
        try {
            const pc = createPeerConnection(senderId);

            await pc.setRemoteDescription(new RTCSessionDescription(offer));

            const answer = await pc.createAnswer();
            await pc.setLocalDescription(answer);

            sendSignalingMessage({
                type: 'answer',
                sender: empNum,
                receiver: senderId,
                answer: answer
            });
        } catch (error) {
            console.error('Offer 처리 오류:', error);
        }
    }

// Answer 메시지 처리
    async function handleAnswerMessage(senderId, answer) {
        try {
            const pc = peerConnections[senderId];

            if (pc) {
                await pc.setRemoteDescription(new RTCSessionDescription(answer));
            }
        } catch (error) {
            console.error('Answer 처리 오류:', error);
        }
    }

// ICE Candidate 메시지 처리
    async function handleCandidateMessage(senderId, candidate) {
        try {
            const pc = peerConnections[senderId];

            if (pc) {
                await pc.addIceCandidate(new RTCIceCandidate(candidate));
            }
        } catch (error) {
            console.error('ICE Candidate 처리 오류:', error);
        }
    }

// PeerConnection 생성
    function createPeerConnection(peerId) {
        // 이미 존재하는 연결이면 반환
        if (peerConnections[peerId]) {
            return peerConnections[peerId];
        }

        console.log('새 PeerConnection 생성:', peerId);

        // 새 PeerConnection 생성
        const pc = new RTCPeerConnection(configuration);
        peerConnections[peerId] = pc;

        // 로컬 스트림 추가
        if (localStream) {
            localStream.getTracks().forEach(track => {
                pc.addTrack(track, localStream);
            });
        }

        // ICE Candidate 이벤트 핸들러
        pc.onicecandidate = event => {
            if (event.candidate) {
                sendSignalingMessage({
                    type: 'candidate',
                    sender: empNum,
                    receiver: peerId,
                    candidate: event.candidate
                });
            }
        };

        // 원격 스트림 이벤트 핸들러
        pc.ontrack = event => {
            console.log('원격 트랙 수신:', peerId, event.streams[0]);

            // 원격 비디오 엘리먼트 생성
            createVideoElement(peerId, peerId, false, false, event.streams[0]);
        };

        // ICE 연결 상태 변경 이벤트 핸들러
        pc.oniceconnectionstatechange = event => {
            console.log('ICE 연결 상태 변경:', peerId, pc.iceConnectionState);

            // 연결이 끊어진 경우
            if (pc.iceConnectionState === 'disconnected' ||
                pc.iceConnectionState === 'failed' ||
                pc.iceConnectionState === 'closed') {

                cleanupPeer(peerId);
            }
        };

        // 연결 협상 필요 이벤트 핸들러
        pc.onnegotiationneeded = async () => {
            try {
                console.log('협상 시작:', peerId);

                const offer = await pc.createOffer();
                await pc.setLocalDescription(offer);

                sendSignalingMessage({
                    type: 'offer',
                    sender: empNum,
                    receiver: peerId,
                    offer: pc.localDescription
                });
            } catch (error) {
                console.error('협상 오류:', error);
            }
        };

        return pc;
    }

// PeerConnection 정리
    function cleanupPeer(peerId) {
        console.log('PeerConnection 정리:', peerId);

        // 비디오 엘리먼트 제거
        const videoElement = document.getElementById(`video-${peerId}`);
        if (videoElement) {
            videoElement.parentElement.remove();
        }

        // PeerConnection 종료
        const pc = peerConnections[peerId];
        if (pc) {
            pc.onicecandidate = null;
            pc.ontrack = null;
            pc.oniceconnectionstatechange = null;
            pc.onnegotiationneeded = null;

            pc.getSenders().forEach(sender => {
                if (sender.track) {
                    sender.track.stop();
                }
            });

            pc.close();
            delete peerConnections[peerId];
        }

        // 참가자 목록에서 제거
        delete participants[peerId];
    }
});
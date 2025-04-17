// ===== 화상회의 시스템 - 핵심 기능 및 상태 관리 =====

// 직원 정보 가져오기 (Thymeleaf에서 전달)
const employeeInfo = {
    empNum: /*[[${employee.empNum}]]*/ '',
    empName: /*[[${employee.name}]]*/ '',
    deptName: /*[[${employee.departmentName}]]*/ ''
};

// 사용자 정보 및 상태 관리
const state = {
    username: employeeInfo.empName,
    empNum: employeeInfo.empNum,
    deptName: employeeInfo.deptName,
    inputRoomPw: '',
    roomId: '',
    roomName: '',
    roomPassword: '',
    localStream: null,
    screenStream: null,
    peerConnections: {}, // 다른 참가자와의 연결을 저장
    isMuted: false,
    isVideoOff: false,
    isScreenSharing: false,
    participants: {},
    activeRooms: [], // 현재 활성화된 방 목록
    isLeaving: false, // 방 나가기 처리 중 여부
    connectionRetries: {}, // 피어별 연결 재시도 횟수
    maxConnectionRetries: 3 // 최대 연결 재시도 횟수
};

// 참가자 Map (empNum을 키로 사용)
const participantsMap = new Map();

// WebRTC 설정
const peerConnectionConfig = {
    'iceServers': [
        {'urls': 'stun:stun.stunprotocol.org:3478'},
        {'urls': 'stun:stun.l.google.com:19302'},
        {'urls': 'stun:stun1.l.google.com:19302'},
        {'urls': 'stun:stun2.l.google.com:19302'},
        {'urls': 'stun:stun3.l.google.com:19302'},
        {'urls': 'stun:stun4.l.google.com:19302'}
    ],
    iceCandidatePoolSize: 10
};

// WebSocket 관련 변수
let socket;
let stompClient;
let wsReconnectAttempts = 0;
const maxReconnectAttempts = 5;
let isIntentionalDisconnect = false;

// 하트비트 관련 변수
let heartbeatInterval = null;
const HEARTBEAT_INTERVAL = 15000; // 15초마다 하트비트 전송
const HEARTBEAT_TIMEOUT = 5000; // 하트비트 응답 대기 시간 (5초)
let lastHeartbeatResponse = Date.now(); // 마지막 하트비트 응답 시간
let heartbeatTimeoutId = null; // 하트비트 타임아웃 ID

// DOM 요소
const backButton = document.getElementById('backButton');
const meetingsContainer = document.getElementById('meetings-container');
const joinForm = document.getElementById('join-form');
const videoArea = document.getElementById('video-area');
const roomIdInput = document.getElementById('roomId');
const inputRoomPw = document.getElementById('input-roomPw');
const joinBtn = document.getElementById('joinBtn');
const cancelJoinFormBtn = document.getElementById('cancelJoinFormBtn');
const createRoomBtn = document.getElementById('createRoomBtn');
const refreshBtn = document.getElementById('refreshBtn');
const meetingsList = document.getElementById('meetings-list');
const emptyMeetings = document.getElementById('empty-meetings');
const videoContainer = document.getElementById('video-container');
const participantsList = document.getElementById('participants-list');
const toggleAudioBtn = document.getElementById('toggleAudio');
const toggleVideoBtn = document.getElementById('toggleVideo');
const shareScreenBtn = document.getElementById('shareScreen');
const leaveRoomBtn = document.getElementById('leaveRoom');
const roomInfo = document.getElementById('room-info');
const currentRoomId = document.getElementById('current-room-id');
const currentRoomName = document.getElementById('current-room-name');

// 비밀번호 모달 요소
const passwordModal = document.getElementById('passwordModal');
const roomPasswordInput = document.getElementById('roomPassword');
const confirmJoinBtn = document.getElementById('confirmJoinBtn');
const cancelJoinBtn = document.getElementById('cancelJoinBtn');
const closePasswordModal = document.getElementById('closePasswordModal');

// 방 생성 모달 요소
const createRoomModal = document.getElementById('createRoomModal');
const newRoomNameInput = document.getElementById('newRoomName');
const newRoomPasswordInput = document.getElementById('newRoomPassword');
const confirmCreateBtn = document.getElementById('confirmCreateBtn');
const cancelCreateBtn = document.getElementById('cancelCreateBtn');
const closeCreateRoomModal = document.getElementById('closeCreateRoomModal');

// 페이지 로드 시 이벤트 처리
window.addEventListener('DOMContentLoaded', function() {
    fetchActiveRooms();
    restoreRoomSession();
});

// 뒤로가기 버튼 처리
backButton.addEventListener('click', () => {
    if (state.roomId) {
        if (confirm('현재 진행 중인 회의가 있습니다. 정말 나가시겠습니까?')) {
            leaveRoom();
            window.location.href = '/';
        }
    } else {
        window.location.href = '/';
    }
});

// 새로고침 버튼 처리
refreshBtn.addEventListener('click', fetchActiveRooms);

// 방 참가 폼 취소 버튼
cancelJoinFormBtn.addEventListener('click', () => {
    joinForm.classList.add('hidden');
    meetingsContainer.classList.remove('hidden');
});

// 모달 외부 클릭 시 닫기
window.addEventListener('click', (event) => {
    if (event.target === passwordModal) {
        closePasswordModalFn();
    }
    if (event.target === createRoomModal) {
        closeCreateRoomModalFn();
    }
});

// 비밀번호 확인 버튼 클릭
confirmJoinBtn.addEventListener('click', () => {
    const password = roomPasswordInput.value.trim();
    if (!password) {
        showError('password-error', '비밀번호를 입력해주세요.');
        return;
    }

    state.roomPassword = password;
    hideError('password-error');

    // 비밀번호 검증 후 참가
    checkRoomValidity(state.roomId, password);
});

// 비밀번호 모달 취소 버튼 클릭
cancelJoinBtn.addEventListener('click', closePasswordModalFn);
closePasswordModal.addEventListener('click', closePasswordModalFn);

// 새 방 만들기 버튼 클릭
createRoomBtn.addEventListener('click', showCreateRoomModal);

// 방 생성 확인 버튼 클릭
confirmCreateBtn.addEventListener('click', () => {
    const roomName = newRoomNameInput.value.trim();
    const roomPassword = newRoomPasswordInput.value.trim();

    if (!roomName) {
        showError('room-name-error', '회의 이름을 입력해주세요.');
        newRoomNameInput.focus();
        return;
    }

    hideError('room-name-error');

    // 방 ID 생성
    const timestamp = new Date().getTime();
    const randomNum = Math.floor(Math.random() * 10000);
    state.roomId = `room-${timestamp}-${randomNum}`;
    state.roomName = roomName;
    state.roomPassword = roomPassword;

    showLoading();

    // 서버에 방 생성 정보 전송
    fetch('/api/videoconf/create-room', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            id: state.roomId,
            name: state.roomName,
            password: state.roomPassword,
            createdBy: state.empNum,
            maxParticipants: 8
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('방 생성에 실패했습니다.');
            }
            return response.json();
        })
        .then(room => {
            closeCreateRoomModalFn();
            joinRoom();
            showToast('success', '회의 생성 완료', '새 화상 회의가 생성되었습니다.');
        })
        .catch(error => {
            console.error('방 생성 오류:', error);
            showToast('error', '방 생성 실패', '회의 생성 중 오류가 발생했습니다. 다시 시도해주세요.');
        })
        .finally(() => {
            hideLoading();
        });
});

// 방 생성 모달 취소 버튼 클릭
cancelCreateBtn.addEventListener('click', closeCreateRoomModalFn);
closeCreateRoomModal.addEventListener('click', closeCreateRoomModalFn);

// 수동으로 방 ID 입력하여 참가하기
joinBtn.addEventListener('click', () => {
    const roomId = roomIdInput.value.trim();
    const roomPw = inputRoomPw.value.trim();

    if (!roomId) {
        showToast('warning', '입력 오류', '방 ID를 입력해주세요.');
        roomIdInput.focus();
        return;
    }

    state.roomId = roomId;
    state.roomName = roomId;
    state.roomPassword = roomPw;

    // 방 유효성 확인 후 참가
    checkRoomValidity(roomId, roomPw);
});

// 오디오 토글 기능
toggleAudioBtn.addEventListener('click', () => {
    if (!state.localStream) {
        showToast('warning', '마이크 없음', '마이크가 연결되어 있지 않습니다.');
        return;
    }

    const audioTracks = state.localStream.getAudioTracks();
    if (audioTracks.length === 0) {
        showToast('warning', '마이크 없음', '마이크가 연결되어 있지 않습니다.');
        return;
    }

    state.isMuted = !state.isMuted;

    audioTracks.forEach(track => {
        track.enabled = !state.isMuted;
    });

    if (state.isMuted) {
        toggleAudioBtn.innerHTML = '<i class="fas fa-microphone-slash"></i>';
        toggleAudioBtn.classList.add('mute');
        showToast('info', '음소거 켜짐', '마이크가 음소거되었습니다.');
    } else {
        toggleAudioBtn.innerHTML = '<i class="fas fa-microphone"></i>';
        toggleAudioBtn.classList.remove('mute');
        showToast('info', '음소거 꺼짐', '마이크가 활성화되었습니다.');
    }
});

// 비디오 토글 기능
toggleVideoBtn.addEventListener('click', () => {
    if (!state.localStream) {
        showToast('warning', '카메라 없음', '카메라가 연결되어 있지 않습니다.');
        return;
    }

    const videoTracks = state.localStream.getVideoTracks();
    if (videoTracks.length === 0) {
        showToast('warning', '카메라 없음', '카메라가 연결되어 있지 않습니다.');
        return;
    }

    state.isVideoOff = !state.isVideoOff;

    videoTracks.forEach(track => {
        track.enabled = !state.isVideoOff;
    });

    if (state.isVideoOff) {
        toggleVideoBtn.innerHTML = '<i class="fas fa-video-slash"></i>';
        toggleVideoBtn.classList.add('mute');
        showToast('info', '비디오 꺼짐', '카메라가 비활성화되었습니다.');

        // 로컬 비디오에 상태 메시지 표시
        const localVideoBox = document.querySelector('.video-box.local-video');
        if (localVideoBox) {
            let statusDiv = localVideoBox.querySelector('.video-status');
            if (!statusDiv) {
                statusDiv = document.createElement('div');
                statusDiv.className = 'video-status';
                localVideoBox.appendChild(statusDiv);
            }
            statusDiv.textContent = '카메라가 꺼져 있습니다';
        }
    } else {
        toggleVideoBtn.innerHTML = '<i class="fas fa-video"></i>';
        toggleVideoBtn.classList.remove('mute');
        showToast('info', '비디오 켜짐', '카메라가 활성화되었습니다.');

        // 로컬 비디오 상태 메시지 제거
        const localVideoBox = document.querySelector('.video-box.local-video');
        if (localVideoBox) {
            localVideoBox.querySelector('.video-status')?.remove();
        }
    }
});

// 화면 공유 기능
shareScreenBtn.addEventListener('click', async () => {
    try {
        if (!state.isScreenSharing) {
            // 화면 공유 시작
            showToast('info', '화면 공유 시작 중', '화면 공유를 시작합니다...');

            const screenStream = await navigator.mediaDevices.getDisplayMedia({
                video: true,
                audio: true
            });

            state.screenStream = screenStream;

            // 기존 비디오 트랙 대체
            const videoTrack = screenStream.getVideoTracks()[0];

            // 모든 피어 연결에 새 트랙 추가
            Object.values(state.peerConnections).forEach(pcWrapper => {
                if (pcWrapper.connection) {
                    const senders = pcWrapper.connection.getSenders();
                    const videoSender = senders.find(sender =>
                        sender.track && sender.track.kind === 'video'
                    );

                    if (videoSender) {
                        videoSender.replaceTrack(videoTrack);
                    }
                }
            });

            // 로컬 비디오 업데이트
            const localVideo = document.getElementById(`video-${state.username}`);
            if (localVideo) {
                localVideo.srcObject = screenStream;
            }

            // 화면 공유 종료 처리
            videoTrack.onended = () => {
                stopScreenSharing();
            };

            shareScreenBtn.innerHTML = '<i class="fas fa-desktop"></i>';
            shareScreenBtn.classList.add('mute');
            state.isScreenSharing = true;
            showToast('success', '화면 공유 시작', '화면 공유가 시작되었습니다.');
        } else {
            // 화면 공유 중지
            stopScreenSharing();
        }
    } catch (error) {
        console.error('화면 공유 오류:', error);
        if (error.name === 'NotAllowedError') {
            showToast('error', '권한 거부됨', '화면 공유 권한이 거부되었습니다.');
        } else {
            showToast('error', '화면 공유 실패', '화면 공유를 시작할 수 없습니다. 브라우저 권한을 확인해주세요.');
        }
    }
});

// 통화 종료 기능
leaveRoomBtn.addEventListener('click', () => {
    // 확인 대화상자 표시
    if (confirm('정말로 회의에서 나가시겠습니까?')) {
        leaveRoom();
    }
});

// 페이지 종료 시 리소스 정리
window.addEventListener('beforeunload', (event) => {
    // 방에 참가 중인 경우 떠나기 메시지 전송
    if (state.roomId && !state.isLeaving) {
        // 방 나가기 API 호출 (비동기 처리는 페이지 종료 시 완료되지 않을 수 있음)
        navigator.sendBeacon(`/api/videoconf/leave-room?roomId=${state.roomId}&empNum=${state.empNum}`);

        // 연결된 참가자들에게 알림 (가능한 경우)
        if (stompClient && stompClient.connected) {
            try {
                sendMessage({
                    from: state.username,
                    empNum: state.empNum,
                    type: 'leave',
                    roomId: state.roomId
                });
            } catch (e) {
                console.warn('떠나기 메시지 전송 중 오류:', e);
            }
        }
    }

    clearResources();
});

// 페이지 가시성 변경 이벤트 리스너 추가
document.addEventListener('visibilitychange', function() {
    if (document.visibilityState === 'hidden') {
        // 페이지가 백그라운드로 갔을 때 처리
        console.log("페이지가 백그라운드로 전환되었습니다.");

        // 하트비트 간격 늘리기
        if (heartbeatInterval) {
            clearInterval(heartbeatInterval);
            heartbeatInterval = setInterval(sendHeartbeat, HEARTBEAT_INTERVAL * 2); // 간격 2배로 늘림
        }
    } else if (document.visibilityState === 'visible' && state.roomId) {
        // 페이지가 다시 활성화되었을 때 처리
        console.log("페이지가 다시 활성화되었습니다.");

        // 하트비트 정상 간격으로 복원
        if (heartbeatInterval) {
            clearInterval(heartbeatInterval);
            heartbeatInterval = setInterval(sendHeartbeat, HEARTBEAT_INTERVAL);
        }

        // 방 상태 확인
        fetch(`/api/videoconf/check-room-validity?roomId=${state.roomId}`)
            .then(response => response.json())
            .then(result => {
                if (result.valid === false) {
                    console.log("방이 더 이상 유효하지 않습니다.");
                    showToast('warning', '회의 종료', "회의가 종료되었습니다.");
                    leaveRoom();
                }
            })
            .catch(error => {
                console.error("방 상태 확인 중 오류 발생:", error);
            });
    }
});
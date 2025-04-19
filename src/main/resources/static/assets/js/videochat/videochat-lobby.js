document.addEventListener('DOMContentLoaded', function() {
    // 모달 관련 요소
    const createRoomModal = document.getElementById('createRoomModal');
    const createRoomBtn = document.getElementById('createRoomBtn');
    const closeCreateRoomBtn = createRoomModal.querySelector('.close');
    const createRoomForm = document.getElementById('createRoomForm');

    const passwordModal = document.getElementById('passwordModal');
    const closePasswordBtn = passwordModal.querySelector('.close');
    const passwordForm = document.getElementById('passwordForm');

    const mediaPermissionModal = document.getElementById('mediaPermissionModal');
    const closeMediaPermissionBtn = mediaPermissionModal.querySelector('.close');
    const continueWithMediaBtn = document.getElementById('continueWithMedia');
    const cameraToggle = document.getElementById('cameraToggle');
    const micToggle = document.getElementById('micToggle');
    const mediaStatus = document.getElementById('mediaStatus');

    // 검색 관련 요소
    const searchInput = document.getElementById('searchInput');
    const searchButton = document.getElementById('searchButton');

    // 방 입장 버튼들
    const joinBtns = document.querySelectorAll('.join-btn');

    // 방 생성 모달 표시
    createRoomBtn.addEventListener('click', function() {
        createRoomModal.style.display = 'block';
    });

    // 방 생성 모달 닫기
    closeCreateRoomBtn.addEventListener('click', function() {
        createRoomModal.style.display = 'none';
    });

    // 비밀번호 모달 닫기
    closePasswordBtn.addEventListener('click', function() {
        passwordModal.style.display = 'none';
    });

    // 미디어 권한 모달 닫기
    closeMediaPermissionBtn.addEventListener('click', function() {
        mediaPermissionModal.style.display = 'none';
    });

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(event) {
        if (event.target === createRoomModal) {
            createRoomModal.style.display = 'none';
        }
        if (event.target === passwordModal) {
            passwordModal.style.display = 'none';
        }
        if (event.target === mediaPermissionModal) {
            mediaPermissionModal.style.display = 'none';
        }
    });

    // 방 생성 폼 제출
    createRoomForm.addEventListener('submit', function(e) {
        e.preventDefault();

        const roomName = document.getElementById('roomName').value;
        const maxParticipants = document.getElementById('maxParticipants').value;
        const roomPassword = document.getElementById('roomPassword').value;

        const roomData = {
            name: roomName,
            maxParticipants: parseInt(maxParticipants),
            password: roomPassword
        };

        // 방 생성 API 호출
        fetch('/api/videochat/rooms', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(roomData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('방 생성에 실패했습니다.');
                }
                return response.json();
            })
            .then(data => {
                // 생성된 방으로 이동하기 전에 미디어 권한 확인
                checkMediaPermissions(data.id, null);
                createRoomModal.style.display = 'none';
            })
            .catch(error => {
                alert('오류가 발생했습니다: ' + error.message);
            });
    });

    // 비밀번호 폼 제출
    passwordForm.addEventListener('submit', function(e) {
        e.preventDefault();

        const roomId = document.getElementById('passwordRoomId').value;
        const password = document.getElementById('inputPassword').value;

        // 비밀번호 입력 후 미디어 권한 확인
        checkMediaPermissions(roomId, password);
        passwordModal.style.display = 'none';
    });

    // 방 입장 버튼 클릭 이벤트
    joinBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const roomId = this.getAttribute('data-room-id');
            const passwordProtected = this.getAttribute('data-password-protected') === 'true';

            if (passwordProtected) {
                // 비밀번호가 필요한 경우 비밀번호 모달 표시
                document.getElementById('passwordRoomId').value = roomId;
                passwordModal.style.display = 'block';
            } else {
                // 비밀번호가 필요 없는 경우 바로 미디어 권한 확인
                checkMediaPermissions(roomId, null);
            }
        });
    });

    // 검색 기능
    searchButton.addEventListener('click', performSearch);
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            performSearch();
        }
    });

    function performSearch() {
        const searchTerm = searchInput.value.trim();

        // 검색 API 호출
        fetch('/api/videochat/rooms/search?name=' + encodeURIComponent(searchTerm))
            .then(response => {
                if (!response.ok) {
                    throw new Error('검색에 실패했습니다.');
                }
                return response.json();
            })
            .then(rooms => {
                updateRoomList(rooms);
            })
            .catch(error => {
                alert('오류가 발생했습니다: ' + error.message);
            });
    }

    // 검색 결과로 방 목록 업데이트
    function updateRoomList(rooms) {
        const roomGrid = document.querySelector('.room-grid');
        const emptyList = document.querySelector('.empty-list');

        if (rooms.length === 0) {
            if (emptyList) {
                emptyList.style.display = 'block';
            }
            if (roomGrid) {
                roomGrid.style.display = 'none';
            }
            return;
        }

        if (emptyList) {
            emptyList.style.display = 'none';
        }
        if (roomGrid) {
            roomGrid.style.display = 'grid';

            roomGrid.innerHTML = '';

            rooms.forEach(room => {
                const roomCard = document.createElement('div');
                roomCard.className = 'room-card';

                const formattedDate = new Date(room.createdAt).toLocaleString('ko-KR', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit'
                });

                roomCard.innerHTML = `
                    <div class="room-info">
                        <h3>${room.name}</h3>
                        <p>
                            <span class="room-id">ID: ${room.id}</span>
                            <span class="room-participants">
                                <i class="fa-solid fa-user"></i> 
                                ${room.currentParticipants}/${room.maxParticipants}
                            </span>
                        </p>
                        <p class="room-created">
                            ${formattedDate}
                            ${room.isPasswordProtected ? '<span class="room-locked"><i class="fa-solid fa-lock"></i> 비밀번호 보호</span>' : ''}
                        </p>
                    </div>
                    <div class="room-actions">
                        <button class="join-btn" data-room-id="${room.id}" data-password-protected="${room.isPasswordProtected}">
                            입장
                        </button>
                    </div>
                `;

                roomGrid.appendChild(roomCard);

                // 새로 추가된 버튼에 이벤트 리스너 추가
                const newJoinBtn = roomCard.querySelector('.join-btn');
                newJoinBtn.addEventListener('click', function() {
                    const roomId = this.getAttribute('data-room-id');
                    const passwordProtected = this.getAttribute('data-password-protected') === 'true';

                    if (passwordProtected) {
                        document.getElementById('passwordRoomId').value = roomId;
                        passwordModal.style.display = 'block';
                    } else {
                        checkMediaPermissions(roomId, null);
                    }
                });
            });
        }
    }

    // 미디어 권한 확인 함수
    function checkMediaPermissions(roomId, password) {
        let hasCamera = false;
        let hasMic = false;

        // 미디어 권한 모달 표시
        mediaPermissionModal.style.display = 'block';

        // 미디어 권한 상태 업데이트
        navigator.mediaDevices.enumerateDevices()
            .then(devices => {
                devices.forEach(device => {
                    if (device.kind === 'videoinput') {
                        hasCamera = true;
                    }
                    if (device.kind === 'audioinput') {
                        hasMic = true;
                    }
                });

                updateMediaStatus(hasCamera, hasMic);
            })
            .catch(error => {
                console.error('미디어 장치 목록 가져오기 오류:', error);
                updateMediaStatus(false, false);
            });

        // 미디어 상태 표시 업데이트
        function updateMediaStatus(hasCamera, hasMic) {
            let statusText = '';

            if (!hasCamera && !hasMic) {
                statusText = '카메라와 마이크가 감지되지 않았습니다. 회의실에 참여는 가능하지만 비디오와 오디오는 사용할 수 없습니다.';
                cameraToggle.checked = false;
                cameraToggle.disabled = true;
                micToggle.checked = false;
                micToggle.disabled = true;
            } else {
                if (!hasCamera) {
                    statusText += '카메라가 감지되지 않았습니다. ';
                    cameraToggle.checked = false;
                    cameraToggle.disabled = true;
                } else {
                    cameraToggle.disabled = false;
                }

                if (!hasMic) {
                    statusText += '마이크가 감지되지 않았습니다. ';
                    micToggle.checked = false;
                    micToggle.disabled = true;
                } else {
                    micToggle.disabled = false;
                }

                if (statusText === '') {
                    statusText = '카메라와 마이크가 정상적으로 감지되었습니다.';
                }
            }

            mediaStatus.textContent = statusText;
        }

        // 미디어 설정 후 회의실 입장
        continueWithMediaBtn.addEventListener('click', function() {
            const useCamera = cameraToggle.checked && !cameraToggle.disabled;
            const useMic = micToggle.checked && !micToggle.disabled;

            // 선택한 미디어 설정을 로컬 스토리지에 저장
            localStorage.setItem('videoChat_useCamera', useCamera);
            localStorage.setItem('videoChat_useMic', useMic);

            // 회의실로 이동
            let roomUrl = '/videochat/room/' + roomId;
            if (password) {
                roomUrl += '?password=' + encodeURIComponent(password);
            }

            window.location.href = roomUrl;
        }, { once: true }); // 이벤트 리스너는 한 번만 실행
    }

    // URL 파라미터에서 오류 메시지 확인
    const urlParams = new URLSearchParams(window.location.search);
    const errorParam = urlParams.get('error');

    if (errorParam) {
        let errorMessage = '';

        switch (errorParam) {
            case 'room_not_found':
                errorMessage = '존재하지 않거나 비활성화된 회의실입니다.';
                break;
            case 'wrong_password':
                errorMessage = '비밀번호가 올바르지 않습니다.';
                break;
            case 'room_full':
                errorMessage = '회의실 참가자 수가 초과되었습니다.';
                break;
            default:
                errorMessage = '오류가 발생했습니다.';
        }

        alert(errorMessage);
    }
});
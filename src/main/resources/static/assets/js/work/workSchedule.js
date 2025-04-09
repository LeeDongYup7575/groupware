document.addEventListener('DOMContentLoaded', function () {
    const calendarEl = document.getElementById('calendar');
    const modalBackdrop = document.getElementById('modalBackdrop');
    const eventModal = document.getElementById('eventModal');

    // 모달 닫기 함수를 전역으로 정의
    window.closeModal = function() {
        eventModal.style.display = 'none';
        modalBackdrop.style.display = 'none';
    };

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && eventModal.style.display === 'block') {
            closeModal();
        }
    });

    // 모달 외부 클릭시 닫기
    modalBackdrop.addEventListener('click', function() {
        closeModal();
    });

    const calendar = new FullCalendar.Calendar(calendarEl, {
        locale: 'ko',
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        eventTimeFormat: {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        },
        events: function (fetchInfo, successCallback, failureCallback) {
            fetch('/works/getWorkSchedule')
                .then(response => response.json())
                .then(data => {
                    const events = data.map(item => {
                        const event = {
                            title: item.title,
                            start: item.start,
                            end: item.end,
                            description: item.description,
                            allDay: item.end && !item.start.includes('T'),
                            extendedProps: {
                                type: item.type || 'normal',
                                description: item.description
                            }
                        };

                        // 스타일 조건
                        if (item.title.includes('휴가')) {
                            event.title = '🌴 ' + item.title;
                            event.backgroundColor = item.color || '#edd67a';
                            event.textColor = '#fff';
                            event.extendedProps.type = 'leave';
                        } else if (item.type === 'overtime' || item.title.includes('연장 근무')) {
                            event.title = '⏱️ ' + item.title;
                            event.backgroundColor = item.color || '#6f42c1';
                            event.textColor = '#fff';
                            event.extendedProps.type = 'overtime';
                        } else {
                            if (item.title.includes('출근')) {
                                event.backgroundColor = '#28a745'; // 초록
                                event.title = '🏢 ' + item.title;
                            } else if (item.title.includes('퇴근')) {
                                event.backgroundColor = '#007bff'; // 파랑
                                event.title = '🚶 ' + item.title;
                            } else if (item.title.includes('지각')) {
                                event.backgroundColor = '#dc3545'; // 빨강
                                event.title = '⚠️ ' + item.title;
                            }
                        }

                        return event;
                    });
                    successCallback(events);
                })
                .catch(error => {
                    console.error('Error fetching data:', error);
                    failureCallback(error);
                });
        },
        eventClick: function (info) {
            const { title, extendedProps, start, end } = info.event;
            const modalTitle = document.getElementById('modalTitle');
            const modalTime = document.getElementById('modalTime');
            const modalReason = document.getElementById('modalReason');

            // 모달 제목 설정
            modalTitle.innerText = title;

            // 모달 내용 초기화
            modalTime.innerHTML = '';
            modalReason.innerHTML = '';

            // 이벤트 유형에 따른 모달 내용 설정
            if (extendedProps.type === 'leave') {
                // 휴가 이벤트
                if (start && end) {
                    const formattedStart = start.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });
                    let formattedEnd = '';

                    if (end.getDate() !== start.getDate() || end.getMonth() !== start.getMonth() || end.getFullYear() !== start.getFullYear()) {
                        formattedEnd = end.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });
                        modalTime.innerHTML = `<i class="fas fa-calendar-alt"></i> 기간: ${formattedStart} ~ ${formattedEnd}`;
                    } else {
                        modalTime.innerHTML = `<i class="fas fa-calendar-day"></i> 날짜: ${formattedStart}`;
                    }
                }

                if (extendedProps.description) {
                    modalReason.innerHTML = `<i class="fas fa-comment"></i> ${extendedProps.description}`;
                }
            } else if (extendedProps.type === 'overtime') {
                // 연장 근무 이벤트
                if (start && end) {
                    const formattedDate = start.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });
                    const formattedStartTime = start.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', hour12: false });
                    const formattedEndTime = end.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', hour12: false });

                    modalTime.innerHTML = `
                        <i class="fas fa-calendar-day"></i> 날짜: ${formattedDate}<br>
                        <i class="fas fa-clock"></i> 시간: ${formattedStartTime} ~ ${formattedEndTime}
                    `;
                }

                if (extendedProps.description) {
                    modalReason.innerHTML = `<i class="fas fa-comment"></i> 사유: ${extendedProps.description}`;
                }
            } else {
                // 일반 출퇴근 이벤트
                if (start) {
                    const formattedDate = start.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });
                    const formattedTime = start.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', hour12: false });

                    modalTime.innerHTML = `
                        <i class="fas fa-calendar-day"></i> 날짜: ${formattedDate}<br>
                        <i class="fas fa-clock"></i> 시간: ${formattedTime}
                    `;
                }

                if (extendedProps.description) {
                    modalReason.innerHTML = `<i class="fas fa-info-circle"></i> ${extendedProps.description}`;
                }
            }

            // 모달 표시
            modalBackdrop.style.display = 'block';
            eventModal.style.display = 'block';
        }
    });

    calendar.render();
});
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
        googleCalendarApiKey: 'AIzaSyAmxOp9PrC83LxO6JmGFH3_rfhvpzOXiy8',
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
        eventSources: [
            // ✅ 1. 내 일정
            {
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
                                        description: item.description,
                                        originalEnd: item.end // 원래 종료일 저장
                                    }
                                };

                                // 스타일 지정
                                if (item.title.includes('휴가')) {
                                    event.title = '🌴 ' + item.title;
                                    event.backgroundColor = item.color || '#edd67a';
                                    event.textColor = '#fff';
                                    event.allDay = true;
                                    event.extendedProps.type = 'leave';

                                    // FullCalendar는 allDay 이벤트의 end date를 자동으로 다음날로 처리하므로
                                    // 별도로 조정할 필요가 없습니다.
                                }
                                else if (item.type === 'overtime' || item.title.includes('연장 근무')) {
                                    event.title = '⏱️ ' + item.title;
                                    event.backgroundColor = item.color || '#6f42c1';
                                    event.textColor = '#fff';
                                    event.extendedProps.type = 'overtime';
                                } else {
                                    if (item.title.includes('출근')) {
                                        event.backgroundColor = '#28a745';
                                        event.title = '🏢 ' + item.title;
                                    } else if (item.title.includes('퇴근')) {
                                        event.backgroundColor = '#007bff';
                                        event.title = '🚶 ' + item.title;
                                    } else if (item.title.includes('지각')) {
                                        event.backgroundColor = '#dc3545';
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
                }
            },
            // ✅ 2. 한국 공휴일
            {
                googleCalendarId: 'ko.south_korea#holiday@group.v.calendar.google.com',
                className: 'korean-holiday', // CSS에서 스타일링 가능
                color: '#ff9f89',           // 기본 배경색
                textColor: '#fff'           // 글자색
            }
        ],

        eventClick: function (info) {
            info.jsEvent.preventDefault();
            // ✅ 공휴일 이벤트는 모달 띄우지 않음
            // if (
            //     info.event.classNames.includes('korean-holiday') ||
            //     (info.event.extendedProps.description && info.event.extendedProps.description.includes('기념일을 숨기려면'))
            // ) {
            //     return;
            // }
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
                if (start) {
                    const formattedStart = start.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });

                    // end가 있고 start와 다른 경우에만 범위로 표시
                    if (end && end.getTime() > start.getTime()) {
                        // 종일 이벤트의 end는 exclusive이므로 하루를 빼서 표시해야 함
                        const adjustedEnd = new Date(end);
                        adjustedEnd.setDate(adjustedEnd.getDate() - 1);
                        const formattedEnd = adjustedEnd.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });

                        // start와 조정된 end가 같다면 하루짜리 휴가
                        if (start.toDateString() === adjustedEnd.toDateString()) {
                            modalTime.innerHTML = `<i class="fas fa-calendar-day"></i> 날짜: ${formattedStart}`;
                        } else {
                            modalTime.innerHTML = `<i class="fas fa-calendar-alt"></i> 기간: ${formattedStart} ~ ${formattedEnd}`;
                        }
                    } else {
                        // 하루짜리 휴가인 경우
                        modalTime.innerHTML = `<i class="fas fa-calendar-day"></i> 날짜: ${formattedStart}`;
                    }
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
                    // modalReason.innerHTML = `<i class="fas fa-info-circle"></i> ${extendedProps.description}`;
                }
            }

            // 모달 표시
            modalBackdrop.style.display = 'block';
            eventModal.style.display = 'block';
        }
    });

    calendar.render();
});
document.addEventListener('DOMContentLoaded', function() {
    const calendarEl = document.getElementById('calendar');

    const calendar = new FullCalendar.Calendar(calendarEl, {
        locale: 'ko',  // 한국어 설정
        initialView: 'dayGridMonth',  // 초기 뷰: 월간 뷰
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        // 중요! 이벤트 시간 표시 형식 설정
        eventTimeFormat: {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false  // 24시간 형식 사용
        },
        events: function(fetchInfo, successCallback, failureCallback) {
            // 서버에서 근태 데이터를 가져오기
            fetch('/works/getWorkSchedule')
                .then(response => response.json())
                .then(data => {
                    // 데이터를 FullCalendar에 맞게 변환하여 전달
                    const events = data.map(item => {
                        const event = {
                            title: item.title,
                            start: new Date(item.start),
                            description: item.description,
                            // 시간을 이벤트의 일부로 표시
                            displayEventTime: true
                        };

                        // 이벤트 색상 설정
                        if (item.title.includes('출근')) {
                            event.backgroundColor = '#28a745';  // 녹색
                        } else if (item.title.includes('퇴근')) {
                            event.backgroundColor = '#007bff';  // 파란색
                        } else if (item.title.includes('지각')) {
                            event.backgroundColor = '#dc3545';  // 빨간색
                        }

                        return event;
                    });
                    successCallback(events);  // FullCalendar에 이벤트 데이터 전달
                })
                .catch(error => {
                    console.error('Error fetching data:', error);
                    failureCallback(error);  // 오류 발생 시 실패 콜백 호출
                });
        },
        eventClick: function(info) {
            // 이벤트 클릭 시 동작
            const eventTime = info.event.start.toLocaleTimeString('ko-KR', {
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
            });
            alert(info.event.title + ' - ' + eventTime);
        }
    });

    calendar.render();
});
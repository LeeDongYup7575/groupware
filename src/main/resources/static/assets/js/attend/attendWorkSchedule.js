document.addEventListener('DOMContentLoaded', function() {
    // Initialize the add button
    document.querySelector(".add-button").onclick = function() {
        location.href = "/leaves/leavesForm";
    };

    // Set up dropdown menu functionality
    const dropdownHeader = document.querySelector('.dropdown-header');
    if (dropdownHeader) {
        dropdownHeader.addEventListener('click', function() {
            this.parentElement.classList.toggle('open');
        });
    }

    // Initialize calendar
    var calendarEl = document.getElementById('calendar');

    if (calendarEl) {
        var calendar = new FullCalendar.Calendar(calendarEl, {
            locale: 'ko',
            initialView: 'dayGridMonth',
            headerToolbar: {
                left: 'prev',
                center: 'title',
                right: 'next'
            },
            titleFormat: { year: 'numeric', month: 'long' },
            dayHeaderFormat: { weekday: 'short' },
            height: 'auto',
            fixedWeekCount: false,
            showNonCurrentDates: true,

            // Events definition (work schedules, vacations, etc.)
            events: [
                // Normal work days
                {
                    title: '정 09:00 - 18:00',
                    start: '2025-03-04',
                    end: '2025-03-05',
                    display: 'block',
                    backgroundColor: '#f5f5f5',
                    textColor: '#888'
                },
                {
                    title: '정 09:00 - 18:00',
                    start: '2025-03-05',
                    end: '2025-03-06',
                    display: 'block',
                    backgroundColor: '#f5f5f5',
                    textColor: '#888'
                },
                {
                    title: '정 09:00 - 18:00',
                    start: '2025-03-06',
                    end: '2025-03-07',
                    display: 'block',
                    backgroundColor: '#f5f5f5',
                    textColor: '#888'
                },
                {
                    title: '정 09:00 - 18:00',
                    start: '2025-03-07',
                    end: '2025-03-08',
                    display: 'block',
                    backgroundColor: '#f5f5f5',
                    textColor: '#888'
                },
                {
                    title: '정 09:00 - 18:00',
                    start: '2025-03-08',
                    end: '2025-03-09',
                    display: 'block',
                    backgroundColor: '#f5f5f5',
                    textColor: '#888'
                },
                // Late arrival
                {
                    title: '출근 09:16',
                    start: '2025-03-12',
                    display: 'block',
                    backgroundColor: '#fff',
                    textColor: '#888'
                },
                {
                    title: '지각',
                    start: '2025-03-12',
                    display: 'block',
                    backgroundColor: '#fff',
                    textColor: '#ff6b6b'
                },
                // Vacation
                {
                    title: '연차(종일)',
                    start: '2025-03-20',
                    display: 'block',
                    backgroundColor: '#fff',
                    textColor: '#4a9fff'
                },
                {
                    title: '연차(종일)',
                    start: '2025-03-21',
                    display: 'block',
                    backgroundColor: '#fff',
                    textColor: '#4a9fff'
                }
            ],

            // Custom rendering for date cells
            dayCellDidMount: function(info) {
                // Weekend color handling
                if (info.date.getDay() === 0) { // Sunday
                    info.el.style.backgroundColor = '#fff9f9';
                } else if (info.date.getDay() === 6) { // Saturday
                    info.el.style.backgroundColor = '#f9f9ff';
                }
            }
        });

        calendar.render();

        // Add weekly summary after calendar rendering
        setTimeout(addWeeklySummary, 100);
    }

    // Weekly summary function
    function addWeeklySummary() {
        // Create weekly summary container
        const summaryContainer = document.createElement('div');
        summaryContainer.className = 'weekly-summary';
        summaryContainer.style.position = 'absolute';
        summaryContainer.style.right = '0';
        summaryContainer.style.top = '72px';
        summaryContainer.style.width = '180px';
        summaryContainer.style.height = 'calc(100% - 72px)';
        summaryContainer.style.borderLeft = '1px solid #ddd';
        summaryContainer.style.backgroundColor = '#f9f9f9';
        summaryContainer.style.overflow = 'hidden';

        // Add title
        const title = document.createElement('div');
        title.textContent = '주간합계';
        title.style.padding = '10px';
        title.style.textAlign = 'center';
        title.style.fontWeight = 'bold';
        title.style.borderBottom = '1px solid #ddd';
        summaryContainer.appendChild(title);

        // Weekly data (example data that would normally come from API)
        const weeklyData = [
            { week: 1, worked: '00시간 00분', real: '00시간 00분' },
            { week: 2, worked: '00시간 00분', total: '40시간 00분', real: '00시간 00분' },
            { week: 3, worked: '00시간 00분', real: '00시간 00분' },
            { week: 4, worked: '16시간 00분', real: '00시간 00분' },
            { week: 5, worked: '00시간 00분', real: '00시간 00분' },
            { week: 6, worked: '00시간 00분', real: '00시간 00분' }
        ];

        // Add summary info for each week
        weeklyData.forEach((data, index) => {
            const weekSummary = document.createElement('div');
            weekSummary.style.padding = '10px';
            weekSummary.style.borderBottom = '1px solid #eee';
            weekSummary.style.fontSize = '12px';
            weekSummary.style.height = '108px'; // Height adjusted for a week

            weekSummary.innerHTML = `
                <div>휴가: ${data.worked}</div>
                ${data.total ? `<div>계: ${data.total}</div>` : ''}
                <div>실근무: ${data.real}</div>
            `;

            summaryContainer.appendChild(weekSummary);
        });

        // Add weekly summary element to the calendar container
        const calendarContainer = document.querySelector('.fc-view-harness');
        if (calendarContainer) {
            calendarContainer.style.position = 'relative';
            calendarContainer.appendChild(summaryContainer);

            // Adjust calendar width
            const calendar = document.querySelector('.fc');
            if (calendar) {
                calendar.style.width = 'calc(100% - 10px)';
            }
        }
    }
});
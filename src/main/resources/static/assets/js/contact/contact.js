document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);

    // tab이 없으면 기본적으로 shared 추가
    if (!urlParams.has('tab')) {
        urlParams.set('tab', 'shared');
        history.replaceState(null, '', window.location.pathname + '?' + urlParams.toString());
    }

    // URL에서 부서 정보 가져와서 필터링 적용
    const department = urlParams.get('dept') || 'all';
    filterList(department);

});

function filterList(team) {
    let rows = document.querySelectorAll("#contactList tr");

    let urlParams = new URLSearchParams(window.location.search);
    // URL에서 부서별 주소 변경
    if (team === 'all') {
        urlParams.delete('dept'); // 전체 보기일 경우 dept 파라미터 제거
    } else {
        urlParams.set('dept', team);
    }
    history.pushState({ dept: team }, '', window.location.pathname + '?' + urlParams.toString());
    // 리스트 필터링
    rows.forEach(row => {
        let department = row.dataset.team;
        row.style.display = (team === 'all' || department === team) ? "" : "none";
    });



    document.querySelectorAll('.contact-sidebar-item').forEach(item => {
        item.classList.remove('active');
    });

    document.querySelector(`[onclick="filterList('${team}')"]`).classList.add('active');

}

// 브라우저 뒤로 가기/앞으로 가기 이벤트 처리
window.addEventListener('popstate', function(event) {
    const urlParams = new URLSearchParams(window.location.search);
    const dept = urlParams.get('dept') || 'all';
    filterList(dept);
});

/**
 * 그룹웨어 사이드바 공통 JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {

    // 사이드바 토글 기능
    const sidebar = document.getElementById('sidebar');
    const sidebarToggle = document.getElementById('sidebarToggle');

    sidebarToggle.addEventListener('click', function() {
        sidebar.classList.toggle('open');
    });

    // 화면 크기가 변경될 때 처리
    window.addEventListener('resize', function() {
        if (window.innerWidth > 1200) {
            sidebar.classList.remove('open');
        }
    });

    // 사이드바 외부 클릭 시 닫기 (모바일)
    document.addEventListener('click', function(event) {
        if (window.innerWidth <= 1200 &&
            !sidebar.contains(event.target) &&
            !sidebarToggle.contains(event.target) &&
            sidebar.classList.contains('open')) {

            sidebar.classList.remove('open');
        }
    });
});
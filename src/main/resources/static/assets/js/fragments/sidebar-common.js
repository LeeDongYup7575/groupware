/**
 * 그룹웨어 사이드바 공통 JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {

    // 사이드바 토글 기능

    // 클래스가 'toggle-menu'인 모든 li 요소를 선택합니다.
    const toggleMenus = document.querySelectorAll('.toggle-menu');

    // 각 toggle-menu 요소에 대해 반복 작업을 수행합니다.
    toggleMenus.forEach(function(menuItem) {
        // 현재 li 요소 바로 아래의 a 태그를 찾습니다. (클릭될 요소)
        const toggleLink = menuItem.querySelector('a');
        // 현재 li 요소 바로 아래의 ul.submenu 태그를 찾습니다. (토글될 요소)
        const submenu = menuItem.querySelector('.submenu');
        // 현재 li 요소 바로 아래의 i 태그 (아이콘)를 찾습니다.
        const icon = menuItem.querySelector('a > i'); // a 태그 자식인 i 태그 선택

        // toggleLink와 submenu가 모두 존재하는지 확인합니다.
        if (toggleLink && submenu) {
            // toggleLink (a 태그)에 클릭 이벤트 리스너를 추가합니다.
            toggleLink.addEventListener('click', function(event) {
                // a 태그의 기본 동작(페이지 이동 등)을 막습니다.
                event.preventDefault();

                // submenu에 'open' 클래스를 추가하거나 제거하여 토글합니다.
                submenu.classList.toggle('open');

                // 아이콘의 클래스를 토글하여 모양을 변경합니다. (선택 사항)
                if (icon) {
                    icon.classList.toggle('fa-chevron-down');
                    icon.classList.toggle('fa-chevron-up');
                }

                // 부모 li 요소에도 'active' 클래스를 토글하여 상태를 표시할 수 있습니다. (선택 사항)
                // menuItem.classList.toggle('active');
            });

            // 초기 상태 설정: 페이지 로드 시 submenu가 'open' 클래스를 가지고 있지 않다면,
            // 아이콘은 'fa-chevron-down' 상태여야 합니다.
            // 만약 submenu가 기본적으로 열려있다면(예: open 클래스가 이미 있다면), 아이콘도 맞춰줘야 합니다.
            if (icon) {
                if (submenu.classList.contains('open')) {
                    // 이미 열려있으면 'up' 아이콘으로
                    icon.classList.remove('fa-chevron-down');
                    icon.classList.add('fa-chevron-up');
                } else {
                    // 닫혀있으면 'down' 아이콘으로
                    icon.classList.add('fa-chevron-down');
                    icon.classList.remove('fa-chevron-up');
                }
            }
        }
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
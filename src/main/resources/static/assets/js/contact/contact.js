document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);

    if (!urlParams.has('tab')) {
        urlParams.set('tab', 'shared');
        urlParams.set('dept', 'all');
        history.replaceState(null, '', window.location.pathname + '?' + urlParams.toString());
    }

    const tab = urlParams.get('tab');
    const dept = urlParams.get('dept') || 'all';

    fetchDepartments();  // 부서 목록 로드
    loadContacts(tab, dept);  // 초기 연락처 로드
    updateActiveSidebarItem(tab, dept);  // active 클래스 초기화

    // 주소 추가 버튼
    document.querySelector('.add-contact-btn').addEventListener('click', function () {
        document.getElementById('contact-modal').classList.remove('hidden');
    });

    // 주소 추가 취소 버튼
    document.querySelector('.contact-cancel-btn').addEventListener('click', function () {
        document.getElementById('contact-modal').classList.add('hidden');
    });
    // Esc 키 눌렀을 때 모달 닫기
    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            document.getElementById('contact-modal').classList.add('hidden');
        }
    });

});

// 부서 목록 불러오기
function fetchDepartments() {
    fetch('/api/departments')
        .then(response => {
            if (!response.ok) {
                throw new Error('부서 목록을 불러오는 데 실패했습니다.');
            }
            return response.json();
        })
        .then(departments => {
            const container = document.getElementById('shared-tab-container');
            departments.forEach(dept => {
                const div = document.createElement('div');
                div.className = 'contact-sidebar-item';
                div.textContent = dept.name;
                div.setAttribute('data-tab', 'shared');
                div.setAttribute('data-dept', dept.name);
                div.setAttribute('onclick', `handleSidebarClick('shared', '${dept.name}')`);
                container.appendChild(div);
            });
        })
        .catch(error => {
            console.error('부서 목록 로드 실패:', error);
        });
}

// 사이드바 클릭 함수
function handleSidebarClick(tab, dept = 'all') {
    const urlParams = new URLSearchParams();
    urlParams.set('tab', tab);

    // shared일 때만 dept 붙이기
    if (tab === 'shared') {
        urlParams.set('dept', dept);
    }

    history.pushState({}, '', window.location.pathname + '?' + urlParams.toString());
    loadContacts(tab, dept);
    updateActiveSidebarItem(tab, dept);
}

// 공유 or 개인 주소록 연락처 불러오기
function loadContacts(tab, dept) {
    let url = `/api/contact/${tab}`;
    if (tab === 'shared') {
        url += `?dept=${encodeURIComponent(dept)}`;
    }

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error("데이터를 불러오는 데 실패했습니다.");
            }
            return response.json();
        })
        .then(data => {
            renderContacts(data, tab);
        })
        .catch(error => {
            console.error(error);
            document.querySelector("#contactList").innerHTML = `
              <tr><td colspan="5">주소록을 불러오는 데 실패했습니다.</td></tr>`;
        });
}

// 연락처 렌더링
function renderContacts(data, tab) {
    const tbody = document.getElementById('contactList');
    tbody.innerHTML = '';

    // 테이블 헤더 텍스트 동적으로 변경
    document.querySelector('.contact-table thead .info-col').textContent = (tab === 'shared') ? '부서' : '메모';

    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="5">표시할 데이터가 없습니다.</td></tr>`;
        return;
    }

    data.forEach(contact => {
        const tr = document.createElement('tr');

        tr.setAttribute('data-team', contact.depName || '');

        tr.innerHTML = `
            <td class="cel"></td>
            <td class="name-col">${contact.name}</td>
            <td>${contact.internalEmail}</td>
            <td>${contact.phone}</td>
            <td class="info-col">${tab === 'shared' ? contact.depName : contact.memo || ''}</td>
        `;

        tbody.appendChild(tr);
    });
}

// active 클래스 토글 전용 함수
function updateActiveSidebarItem(tab, dept) {
    document.querySelectorAll('.contact-sidebar-item').forEach(item => {
        const itemTab = item.getAttribute('data-tab');
        const itemDept = item.getAttribute('data-dept');
        if (itemTab === tab && itemDept === dept) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });
}

// 브라우저 뒤로가기/앞으로가기 대응
window.addEventListener('popstate', function(event) {
    const urlParams = new URLSearchParams(window.location.search);
    const tab = urlParams.get('tab') || 'shared';
    let dept = urlParams.get('dept') || 'all';

    // personal이면 dept 무시
    if (tab === 'personal') {
        dept = 'all';
    }

    loadContacts(tab, dept);
    updateActiveSidebarItem(tab, dept);
});

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
        resetContactForm()

        document.getElementById('contact-modal').classList.add('hidden');
    });
    // Esc 키 눌렀을 때 모달 닫기
    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            resetContactForm()

            document.getElementById('contact-modal').classList.add('hidden');
        }
    });

    //  개인 주소록 연락처 등록 메모 글자 수 업데이트
    document.getElementById('memoInput').addEventListener('input', function () {
        const currentLength = this.value.length;
        document.getElementById('char-count').textContent = currentLength;
    });


    // 개인주소록 주소 저장
    document.getElementById('saveContactBtn').addEventListener('click', function () {
        console.log("버튼 클릭");
        const name = document.getElementById('nameInput').value.trim();
        const email = document.getElementById('emailInput').value;
        const phone = document.getElementById('phoneInput').value;
        const memo = document.getElementById('memoInput').value;

        if(!name) {
            alert('이름은 필수 입력 항목입니다.');
            document.getElementById('nameInput').focus();
            return;
        }

        const contactData = { name, email, phone, memo };

        fetch('/api/contact/personal/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(contactData)
        })
            .then(response => {
                resetContactForm()

                if (response.status === 201) {
                    alert('주소가 저장되었습니다.');
                    // 여기서 모달 닫기나 리스트 새로고침 같은 후처리 추가 가능
                    document.getElementById('contact-modal').classList.add('hidden');

                    loadContacts('personal', 'all');
                    updateActiveSidebarItem('personal', 'all');
                } else {
                    alert('저장에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('저장 중 오류 발생:', error);
                alert('서버 오류로 저장에 실패했습니다.');
            });
    });
});

// 개인 주소록 연락처 추가 input 초기화
function resetContactForm() {
    document.getElementById('nameInput').value = '';
    document.getElementById('emailInput').value = '';
    document.getElementById('phoneInput').value = '';
    document.getElementById('memoInput').value = '';
    document.getElementById('char-count').textContent = '0';
}

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
            <td>${tab === 'shared' ? contact.internalEmail : contact.email || ''}</td>
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

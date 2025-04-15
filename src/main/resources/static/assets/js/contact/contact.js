document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const search = urlParams.get('search');
    const tab = urlParams.get('tab') || 'shared';
    const dept = urlParams.get('dept') || 'all';

    fetchDepartments().then(() => {
        if (search && search.trim() !== "") {
            const searchInput = document.getElementById('searchInput');
            if (searchInput) searchInput.value = search;

            document.querySelectorAll('.contact-sidebar-item').forEach(item => {
                item.classList.remove('active');
            });

            searchContacts(search);
        } else {
            loadContacts(tab, dept);
            updateActiveSidebarItem(tab, dept); // 반드시 부서 DOM이 존재한 뒤에 호출
        }
    });


    // 주소 추가 버튼
    document.querySelector('.add-contact-btn').addEventListener('click', function () {
        const modal = document.getElementById('contact-modal');
        modal.removeAttribute('data-mode');
        modal.removeAttribute('data-id');

        modal.classList.remove('hidden');
    });

    // 주소 추가 취소 버튼
    document.querySelector('.contact-cancel-btn').addEventListener('click', function () {
        resetContactForm()

        document.getElementById('contact-modal').classList.add('hidden');
    });

    // Esc 키 눌렀을 때 모달 닫기
    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            const contactModal = document.getElementById('contact-modal');
            const detailModal = document.getElementById('contact-detail-modal');

            if (!contactModal.classList.contains('hidden')) {
                resetContactForm();
                contactModal.classList.add('hidden');
            }

            if (!detailModal.classList.contains('hidden')) {
                detailModal.classList.add('hidden');
            }
        }
    });


    //  개인 주소록 연락처 등록 메모 글자 수 업데이트
    document.getElementById('memoInput').addEventListener('input', function () {
        const currentLength = this.value.length;
        document.getElementById('char-count').textContent = currentLength;
    });


    // 개인주소록 주소 저장
    document.getElementById('saveContactBtn').addEventListener('click', function () {
        const name = document.getElementById('nameInput').value.trim();
        const email = document.getElementById('emailInput').value;
        const phone = document.getElementById('phoneInput').value;
        const memo = document.getElementById('memoInput').value;

        const phonePattern = /^010-\d{4}-\d{4}$/;
        const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if(!name) {
            alert('이름은 필수 입력 항목입니다.');
            document.getElementById('nameInput').focus();
            return;
        }

        if (email && !emailPattern.test(email)) {
            alert('이메일 형식이 올바르지 않습니다.');
            return;
        }

        if (phone && !phonePattern.test(phone)) {
            alert('전화번호는 010-XXXX-XXXX 형식으로 입력해주세요.');
            return;
        }

        const contactData = { name, email, phone, memo };

        const modal = document.getElementById('contact-modal');
        const mode = modal.getAttribute('data-mode');

        if (mode === 'edit') {
            const id = modal.getAttribute('data-id');

            fetch(`/api/contact/personal/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(contactData)
            })
                .then(res => {
                    if (res.ok) {
                        resetContactForm();
                        modal.classList.add('hidden');

                        const urlParams = new URLSearchParams(window.location.search);
                        const search = urlParams.get('search');

                        if (search && search.trim() !== '') {
                            searchContacts(search); // 검색 중이면 다시 검색
                        } else {
                            loadContacts('personal', 'all'); // 일반 모드면 전체 다시 로드
                        }
                    } else {
                        alert('수정에 실패했습니다.');
                    }
                })
                .catch(error => {
                    alert('서버 오류로 수정에 실패했습니다.');
                });

        } else {
            // 추가일 경우 기존 POST 요청 유지
            fetch('/api/contact/personal/add', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(contactData)
            })
                .then(res => {
                    resetContactForm();

                    if (res.status === 201) {
                        modal.classList.add('hidden');

                        const urlParams = new URLSearchParams(window.location.search);
                        const search = urlParams.get('search');

                        if (search && search.trim() !== '') {
                            searchContacts(search); // ✅ 검색 중이면 다시 검색
                        } else {
                            loadContacts('personal', 'all'); // 일반 상태면 전체 다시 로드
                        }
                    } else {
                        alert('저장에 실패했습니다.');
                    }
                })
                .catch(error => {
                    alert('서버 오류로 저장에 실패했습니다.');
                });
        }
    });

    // 개인 주소록 삭제 이벤트
    document.addEventListener('click', function (e) {
        if (e.target.id === 'deleteContactsBtn') {
            // 검색 상태 여부 확인
            const urlParams = new URLSearchParams(window.location.search);
            const isSearchMode = urlParams.has('search');

            // 검색된 상태에서는 검색결과 중 personal 테이블에서만 체크된 항목 찾기
            let checked = [];
            if (isSearchMode) {
                const personalSearchTable = document.querySelector('#searchResultsContainer .contact-table.personal');
                if (personalSearchTable) {
                    checked = personalSearchTable.querySelectorAll('.contact-checkbox:checked');
                }
            } else {
                // 일반 모드일 때는 기본 테이블에서 찾기
                const visibleTable = Array.from(document.querySelectorAll('.contact-table'))
                    .find(table => getComputedStyle(table).display !== 'none');
                if (visibleTable) {
                    checked = visibleTable.querySelectorAll('.contact-checkbox:checked');
                }
            }

            if (checked.length === 0) {
                alert('삭제할 항목을 선택해주세요.');
                return;
            }

            if (!confirm(`${checked.length}개의 주소를 삭제하시겠습니까?`)) {
                return;
            }

            const ids = Array.from(checked).map(cb => Number(cb.closest('tr').dataset.id));


            fetch('/api/contact/personal/delete', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(ids)
            })
                .then(response => {
                    if (response.ok) {
                        // 주소록 다시 불러오기
                        const urlParams = new URLSearchParams(window.location.search);
                        const search = urlParams.get('search');

                        if (search) {
                            // 검색된 상태라면 다시 검색
                            searchContacts(search);
                        } else {
                            // 일반 탭 로딩
                            loadContacts('personal', 'all');
                            updateHeaderForSelection();
                        }
                    } else {
                        alert('삭제에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('삭제 중 오류 발생:', error);
                    alert('서버 오류로 삭제에 실패했습니다.');
                });
        }
    });


    // 개인 주소록 상세 보기 모달 수정 버튼 이벤트리스너
    document.getElementById('editContactBtn').addEventListener('click', function () {
        const contact = this.contactData;

        if (!contact) return;

        // input에 값 세팅
        document.getElementById('nameInput').value = contact.name || '';
        document.getElementById('emailInput').value = contact.email || '';
        document.getElementById('phoneInput').value = contact.phone || '';
        document.getElementById('memoInput').value = contact.memo || '';
        document.getElementById('char-count').textContent = contact.memo?.length || 0;

        // 수정 모드로 세팅
        const modal = document.getElementById('contact-modal');
        modal.setAttribute('data-mode', 'edit');
        modal.setAttribute('data-id', contact.id);

        document.getElementById('saveContactBtn').textContent = '수정';

        // 모달 전환
        document.getElementById('contact-detail-modal').classList.add('hidden');
        modal.classList.remove('hidden');
    });



    // 전체 선택 체크박스 이벤트
    document.addEventListener('change', function (e) {
        if (e.target.classList.contains('select-all-checkbox')) {
            const isChecked = e.target.checked;
            const checkboxes = document.querySelectorAll('.contact-checkbox');
            checkboxes.forEach(cb => cb.checked = isChecked);
        }
    });


    // 체크박스 변화 감지해서 헤더 동적으로 변경
    document.addEventListener('change', function (e) {
        // 개별 체크박스 or 전체 선택 체크박스일 때만 처리
        if (e.target.classList.contains('contact-checkbox') || e.target.classList.contains('select-all-checkbox')) {
            updateHeaderForSelection();
            updateSelectAllCheckbox();
        }
    });

    // 검색 입력 이벤트 리스너 추가
    document.getElementById('searchInput').addEventListener('input', debounce(function(e) {
        const query = e.target.value.trim();

        document.querySelectorAll('.contact-sidebar-item').forEach(item => {
            item.classList.remove('active');
        });

        // 검색어가 있으면 새로운 URLParams 객체를 만들어 search 파라미터만 설정
        const params = new URLSearchParams();
        params.set("search", query);
        history.replaceState(null, '', window.location.pathname + '?' + params.toString());

        // 검색 API 호출 (검색 결과 렌더링)
        searchContacts(query);

    }, 300));

    document.getElementById('searchInputDelBtn').addEventListener('click', function () {
        const input = document.getElementById('searchInput');
        input.value = '';

        // input 이벤트 강제 발생시켜서 기존 검색 로직 그대로 작동하도록 함
        input.dispatchEvent(new Event('input'));
    });

});

// 개인 주소록 연락처 추가 input 초기화
function resetContactForm() {
    document.getElementById('nameInput').value = '';
    document.getElementById('emailInput').value = '';
    document.getElementById('phoneInput').value = '';
    document.getElementById('memoInput').value = '';
    document.getElementById('char-count').textContent = '0';
    document.getElementById('saveContactBtn').textContent = '저장';

}

// 부서 목록 불러오기
function fetchDepartments() {
    return fetch('/api/departments') // Promise 반환
        .then(response => {
            if (!response.ok) {
                throw new Error('부서 목록을 불러오는 데 실패했습니다.');
            }
            return response.json();
        })
        .then(departments => {
            const container = document.getElementById('shared-tab-container');
            container.innerHTML = "";

            // ✅ "전체" 항목 먼저 추가
            const allDiv = document.createElement('div');
            allDiv.className = 'contact-sidebar-item';
            allDiv.textContent = '전체';
            allDiv.setAttribute('data-tab', 'shared');
            allDiv.setAttribute('data-dept', 'all');
            allDiv.setAttribute('onclick', `handleSidebarClick('shared', 'all')`);
            container.appendChild(allDiv);

            // ✅ 부서들 추가
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
    // 검색 input 비우기
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.value = "";
    }

    // URL 파라미터에서 search 파라미터 제거
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.delete('search');
    urlParams.set('tab', tab);
    if (tab === 'shared') {
        urlParams.set('dept', dept);
    }
    history.pushState({}, '', window.location.pathname + '?' + urlParams.toString());

    // 검색 결과 컨테이너 숨김
    const searchContainer = document.getElementById("searchResultsContainer");
    if (searchContainer) {
        searchContainer.style.display = "none";
    }

    // 메인 테이블 복원
    const mainTable = document.getElementById("mainContactTable");
    if (mainTable) {
        mainTable.style.display = "";
    }

    // 일반 연락처 데이터 로드
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
            updateHeaderForSelection(); // 테이블 헤더 초기화
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

    // info-col 헤더 변경
    document.querySelector('.contact-table thead .info-col').textContent = (tab === 'shared') ? '부서' : '메모';

    // 헤더 체크박스 동적 처리
    const headerCel = document.querySelector('.contact-table thead .cel');
    if (tab === 'personal') {
        headerCel.innerHTML = `<input type="checkbox" class="select-all-checkbox">`;
    } else {
        headerCel.innerHTML = ''; // 공유 탭이면 제거
    }


    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="5">표시할 데이터가 없습니다.</td></tr>`;
        return;
    }

    data.forEach(contact => {
        const tr = document.createElement('tr');

        tr.setAttribute('data-team', contact.depName || '');

        // 개인 연락처일 경우 data-id 속성 추가
        if (tab === 'personal') {
            tr.setAttribute('data-id', contact.id); // ✅ 추가
        }

        tr.innerHTML = `
            <td class="cel">
                ${tab === 'personal' ? `<input type="checkbox" class="contact-checkbox">` : ''}
            </td>
            <td class="name-col">${contact.name}</td>
            <td>
                <a href="#" class="email-link" onclick="openMailComposePopup('${tab === 'shared' ? contact.internalEmail : contact.email || ''}')">
                    ${tab === 'shared' ? contact.internalEmail : contact.email || ''}
                </a>
            </td>
            <td>${contact.phone}</td>
            <td class="info-col">${tab === 'shared' ? contact.depName : contact.memo || ''}</td>
        `;

        // 연락처 행 클릭 감시 (상세 보기 모달 띄우기 위해)
        tr.addEventListener('click', function (e) {
            if (e.target.classList.contains('contact-checkbox') ||
                e.target.tagName === 'A'
            ) return; // 체크박스 또는 이메일 링크 클릭은 무시

            openDetailModal(contact, tab);
        });

        tbody.appendChild(tr);
    });
}

// 메일 전송 팝업
function openMailComposePopup(email) {
    if (!email) return;

    const width = 600;
    const height = 600;

    // 현재 창의 화면 크기 기준 위치 계산
    const left = window.screenX + window.outerWidth - width - 20;  // 오른쪽에서 20px 띄움
    const top = window.screenY + window.outerHeight - height - 40; // 아래에서 40px 띄움

    const url = `http://techx.kro.kr:8081/roundcube/?_task=mail&_action=compose&_to=${encodeURIComponent(email)}`;
    window.open(url, 'composeMail',
        `width=${width},height=${height},left=${left},top=${top},resizable=yes,scrollbars=yes`);
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
    const search = urlParams.get('search');

    // 검색 상태라면 검색 처리
    if (search && search.trim() !== "") {
        // input 값도 업데이트해주기
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.value = search;
        }

        // 사이드바 active 제거
        document.querySelectorAll('.contact-sidebar-item').forEach(item => {
            item.classList.remove('active');
        });

        searchContacts(search);
        return;
    }

    // 검색이 아니면 기본 주소록 로드
    const tab = urlParams.get('tab') || 'shared';
    let dept = urlParams.get('dept') || 'all';
    if (tab === 'personal') {
        dept = 'all';
    }

    // 검색 input 초기화
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.value = "";
    }

    // 검색결과 영역 숨기고 메인 테이블 표시
    const searchContainer = document.getElementById("searchResultsContainer");
    if (searchContainer) {
        searchContainer.style.display = "none";
    }
    const mainTable = document.getElementById("mainContactTable");
    if (mainTable) {
        mainTable.style.display = "";
    }

    loadContacts(tab, dept);
    updateActiveSidebarItem(tab, dept);
});



// 체크 여부 판단
function updateSelectAllCheckbox() {
    const urlParams = new URLSearchParams(window.location.search);
    const isSearchMode = urlParams.has('search');

    let allCheckboxes = [];
    let checkedCheckboxes = [];
    let selectAll;

    if (isSearchMode) {
        // 검색 결과 personal 테이블 기준
        const personalSearchTable = document.querySelector('#searchResultsContainer .contact-table.personal');
        if (personalSearchTable) {
            allCheckboxes = personalSearchTable.querySelectorAll('.contact-checkbox');
            checkedCheckboxes = personalSearchTable.querySelectorAll('.contact-checkbox:checked');
            selectAll = personalSearchTable.querySelector('.select-all-checkbox');
        }
    } else {
        // 일반 personal 테이블 기준
        const visibleTable = Array.from(document.querySelectorAll('.contact-table'))
            .find(table => getComputedStyle(table).display !== 'none');
        if (visibleTable) {
            allCheckboxes = visibleTable.querySelectorAll('.contact-checkbox');
            checkedCheckboxes = visibleTable.querySelectorAll('.contact-checkbox:checked');
            selectAll = visibleTable.querySelector('.select-all-checkbox');
        }
    }

    if (!selectAll) return;

    selectAll.checked = (allCheckboxes.length > 0 && checkedCheckboxes.length === allCheckboxes.length);
}




// 개인주소록 연락처 체크박스 선택 시 버튼 처리
function updateHeaderForSelection() {
    // 모든 contact-table 요소를 가져와서, 현재 보이는 테이블만 업데이트
    const tables = document.querySelectorAll('.contact-table');
    tables.forEach(table => {
        // getComputedStyle으로 현재 display 상태 확인 (숨겨지지 않은 테이블만 처리)
        if (getComputedStyle(table).display === 'none') return;

        const thNameCol = table.querySelector('thead .name-col');
        const thEmail = table.querySelector('thead th:nth-child(3)');
        const thPhone = table.querySelector('thead th:nth-child(4)');
        const thInfo = table.querySelector('thead .info-col');

        // 각 테이블 내에서 선택된 체크박스 개수 계산 (개인 주소록 테이블에 해당)
        const checkedCount = table.querySelectorAll('.contact-checkbox:checked').length;
        const urlParams = new URLSearchParams(window.location.search);
        const tab = urlParams.get('tab') || 'shared';

        if (checkedCount > 0) {
            // 선택된 항목이 있으면 헤더의 나머지 열 텍스트 숨기고, 이름 열은 삭제 버튼으로 대체
            thEmail.textContent = '';
            thPhone.textContent = '';
            thInfo.textContent = '';
            thNameCol.innerHTML = `<button id="deleteContactsBtn" class="delete-btn">삭제</button>`;
        } else {
            // 선택된 항목이 없으면 원래 텍스트 복원
            thEmail.textContent = '이메일';
            thPhone.textContent = '전화번호';
            thInfo.textContent = (tab === 'shared') ? '부서' : '메모';
            thNameCol.textContent = '이름';
        }
    });
}


// 연락처 상세 보기 모달 열기 닫기
function openDetailModal(contact, tab) {
    setFieldVisibility('detailName', contact.name);
    setFieldVisibility('detailEmail', tab === 'shared' ? contact.internalEmail : contact.email);
    setFieldVisibility('detailPhone', contact.phone);

    const editBtn = document.getElementById('editContactBtn');

    if (tab === 'shared') {
        document.getElementById('sharedOnly').style.display = '';
        document.getElementById('personalOnly').style.display = 'none';

        setFieldVisibility('detailDept', contact.depName);
        setFieldVisibility('detailPosition', contact.posTitle);

        // 공유 주소록은 수정 버튼 숨김
        editBtn.classList.add('hidden');
    } else {
        document.getElementById('sharedOnly').style.display = 'none';
        document.getElementById('personalOnly').style.display = '';

        setFieldVisibility('detailMemo', contact.memo);

        // 개인 주소록은 수정 버튼 표시
        editBtn.classList.remove('hidden');
        editBtn.setAttribute('data-id', contact.id); // 수정 시 사용할 ID 저장
        editBtn.contactData = contact;
    }

    document.getElementById('contact-detail-modal').classList.remove('hidden');
}

// 연락처 상세 보기 모달 숨기기
function closeDetailModal() {
    document.getElementById('contact-detail-modal').classList.add('hidden');
}

// 값이 없으면 해당 form-group 숨기고, 값이 있으면 보여줌
function setFieldVisibility(fieldId, value) {
    const field = document.getElementById(fieldId);
    const group = field?.closest('.form-group');

    if (!field || !group) return;

    if (value && value.trim() !== '') {
        field.textContent = value;
        group.style.display = '';
    } else {
        group.style.display = 'none';
    }
}

// 헬퍼 함수: 디바운스(debounce) – 입력 이벤트 과다 호출 방지
function debounce(func, delay) {
    let timer;
    return function(...args) {
        clearTimeout(timer);
        timer = setTimeout(() => { func.apply(this, args); }, delay);
    }
}

// 검색 API 호출 함수 (서버는 입력된 값에 대해 공유/개인 결과 모두 반환)
function searchContacts(query) {
    fetch(`/api/contact/search?query=${query}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("검색 결과를 불러오는 데 실패했습니다.");
            }
            return response.json();
        })
        .then(data => {
            console.log("검색 API 응답:", data);
            renderSearchResults(data);
        })
        .catch(error => {
            console.error(error);
            // 오류시 사용자에게 알림 처리 가능
        });
}

// 검색 결과 렌더링: 공유와 개인 각각 드롭다운 섹션으로 출력
function renderSearchResults(data) {
    // 기존 테이블 숨기기
    document.querySelector(".contact-table").style.display = "none";

    // 검색 결과 컨테이너가 없으면 생성
    let container = document.getElementById("searchResultsContainer");
    if (!container) {
        container = document.createElement("div");
        container.id = "searchResultsContainer";
        document.querySelector(".contact-main").appendChild(container);
    }
    container.style.display = "";
    container.innerHTML = "";  // 이전 검색 결과 초기화

    // 공유 주소록 검색 결과 섹션
    const sharedSection = createSearchSection("공유 주소록 검색 결과", data.shared, "shared");
    container.appendChild(sharedSection);

    // 개인 주소록 검색 결과 섹션
    const personalSection = createSearchSection("개인 주소록 검색 결과", data.personal, "personal");
    container.appendChild(personalSection);
}

// 드롭다운 섹션 생성 (각 섹션의 제목, 결과 개수, 테이블)
function createSearchSection(title, results, tab) {
    const section = document.createElement("div");
    section.className = "search-section";

    // 헤더 (드롭다운 토글)
    const header = document.createElement("div");
    header.className = "search-section-header";

    const titleSpan = document.createElement("span");
    titleSpan.textContent = `${title} (${results.length}건)`;
    header.appendChild(titleSpan);

    // (개인 탭에서는 삭제 버튼을 미리 넣지 않고, 체크박스 선택 시 updateHeaderForSelection 함수가 삭제 버튼을 대신 넣음)
    const arrow = document.createElement("span");
    arrow.textContent = "▼";
    header.appendChild(arrow);

    // 테이블 생성 : 기존 contact-table 마크업 재사용
    const table = document.createElement("table");
    table.className = "contact-table";
    // personal 결과인 경우 추가 클래스 부여 (필요 시 updateHeaderForSelection에서 여러 테이블에 대해 적용할 수 있도록)
    if (tab === "personal") {
        table.classList.add("personal");
    }

    // 테이블 헤더 생성 (전체 선택 체크박스 및 각 열 제목 유지)
    const thead = document.createElement("thead");
    const trHead = document.createElement("tr");

    // 첫번째 열 : 개인일 경우 전체 선택 체크박스, 공유이면 빈 셀
    const thCel = document.createElement("th");
    thCel.className = "cel";
    if (tab === "personal") {
        thCel.innerHTML = `<input type="checkbox" class="select-all-checkbox">`;
    }
    trHead.appendChild(thCel);

    // 이름 열 (초기에는 단순 텍스트 "이름"을 표시)
    const thName = document.createElement("th");
    thName.className = "name-col";
    thName.textContent = "이름";
    trHead.appendChild(thName);

    // 이메일 열
    const thEmail = document.createElement("th");
    thEmail.className = "email-col";
    thEmail.textContent = "이메일";
    trHead.appendChild(thEmail);

    // 전화번호 열
    const thPhone = document.createElement("th");
    thPhone.className = "phone-col";
    thPhone.textContent = "전화번호";
    trHead.appendChild(thPhone);

    // 부서 또는 메모 열
    const thInfo = document.createElement("th");
    thInfo.className = "info-col";
    thInfo.textContent = (tab === "shared") ? "부서" : "메모";
    trHead.appendChild(thInfo);

    thead.appendChild(trHead);
    table.appendChild(thead);

    // 테이블 바디 생성
    const tbody = document.createElement("tbody");
    if (results.length === 0) {
        const tr = document.createElement("tr");
        const td = document.createElement("td");
        td.colSpan = "5";
        td.textContent = "표시할 데이터가 없습니다.";
        tr.appendChild(td);
        tbody.appendChild(tr);
    } else {
        results.forEach(contact => {
            const tr = document.createElement("tr");
            if (tab === "personal") {
                tr.setAttribute("data-id", contact.id);
            }
            // 첫 번째 셀
            const tdCel = document.createElement("td");
            tdCel.className = "cel";
            if (tab === "personal") {
                tdCel.innerHTML = `<input type="checkbox" class="contact-checkbox">`;
            }
            tr.appendChild(tdCel);

            // 이름
            const tdName = document.createElement("td");
            tdName.className = "name-col";
            tdName.textContent = contact.name;
            tr.appendChild(tdName);

            // 이메일
            const tdEmail = document.createElement("td");
            const email = (tab === "shared") ? contact.internalEmail : (contact.email || '');
            tdEmail.innerHTML = `<a href="#" class="email-link" onclick="openMailComposePopup('${email}')">${email}</a>`;
            tr.appendChild(tdEmail);

            // 전화번호
            const tdPhone = document.createElement("td");
            tdPhone.textContent = contact.phone;
            tr.appendChild(tdPhone);

            // 부서 or 메모
            const tdInfo = document.createElement("td");
            tdInfo.className = "info-col";
            tdInfo.textContent = (tab === "shared") ? contact.depName : (contact.memo || '');
            tr.appendChild(tdInfo);

            // 행 클릭 시 모달 열기 (체크박스 클릭은 제외)
            tr.addEventListener("click", function(e) {
                if (e.target.classList.contains("contact-checkbox")) return;
                openDetailModal(contact, tab);
            });
            tbody.appendChild(tr);
        });
    }
    table.appendChild(tbody);

    // 드롭다운 토글 기능은 그대로 유지
    header.addEventListener("click", function() {
        if (table.style.display === "none") {
            table.style.display = "";
            arrow.textContent = "▼";
        } else {
            table.style.display = "none";
            arrow.textContent = "▲";
        }
    });

    section.appendChild(header);
    section.appendChild(table);
    return section;
}




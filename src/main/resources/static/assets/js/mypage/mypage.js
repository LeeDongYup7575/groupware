document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    let tab = urlParams.get('tab');

    if (!tab) {
        tab = 'info'; // 기본값 설정
        window.history.replaceState({}, '', `?tab=${tab}`); // URL 업데이트
    }

    loadTabContent(tab); // 탭에 맞는 내용 로드

});

function loadTabContent(tabName) {
    // 모든 sidebar-item에서 active 클래스 제거
    document.querySelectorAll('.mypage-sidebar-item').forEach(item => {
        item.classList.remove('active');
    });

    // 클릭한 요소에 active 클래스 추가
    document.getElementById(tabName).classList.add('active');


    // 탭 내용 가져오기
    fetch(`/api/mypage/${tabName}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('mainSection').innerHTML = generateContent(tabName, data);

            if (tabName === 'activities') {
                const urlParams = new URLSearchParams(window.location.search);
                let menu = urlParams.get('menu') || 'mypost'; // 기본값: 'mypost'
                loadMenuContent(menu); // menu 로드
            }

        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });

    // URL 업데이트
    window.history.pushState({}, '', `?tab=${tabName}`);
}

function loadMenuContent(menuName) {
    // 모든 sidebar-item에서 active 클래스 제거
    document.querySelectorAll('.menu-item').forEach(item => {
        item.classList.remove('active');
    });

    // 클릭한 요소에 active 클래스 추가
    document.getElementById(menuName).classList.add('active');

    // 탭 내용 가져오기
    fetch(`/api/mypage/activities/${menuName}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('tableContainer').innerHTML = generateContent(menuName, data);
            if (menuName === 'mypost') {
                setupPostPagination();
                setupPostDelete();
            }else if (menuName === 'mycomment') {
                setupCommentPagination();
                setupCommentDelete();
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });

    // URL 업데이트 (activities 탭 안에서 menu 변경)
    window.history.pushState({}, '', `?tab=activities&menu=${menuName}`);
}


function generateContent(contentName, data) {
    let content = '';
    if (contentName === 'info') {
        content = `
    <div class="title-container">
      <h1>내 정보</h1>
    </div>

    <div class="content-container">

      <div class="profile-container">
        <div class="profile-image">
            <img src=${data.profileImgUrl} alt="프로필 이미지">
            
            
            <input type="file" id="profile-input" style="display: none;" accept="image/*">
            <div class="profile-image-controls">
                <button type="button" class="profile-btn img-delete-btn" title="삭제" onclick="deleteProfileImage()">
                    <i class="fas fa-trash"></i>
                </button>
                <button type="button" class="profile-btn img-edit-btn" title="수정" onclick="openImageSelector()">
                    <i class="fas fa-camera"></i>
                </button>
            </div>
        </div>

        <div class="profile-info">
          <div class="info-row upper-info-row">
            <div class="info-label upper-info-label">이름</div>
            <div class="info-content upper-info-content" id="name">${data.name}</div>
          </div>
          <div class="info-row upper-info-row">
            <div class="info-label upper-info-label">사번</div>
            <div class="info-content upper-info-content" id="empNum">${data.empNum}</div>
          </div>
        </div>
      </div>

      <div class="info-container">
        <div class="info-row">
          <div class="info-label">입사일</div>
          <div class="info-content" id="hireDate">${data.hireDate}</div>
        </div>

        <div class="info-row">
          <div class="info-label">소속</div>
          <div class="info-content" id="departmentName">${data.departmentName}</div>
        </div>

        <div class="info-row">
          <div class="info-label">직급</div>
          <div class="info-content" id="positionTitle">${data.positionTitle}</div>
        </div>

        <div class="info-row">
          <div class="info-label">전화번호</div>
          <div class="info-content">
            <input type="text" id="phone" value="${data.phone}">
          </div>
        </div>

        <div class="info-row">
          <div class="info-label">이메일</div>
          <div class="info-content" id="internalEmail">${data.internalEmail}</div>
        </div>

        <div class="info-row">
          <div class="info-label">개인 이메일</div>
          <div class="info-content">
            <input type="email" id="email" value="${data.email}">
          </div>
        </div>

        <button class="submit-button" onclick="updateInfo()">저장</button>
      </div>
    </div>
`;

    } else if (contentName === 'activities') {
        content = `
    <div class="title-container">
      <h1>나의 활동</h1>
    </div>

    <div class="menu-container">
      <div class="menu-item active" id="mypost" onclick="loadMenuContent('mypost')">작성글</div>
      <div class="menu-item" id="mycomment" onclick="loadMenuContent('mycomment')">작성댓글</div>
    </div>
    
    <div class="table-container" id="tableContainer">

    </div class="table-container">      
`;

    } else if (contentName === 'security') {
        content = `
    <div class="title-container">
      <h1>보안</h1>
    </div>
    
    <div class="info-container">
        <div class="info-row">
          <div class="info-label">마지막 활동</div>
          <div class="info-content" id="lastLogin">${data.lastLogin}</div>
        </div>
        <div class="info-row">
          <div class="info-label">비밀번호 변경</div>
          <div class="info-content pw-change"><a href="/auth/change-password" target="_blank">변경하기</a></div>
        </div>
    </div>  
    
`;
    } else if (contentName === 'mypost') {
        let rows = '';

        data.forEach(post => {
            rows += `
        <tr>
          <td class="checkbox-col post-td"><input type="checkbox" data-post-id="${post.id}"></td>
          <td class="board-col post-td">${post.boardName}</td>
          <td class="title-col post-td">
            <a href="/board/post/${post.id}">${post.title}</a>
          </td>
          <td class="created-at-col post-td">${formatDate(post.createdAt)}</td>
          <td class="views-col post-td">${post.views}</td>
        </tr>
        `;
        });

        content = `
            <table class="post-table">
            <thead>
                <tr>
               <th class="checkbox-col post-th"></th>
                   <th class="board-col post-th">게시판</th>
                 <th class="title-col post-th">제목</th>
                <th class="created-at-col post-th">작성일</th>
                 <th class="views-col post-th">조회수</th>
               </tr>
             </thead>
            <tbody>
               ${rows}
             </tbody>
            </table>

            <div class="table-footer">
                <div class="footer-left">
                    <input type="checkbox" id="selectAll">
                    <label for="selectAll">전체선택</label>
                </div>    

                <div class="footer-center">
                    <div class="pagination"></div>
                </div>

                <div class="footer-right">
                    <button class="post-delete-btn delete-btn" id="postDeleteBtn">삭제</button>
                </div>
            </div>

        `;
    }else if (contentName === 'mycomment'){
        let rows = '';

        if (data.length === 0) {
            rows = `
            <tr>
                <td colspan="2" class="comment-td no-data">작성한 댓글이 없습니다.</td>
            </tr>
        `;
        } else {
            data.forEach(comment => {
                rows += `
            <tr>
                <td class="checkbox-col comment-td">
                    <input type="checkbox" data-comment-id="${comment.id}">
                </td>
                <td class="comment-info-col comment-td">
                    <a href="/board/post/${comment.postId}#commentSection">
                        <div class="comment-content">${comment.content}</div>
                        <div class="comment-date">${formatDateTime(comment.createdAt)}</div>
                        <div class="comment-post-title">${comment.postTitle}</div>
                    </a>
                </td>
            </tr>
            `;
            });
        }

        content = `
    <table class="comment-table">
        <thead>
            <tr>
                <th class="checkbox-col comment-th"></th>
                <th class="comment-info-col comment-th">댓글</th>
            </tr>
        </thead>
        <tbody>
            ${rows}
        </tbody>
    </table>

    <div class="table-footer">
        <div class="footer-left">
            <input type="checkbox" id="selectAllComments">
            <label for="selectAllComments">전체선택</label>
        </div>    

        <div class="footer-center">
            <div class="pagination"></div>
        </div>

        <div class="footer-right">
            <button class="comment-delete-btn delete-btn" id="commentDeleteBtn">삭제</button>
        </div>
    </div>
    `;
    }
    return content;
}

// 날짜 변환
function formatDate(dateStr) {
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
}

// 날짜 시간 변환
function formatDateTime(dateStr) {
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}.${month}.${day} ${hours}:${minutes}`;
}

// 카메라 아이콘 클릭 시 파일 선택 창 열기
function openImageSelector() {
    document.getElementById('profile-input').click();
}

// 프로필 이미지 삭제 플래그
let isImageDeleted = false;

// 파일 선택 시 이미지 미리보기 및 업로드 준비
document.addEventListener('change', function(event) {
    if (event.target.id === 'profile-input') {
        const file = event.target.files[0];
        if (file) {
            // 파일 유효성 검사 (이미지 파일인지)
            if (!file.type.startsWith('image/')) {
                alert('이미지 파일만 선택할 수 있습니다.');
                return;
            }

            // 파일 크기 제한 (예: 5MB)
            if (file.size > 5 * 1024 * 1024) {
                alert('파일 크기는 5MB 이하여야 합니다.');
                return;
            }

            isImageDeleted = false;

            // 이미지 미리보기
            const reader = new FileReader();
            reader.onload = function(e) {
                document.querySelector('.profile-image img').src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    }
});

// 프로필 이미지 삭제 함수
function deleteProfileImage() {
    document.querySelector('.profile-image img').src = '/assets/images/default-profile.png'; // 기본 이미지로 변경
    document.getElementById('profile-input').value = ''; // 파일 선택 초기화
    isImageDeleted = true;
    console.log(isImageDeleted);
}

// 내 정보 업데이트
function updateInfo() {
    const phoneInput = document.getElementById("phone");
    const emailInput = document.getElementById("email");
    const profileInput = document.getElementById('profile-input');

    // 전화번호 이메일 유효성 검사
    const phonePattern = /^010-\d{4}-\d{4}$/;
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if (!phonePattern.test(phoneInput.value)) {
        alert('전화번호는 010-XXXX-XXXX 형식으로 입력해주세요.');
        phoneInput.focus();
        return;
    }

    if (!emailPattern.test(emailInput.value)) {
        alert('유효한 이메일 주소를 입력해주세요.');
        emailInput.focus();
        return;
    }

    // FormData 생성
    const formData = new FormData();
    formData.append('phone', phoneInput.value);
    formData.append('email', emailInput.value);

    if (profileInput.files.length > 0) {
        formData.append('profileImage', profileInput.files[0]);
    }

    formData.append("isImageDeleted", isImageDeleted);

    // update 처리
    fetch('/api/mypage/update', {
        method: 'PATCH',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 응답 오류');
            }
            return response.json();
        })
        .then(data => {
            alert('정보가 성공적으로 업데이트되었습니다.');
        })
        .catch(error => {
            alert('정보 업데이트 중 오류가 발생했습니다.');
            console.error(error); // 오류 로그 출력
        });
}


// 게시글 페이지네이션
function setupPostPagination() {
    const recordsPerPage = 5;
    const naviCountPerPage = 5;

    const table = document.querySelector(".post-table");
    if (!table) return;

    const rows = Array.from(table.querySelectorAll("tbody tr"));
    const totalRecords = rows.length;
    const totalPages = Math.ceil(totalRecords / recordsPerPage);

    const footer = document.querySelector(".table-footer");
    const pagination = footer.querySelector(".pagination");
    const selectAllCheckbox = document.getElementById("selectAll");

    let currentPage = 1;

    function syncCheckboxEvents() {
        const visibleRows = rows.filter(row => row.style.display !== "none");
        visibleRows.forEach(row => {
            const cb = row.querySelector('input[type="checkbox"]');
            if (cb) {
                cb.addEventListener("change", () => {
                    const allChecked = visibleRows.every(r => {
                        const cb = r.querySelector('input[type="checkbox"]');
                        return cb && cb.checked;
                    });
                    selectAllCheckbox.checked = allChecked;
                });
            }
        });
    }

    function showPage(page) {
        const start = (page - 1) * recordsPerPage;
        const end = start + recordsPerPage;

        rows.forEach((row, index) => {
            row.style.display = index >= start && index < end ? "" : "none";
            const cb = row.querySelector('input[type="checkbox"]');
            if (cb) cb.checked = false;
        });

        selectAllCheckbox.checked = false;
        syncCheckboxEvents();
    }

    pagination.innerHTML = "";

    if (totalRecords > recordsPerPage) {
        function renderPagination(page) {
            currentPage = page;
            pagination.innerHTML = "";

            const groupIndex = Math.floor((currentPage - 1) / naviCountPerPage);
            const startPage = groupIndex * naviCountPerPage + 1;
            const endPage = Math.min(startPage + naviCountPerPage - 1, totalPages);

            if (startPage > 1)
                pagination.appendChild(createPageLink(startPage - 1, "prev"));

            for (let i = startPage; i <= endPage; i++) {
                pagination.appendChild(createPageLink(i, i.toString(), i === currentPage));
            }

            if (endPage < totalPages)
                pagination.appendChild(createPageLink(endPage + 1, "next"));
        }

        function createPageLink(page, label, isActive = false) {
            const a = document.createElement("a");
            a.href = "#";
            a.className = "page-link";
            a.dataset.page = page;
            a.textContent = label === "prev" ? "<" : label === "next" ? ">" : label;
            if (isActive) a.classList.add("active");
            return a;
        }

        pagination.addEventListener("click", function (e) {
            const link = e.target.closest("a.page-link");
            if (!link) return;
            e.preventDefault();
            const page = parseInt(link.dataset.page);
            renderPagination(page);
            showPage(page);
            window.scrollTo({ top: table.offsetTop - 100, behavior: "smooth" });
        });

        renderPagination(1);
    }

    showPage(1); // 무조건 호출해서 이벤트 연결
    selectAllCheckbox.addEventListener("change", function () {
        const visibleRows = rows.filter(row => row.style.display !== "none");
        visibleRows.forEach(row => {
            const cb = row.querySelector('input[type="checkbox"]');
            if (cb) cb.checked = selectAllCheckbox.checked;
        });
    });
}


// 게시글 삭제
function setupPostDelete() {
    const deleteBtn = document.getElementById('postDeleteBtn');
    if (!deleteBtn) return;

    deleteBtn.addEventListener('click', () => {
        const visibleRows = Array.from(document.querySelectorAll('.post-table tbody tr'))
            .filter(row => row.style.display !== 'none');

        const checkedIds = visibleRows
            .map(row => row.querySelector('input[type="checkbox"]'))
            .filter(cb => cb && cb.checked)
            .map(cb => parseInt(cb.dataset.postId));

        if (checkedIds.length === 0) {
            alert('삭제할 게시글을 선택해주세요.');
            return;
        }

        if (!confirm('선택한 게시글을 삭제하시겠습니까?')) return;

        fetch('/api/posts/delete', {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(checkedIds)
        })
            .then(res => {
                if (!res.ok) throw new Error('삭제 실패');
                loadMenuContent('mypost');
            })
            .catch(() => alert('오류 발생'));
    });
}

// 댓글 페이지네이션
function setupCommentPagination() {
    const recordsPerPage = 5;
    const naviCountPerPage = 5;

    const table = document.querySelector(".comment-table");
    if (!table) return;

    const rows = Array.from(table.querySelectorAll("tbody tr"));
    const totalRecords = rows.length;
    const totalPages = Math.ceil(totalRecords / recordsPerPage);

    const footer = document.querySelector(".table-footer");
    const pagination = footer.querySelector(".pagination");
    const selectAllCheckbox = footer.querySelector("#selectAllComments");

    let currentPage = 1;

    function syncCheckboxEvents() {
        const visibleRows = rows.filter(row => row.style.display !== "none");
        visibleRows.forEach(row => {
            const cb = row.querySelector('input[type="checkbox"]');
            if (cb) {
                cb.addEventListener("change", () => {
                    const allChecked = visibleRows.every(r => {
                        const cb = r.querySelector('input[type="checkbox"]');
                        return cb && cb.checked;
                    });
                    selectAllCheckbox.checked = allChecked;
                });
            }
        });
    }

    function showPage(page) {
        const start = (page - 1) * recordsPerPage;
        const end = start + recordsPerPage;

        rows.forEach((row, index) => {
            row.style.display = index >= start && index < end ? "" : "none";
            const cb = row.querySelector('input[type="checkbox"]');
            if (cb) cb.checked = false;
        });

        selectAllCheckbox.checked = false;
        syncCheckboxEvents(); // 무조건 체크박스 이벤트 연결
    }

    pagination.innerHTML = "";

    if (totalRecords > recordsPerPage) {
        function renderPagination(page) {
            currentPage = page;
            pagination.innerHTML = "";

            const groupIndex = Math.floor((currentPage - 1) / naviCountPerPage);
            const startPage = groupIndex * naviCountPerPage + 1;
            const endPage = Math.min(startPage + naviCountPerPage - 1, totalPages);

            if (startPage > 1)
                pagination.appendChild(createPageLink(startPage - 1, "prev"));

            for (let i = startPage; i <= endPage; i++) {
                pagination.appendChild(createPageLink(i, i.toString(), i === currentPage));
            }

            if (endPage < totalPages)
                pagination.appendChild(createPageLink(endPage + 1, "next"));
        }

        function createPageLink(page, label, isActive = false) {
            const a = document.createElement("a");
            a.href = "#";
            a.className = "page-link";
            a.dataset.page = page;
            a.textContent = label === "prev" ? "<" : label === "next" ? ">" : label;
            if (isActive) a.classList.add("active");
            return a;
        }

        pagination.addEventListener("click", function (e) {
            const link = e.target.closest("a.page-link");
            if (!link) return;
            e.preventDefault();
            const page = parseInt(link.dataset.page);
            renderPagination(page);
            showPage(page);
            window.scrollTo({ top: table.offsetTop - 100, behavior: "smooth" });
        });

        renderPagination(1);
    }

    showPage(1); // 데이터 수 상관없이 항상 실행
    selectAllCheckbox.addEventListener("change", function () {
        const visibleRows = rows.filter(row => row.style.display !== "none");
        visibleRows.forEach(row => {
            const checkbox = row.querySelector('input[type="checkbox"]');
            if (checkbox) checkbox.checked = selectAllCheckbox.checked;
        });
    });
}


// 댓글 삭제
function setupCommentDelete() {
    const deleteBtn = document.getElementById('commentDeleteBtn');
    if (!deleteBtn) return;

    deleteBtn.addEventListener('click', () => {
        const visibleRows = Array.from(document.querySelectorAll('.comment-table tbody tr'))
            .filter(row => row.style.display !== 'none');

        const checkedIds = visibleRows
            .map(row => row.querySelector('input[type="checkbox"]'))
            .filter(cb => cb && cb.checked)
            .map(cb => parseInt(cb.dataset.commentId));

        if (checkedIds.length === 0) {
            alert('삭제할 댓글을 선택해주세요.');
            return;
        }

        if (!confirm('선택한 댓글을 삭제하시겠습니까?')) return;

        fetch('/api/comments/delete', {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(checkedIds)
        })
            .then(res => {
                if (!res.ok) throw new Error('삭제 실패');
                loadMenuContent('mycomment');
            })
            .catch(() => alert('오류 발생'));
    });
}

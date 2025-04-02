// HTML 파일의 </body> 태그 바로 앞에 다음 스크립트를 추가하세요
document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    let tab = urlParams.get('tab');

    if (!tab) {
        tab = 'info'; // 기본값 설정
        window.history.replaceState({}, '', `?tab=${tab}`); // URL 업데이트
    }

    loadTabContent(tab); // 탭에 맞는 내용 로드



    // // 필요한 요소들 선택
    // const phoneInput = document.getElementById("phone");
    // const emailInput = document.getElementById("email");
    // const submitButton = document.getElementById("submit-button");
    //
    // // 전화번호 입력 이벤트 처리 (숫자와 하이픈만 허용)
    // phoneInput.addEventListener('input', function() {
    //     // 숫자와 하이픈만 남기고 모두 제거
    //     this.value = this.value.replace(/[^\d-]/g, '');
    //
    //     // 전화번호 형식 검증 (예: 010-1234-5678)
    //     if (!/^[\d-]+$/.test(this.value)) {
    //         this.setCustomValidity('전화번호는 숫자와 하이픈(-)만 포함해야 합니다.');
    //     } else {
    //         this.setCustomValidity('');
    //     }
    // });
    //
    // // 이메일 입력 이벤트 처리
    // emailInput.addEventListener('input', function() {
    //     // 이메일 형식 검증
    //     const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    //
    //     if (!emailPattern.test(this.value)) {
    //         this.setCustomValidity('유효한 이메일 주소를 입력해주세요.');
    //     } else {
    //         this.setCustomValidity('');
    //     }
    // });
    //
    // // 폼 제출 처리
    // submitButton.addEventListener('click', function() {
    //     // 유효성 검사
    //     const isPhoneValid = phoneInput.validity.valid;
    //     const isEmailValid = personalEmailInput.validity.valid;
    //
    //     if (!isPhoneValid) {
    //         alert('유효한 전화번호를 입력해주세요.');
    //         phoneInput.focus();
    //         return;
    //     }
    //
    //     if (!isEmailValid) {
    //         alert('유효한 이메일 주소를 입력해주세요.');
    //         personalEmailInput.focus();
    //         return;
    //     }
    //
    //     // 변경된 데이터 준비
    //     const updatedData = {
    //         phone: phoneInput.value,
    //         email: personalEmailInput.value
    //     };
    //
    //     // Fetch API를 사용하여 서버에 데이터 전송
    //     fetch('/api/employee/update', {
    //         method: 'POST',
    //         headers: {
    //             'Content-Type': 'application/json',
    //         },
    //         body: JSON.stringify(updatedData)
    //     })
    //         .then(response => {
    //             if (!response.ok) {
    //                 throw new Error('서버 응답 오류');
    //             }
    //             return response.json();
    //         })
    //         .then(data => {
    //             alert('정보가 성공적으로 업데이트되었습니다.');
    //             console.log('업데이트 성공:', data);
    //         })
    //         .catch(error => {
    //             alert('정보 업데이트 중 오류가 발생했습니다.');
    //             console.error('업데이트 오류:', error);
    //         });
    // });
});

function loadTabContent(tabName) {
    // 모든 sidebar-item에서 active 클래스 제거
    document.querySelectorAll('.sidebar-item').forEach(item => {
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

function generateContent(contentName, data) {
    let content = '';
    if (contentName === 'info') {
        content = `
    <div class="title-container">
      <h1>내 정보</h1>
    </div>

    <div class="content-container">

      <div class="profile-container">
        <div class="profile-image"></div>

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
          <div class="info-content" id="hireDate">${data.empNum}</div>
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

        <button class="submit-button">저장</button>
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
      <div class="menu-item" id="starredpost" onclick="loadMenuContent('starredpost')">중요게시글</div>
    </div>
    
    <div class="table-container" id="tableContainer">

    </div class="table-container">      
`;

    } else if (contentName === 'security') {
        content = `<h2>보안설정</h2>`;
    } else if (contentName === 'mypost') {
        content = `
    <table>
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
      <tr>
        <td class="checkbox-col post-td"><input type="checkbox"></td>
        <td class="board-col post-td">자유게시판</td>
        <td class="title-col post-td">제목입니다!!!!!</td>
        <td class="created-at-col post-td">2025.03.28</td>
        <td class="views-col post-td">26</td>
      </tr>
      <tr>
        <td class="checkbox-col post-td"><input type="checkbox"></td>
        <td class="board-col post-td">자유게시판</td>
        <td class="title-col post-td">제목입니다!!!!!</td>
        <td class="created-at-col post-td">2025.03.28</td>
        <td class="views-col post-td">26</td>
      </tr>
      <tr>
        <td class="checkbox-col post-td"><input type="checkbox"></td>
        <td class="board-col post-td">자유게시판</td>
        <td class="title-col post-td">제목입니다!!!!!</td>
        <td class="created-at-col post-td">2025.03.28</td>
        <td class="views-col post-td">26</td>
      </tr>
      <tr>
        <td class="checkbox-col post-td"><input type="checkbox"></td>
        <td class="board-col post-td">자유게시판</td>
        <td class="title-col post-td">제목입니다!!!!!</td>
        <td class="created-at-col post-td">2025.03.28</td>
        <td class="views-col post-td">26</td>
      </tr>
      <tr>
        <td class="checkbox-col post-td"><input type="checkbox"></td>
        <td class="board-col post-td">자유게시판</td>
        <td class="title-col post-td">제목입니다!!!!!</td>
        <td class="created-at-col post-td">2025.03.28</td>
        <td class="views-col post-td">26</td>
      </tr>

      </tbody>
    </table>

    <div style="margin-top: 20px;">
      <input type="checkbox" id="selectAll">
      <label for="selectAll">전체선택</label>
      <button class="delete-btn">삭제</button>
    </div>        
        `;
    }else if (contentName === 'mycomment'){
        content = `<h2>작성댓글</h2>`;
    }else if (contentName === 'starredpost'){
        content = `<h2>중요게시글</h2>`;
    }
    return content;
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
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });

    // URL 업데이트 (activities 탭 안에서 menu 변경)
    window.history.pushState({}, '', `?tab=activities&menu=${menuName}`);
}

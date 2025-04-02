// HTML 파일의 </body> 태그 바로 앞에 다음 스크립트를 추가하세요
document.addEventListener('DOMContentLoaded', function() {
    fetchUserInfo();
    // 필요한 요소들 선택
    const phoneInput = document.querySelector('.info-row:nth-of-type(4) input');
    const personalEmailInput = document.querySelector('.info-row:nth-of-type(6) input');
    const submitButton = document.querySelector('.submit-button');

    // 전화번호 입력 이벤트 처리 (숫자와 하이픈만 허용)
    phoneInput.addEventListener('input', function() {
        // 숫자와 하이픈만 남기고 모두 제거
        this.value = this.value.replace(/[^\d-]/g, '');

        // 전화번호 형식 검증 (예: 010-1234-5678)
        if (!/^[\d-]+$/.test(this.value)) {
            this.setCustomValidity('전화번호는 숫자와 하이픈(-)만 포함해야 합니다.');
        } else {
            this.setCustomValidity('');
        }
    });

    // 이메일 입력 이벤트 처리
    personalEmailInput.addEventListener('input', function() {
        // 이메일 형식 검증
        const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if (!emailPattern.test(this.value)) {
            this.setCustomValidity('유효한 이메일 주소를 입력해주세요.');
        } else {
            this.setCustomValidity('');
        }
    });

    // 폼 제출 처리
    submitButton.addEventListener('click', function() {
        // 유효성 검사
        const isPhoneValid = phoneInput.validity.valid;
        const isEmailValid = personalEmailInput.validity.valid;

        if (!isPhoneValid) {
            alert('유효한 전화번호를 입력해주세요.');
            phoneInput.focus();
            return;
        }

        if (!isEmailValid) {
            alert('유효한 이메일 주소를 입력해주세요.');
            personalEmailInput.focus();
            return;
        }

        // 변경된 데이터 준비
        const updatedData = {
            phone: phoneInput.value,
            email: personalEmailInput.value
        };

        // Fetch API를 사용하여 서버에 데이터 전송
        fetch('/api/employee/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatedData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('서버 응답 오류');
                }
                return response.json();
            })
            .then(data => {
                alert('정보가 성공적으로 업데이트되었습니다.');
                console.log('업데이트 성공:', data);
            })
            .catch(error => {
                alert('정보 업데이트 중 오류가 발생했습니다.');
                console.error('업데이트 오류:', error);
            });
    });
});

function fetchUserInfo() {
    fetch('/api/mypage/info')
        .then(response => response.json())
        .then(employee => {
            document.getElementById('name').textContent = employee.name;
            document.getElementById('empNum').textContent = employee.empNum;
            document.getElementById('hireDate').textContent = employee.hireDate;
            document.getElementById('departmentName').textContent = employee.departmentName;
            document.getElementById('positionTitle').textContent = employee.positionTitle;
            document.getElementById('phone').value = employee.phone;
            document.getElementById('internalEmail').textContent = employee.internalEmail;
            document.getElementById('email').value = employee.email;
        })
        .catch(error => {
            console.error('사용자 정보를 가져오는 중 오류 발생:', error);
        });
}
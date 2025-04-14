/**
 * 프로젝트 등록 관련 JavaScript 기능
 */
$(document).ready(function() {
    // 초기 날짜 설정 (프로젝트 시작일은 오늘, 종료일은 1개월 후)
    initializeDates();

    // 프로젝트 멤버 추가/제거 기능
    setupMemberSelection();

    // 프로젝트 등록 폼 제출
    $('#save-project-btn').on('click', submitProjectForm);

    // 취소 버튼 처리
    $('#cancel-project-btn').on('click', function() {
        if (confirm('작성 중인 내용이 저장되지 않습니다. 취소하시겠습니까?')) {
            window.location.href = '/workmanagement';
        }
    });
});

/**
 * 날짜 초기화 함수
 */
function initializeDates() {
    const today = new Date();
    const nextMonth = new Date(today);
    nextMonth.setMonth(nextMonth.getMonth() + 1);

    $('#project-start-date').val(formatDate(today));
    $('#project-end-date').val(formatDate(nextMonth));
}

/**
 * 프로젝트 멤버 선택 기능 설정
 */
function setupMemberSelection() {
    // 멤버 추가 버튼
    $('#add-member-btn').on('click', function() {
        $('#available-members option:selected').each(function() {
            // 중복 제거
            if ($('#selected-members option[value="' + $(this).val() + '"]').length === 0) {
                const newOption = $(this).clone().removeAttr('selected');
                $('#selected-members').append(newOption);
            }
        });
    });

    // 멤버 제거 버튼
    $('#remove-member-btn').on('click', function() {
        $('#selected-members option:selected').each(function() {
            // 매니저(현재 사용자)는 제거 불가
            if (!$(this).text().includes('[매니저]')) {
                $(this).remove();
            }
        });
    });

    // 멤버 검색 기능
    $('#search-members-btn').on('click', function() {
        const searchText = $('#project-members-search').val().toLowerCase();

        if (searchText.trim() === '') {
            $('#available-members option').show();
        } else {
            $('#available-members option').each(function() {
                if ($(this).text().toLowerCase().includes(searchText)) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
    });

    // 검색창 엔터 키 처리
    $('#project-members-search').on('keyup', function(e) {
        if (e.key === 'Enter') {
            $('#search-members-btn').click();
        }
    });
}

/**
 * 프로젝트 등록 폼 제출 처리
 */
function submitProjectForm() {
    // 폼 유효성 검사
    if (!validateProjectForm()) {
        return;
    }

    // 프로젝트 데이터 수집
    const projectData = {
        name: $('#project-name').val(),
        description: $('#project-description').val(),
        status: $('#project-status').val(),
        startDate: $('#project-start-date').val(),
        endDate: $('#project-end-date').val(),
        depId: $('#project-department').val() || null,
        managerEmpNum: $('#current-user-emp-num').val(), // 현재 로그인한 사용자
        progress: 0, // 신규 프로젝트는 진행률 0으로 시작
        isPublic: true // 기본값 (필요시 변경)
    };

    // 프로젝트 멤버 수집
    const members = [];
    $('#selected-members option').each(function() {
        members.push({
            empNum: $(this).val(),
            projectId: null, // 서버에서 생성된 프로젝트 ID가 할당됨
            role: $(this).text().includes('[매니저]') ? '매니저' : '팀원'
        });
    });

    console.log('프로젝트 데이터:', projectData);
    console.log('멤버 데이터:', members);

    // API 호출로 프로젝트 생성
    $.ajax({
        url: '/api/projects',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(projectData),
        success: function(response) {
            console.log('프로젝트 생성 응답:', response);
            // 프로젝트 생성 성공
            addProjectMembers(response.id, members);
        },
        error: function(xhr) {
            console.error('프로젝트 생성 오류:', xhr);
            handleError(xhr, '프로젝트 생성에 실패했습니다.');
        }
    });
}

/**
 * 프로젝트 멤버 추가 처리 (프로젝트 생성 후)
 */
function addProjectMembers(projectId, membersList) {
    console.log('프로젝트 ID:', projectId);
    console.log('추가할 멤버:', membersList);

    // 기존 멤버에 현재 사용자(매니저)가 이미 포함되어 있으므로 추가 호출 필요 없음
    if (membersList.length <= 1) {
        showSuccess('프로젝트가 성공적으로 생성되었습니다.');
        return;
    }

    // 멤버 추가 요청 준비
    const memberRequests = [];

    // 현재 사용자를 제외한 모든 멤버에 대해 요청 생성
    membersList.forEach(member => {
        if (member.role !== '매니저') {
            memberRequests.push(
                $.ajax({
                    url: `/api/projects/${projectId}/members`,
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        empNum: member.empNum,
                        role: member.role
                    })
                })
            );
        }
    });

    // 모든 멤버 추가 요청 실행
    if (memberRequests.length > 0) {
        Promise.all(memberRequests)
            .then(() => {
                console.log('모든 멤버 추가 완료');
                showSuccess('프로젝트가 성공적으로 생성되었습니다.');
            })
            .catch(error => {
                console.error('프로젝트 멤버 추가 실패:', error);
                showWarning('프로젝트는 생성되었으나 일부 멤버 추가에 실패했습니다.');
            });
    } else {
        showSuccess('프로젝트가 성공적으로 생성되었습니다.');
    }
}

/**
 * 프로젝트 폼 유효성 검사
 */
function validateProjectForm() {
    // 필수 입력 필드 확인
    const requiredFields = [
        { id: 'project-name', message: '프로젝트 이름을 입력하세요.' },
        { id: 'project-description', message: '프로젝트 설명을 입력하세요.' },
        { id: 'project-start-date', message: '시작일을 선택하세요.' },
        { id: 'project-end-date', message: '종료 예정일을 선택하세요.' }
    ];

    // 필수 필드 검사
    for (const field of requiredFields) {
        const value = $(`#${field.id}`).val();
        if (!value || value.trim() === '') {
            showError(field.message);
            $(`#${field.id}`).focus();
            return false;
        }
    }

    // 날짜 유효성 검사
    const startDate = new Date($('#project-start-date').val());
    const endDate = new Date($('#project-end-date').val());

    if (endDate < startDate) {
        showError('종료일은 시작일보다 이후여야 합니다.');
        $('#project-end-date').focus();
        return false;
    }

    return true;
}

/**
 * 날짜 포맷팅 (YYYY-MM-DD)
 */
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

/**
 * 성공 메시지 표시 및 리다이렉트
 */
function showSuccess(message) {
    alert(message);
    window.location.href = '/workmanagement';
}

/**
 * 경고 메시지 표시
 */
function showWarning(message) {
    alert(message);
    window.location.href = '/workmanagement';
}

/**
 * 오류 메시지 표시
 */
function showError(message) {
    alert(message);
}

/**
 * AJAX 오류 처리
 */
function handleError(xhr, defaultMessage) {
    let errorMessage = defaultMessage;

    // 서버 응답에서 오류 메시지 추출 시도
    if (xhr.responseJSON && xhr.responseJSON.message) {
        errorMessage = xhr.responseJSON.message;
    } else if (xhr.responseText) {
        try {
            const response = JSON.parse(xhr.responseText);
            if (response.message) {
                errorMessage = response.message;
            }
        } catch (e) {
            // JSON 파싱 실패시 기본 메시지 사용
        }
    }

    showError(errorMessage);
}
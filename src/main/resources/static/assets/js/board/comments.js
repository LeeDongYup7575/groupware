// 댓글 작성 (일반 댓글 및 대댓글)
function submitComment(parentId) {
    const contentElement = parentId ?
        document.getElementById('reply-textarea-' + parentId) :
        document.getElementById('comment-content');
    const content = contentElement.value.trim();
    const token = localStorage.getItem('accessToken');

    if (!token) {
        alert('로그인이 필요합니다.');
        window.location.href = '/auth/login';
        return;
    }

    if (!content) {
        alert(parentId ? '답글 내용을 입력해주세요.' : '댓글 내용을 입력해주세요.');
        return;
    }

    // 현재 로그인한 사용자 ID(empId) 가져옴
    fetch('/api/posts/current-user', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('로그인 정보를 가져올 수 없습니다.');
            }
            return response.json();
        })
        .then(user => {
            const empId = user.id;

            const commentDTO = {
                postId: postId,  // 전역 변수로 설정된 postId 사용
                empId: empId,
                content: content,
                parentId: parentId // 부모 댓글 ID (일반 댓글은 null, 대댓글은 부모 댓글의 ID)
            };

            console.log('댓글 데이터:', commentDTO);  // 디버깅용

            // 댓글 추가 요청 (POST)
            return fetch('/api/comments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(commentDTO)
            });
        })
        .then(response => {
            console.log('응답 상태:', response.status);  // 디버깅용

            if (!response.ok) {
                throw new Error('댓글 등록에 실패했습니다.');
            }
            return response.json();
        })
        .then(data => {
            location.reload(); // 페이지 새로고침으로 댓글 목록 업데이트
        })
        .catch(error => {
            alert(error.message || (parentId ? '답글 등록에 실패했습니다.' : '댓글 등록에 실패했습니다.'));
        });
}

// 대댓글 작성 함수
function submitReply(parentId) {
    submitComment(parentId);
}

// 대댓글 폼 표시
function showReplyForm(commentId) {
    // 모든 대댓글 폼 숨기기 (다른 폼이 열려있는 경우 닫기)
    const allReplyForms = document.querySelectorAll('.reply-form-container');
    allReplyForms.forEach(form => {
        form.style.display = 'none';
    });

    // 선택한 댓글의 대댓글 폼 표시
    const replyForm = document.getElementById('reply-form-container-' + commentId);
    if (replyForm) {
        replyForm.style.display = 'block';

        // 대댓글 텍스트 영역에 포커스
        const textarea = document.getElementById('reply-textarea-' + commentId);
        if (textarea) {
            textarea.focus();
        }
    }
}

// 대댓글 폼 닫기
function cancelReply(commentId) {
    const replyForm = document.getElementById('reply-form-container-' + commentId);
    if (replyForm) {
        replyForm.style.display = 'none';

        // 텍스트 영역 초기화
        const textarea = document.getElementById('reply-textarea-' + commentId);
        if (textarea) {
            textarea.value = '';
        }
    }
}

// 댓글 삭제
function deleteComment(commentId) {
    if (!confirm('정말 삭제하시겠습니까?')) {
        return;
    }

    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('로그인이 필요합니다.');
        window.location.href = '/auth/login';
        return;
    }

    // AJAX 요청으로 댓글 삭제
    fetch(`/api/comments/${commentId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('댓글 삭제에 실패했습니다.');
            }
            return response.json();
        })
        .then(data => {
            if (data.deleted) {
                alert('댓글이 삭제되었습니다.');
                location.reload(); // 페이지 새로고침
            } else {
                alert('댓글 삭제에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('댓글 삭제 오류:', error);
            alert(error.message || '댓글 삭제 중 오류가 발생했습니다.');
        });
}

// 댓글 수정 폼 표시
function showEditForm(commentId) {
    // 댓글 내용 영역 숨기기
    document.getElementById('comment-content-' + commentId).style.display = 'none';
    // 수정 폼 표시
    document.getElementById('comment-edit-container-' + commentId).style.display = 'block';
}

// 댓글 수정 취소
function cancelEdit(commentId) {
    // 수정 폼 숨기기
    document.getElementById('comment-edit-container-' + commentId).style.display = 'none';
    // 댓글 내용 영역 다시 표시
    document.getElementById('comment-content-' + commentId).style.display = 'block';
}

// 댓글 수정 저장
function updateComment(commentId) {
    // 수정된 내용 가져오기
    const editedContent = document.getElementById('comment-edit-textarea-' + commentId).value;

    // 내용이 비어있는지 확인
    if (!editedContent.trim()) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }

    // 토큰 가져오기
    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('로그인이 필요합니다.');
        window.location.href = '/auth/login';
        return;
    }

    // 데이터 구성
    const updateData = {
        content: editedContent
    };

    // AJAX로 댓글 수정 요청
    fetch('/api/comments/' + commentId, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(updateData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('댓글 수정에 실패했습니다.');
            }
            return response.json();
        })
        .then(data => {
            // 서버에서 업데이트된 데이터를 반영
            document.getElementById('comment-content-' + commentId).textContent = data.content;

            // 수정 폼 숨기고 댓글 내용 다시 표시
            cancelEdit(commentId);

            // 성공 메시지
            alert('댓글이 수정되었습니다.');
        })
        .catch(error => {
            alert(error.message || '댓글 수정 중 오류가 발생했습니다.');
        });
}
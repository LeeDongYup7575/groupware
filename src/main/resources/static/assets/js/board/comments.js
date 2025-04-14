// 댓글 작성
function submitComment() {
    const content = document.getElementById('comment-content').value.trim();
    const token = localStorage.getItem('accessToken');

    if (!token) {
        alert('로그인이 필요합니다.');
        window.location.href = '/auth/login';
        return;
    }

    if (!content) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }
    /// api/posts/current-user 요청으로 현재 로그인한 사용자 ID(empId) 가져옴
    fetch('/api/posts/current-user', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token
        }
    })
        .then(response => response.json())
        .then(user => {
            const empId = user.id;

            const commentDTO = {
                postId: postId,
                empId: empId,
                content: content,
                parentId: null // 대댓글 아니면 null
            };

            /// api/comments로 댓글 추가 요청 (POST)
            return fetch('/api/comments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(commentDTO)
            });
        })
        .then(response => response.json())
        .then(data => {
            alert('댓글이 등록되었습니다.');
            location.reload(); // 또는 DOM에 직접 추가도 가능
        })
        .catch(error => {
            console.error('댓글 추가 오류:', error);
            alert('댓글 등록에 실패했습니다.');
        });
}


// 댓글 삭제 기능
function deleteComment(commentId) {
    if (!confirm('정말 삭제하시겠습니까?')) {
        return false;
    }

    // AJAX 요청으로 댓글 삭제
    fetch(`/api/comments/${commentId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.deleted) {
                // 성공적으로 삭제됨
                alert('댓글이 삭제되었습니다.');
                // 댓글 요소 DOM에서 제거
                const commentElement = document.querySelector(`[data-comment-id="${commentId}"]`).closest('.comment-item');
                if (commentElement) {
                    commentElement.remove();
                } else {
                    // DOM 요소를 찾지 못했다면 페이지 새로고침
                    location.reload();
                }
            } else {
                alert('댓글 삭제에 실패했습니다.');
            }
        })
        .catch(error => {
            alert('댓글 삭제 중 오류가 발생했습니다.');
        });

    return false; // 이벤트 기본 동작 중지
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

    // CommentsDTO 형식에 맞추어 데이터 구성
    const commentsDTO = {
        id: parseInt(commentId),
        content: editedContent
        // 필요한 다른 필드가 있다면 추가
    };

    console.log('댓글 수정 요청 데이터:', commentsDTO);

    // AJAX로 댓글 수정 요청
    fetch('/api/comments/' + commentId, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(commentsDTO)
    })
        .then(response => {
            console.log('서버 응답:', response.status, response.statusText);
            if (!response.ok) {
                console.error('응답 텍스트:', text);
                throw new Error('댓글 수정 실패');
            }
            return response.json();
        })
        .then(data => {
            console.log('서버 응답 데이터:', data);

            // 서버에서 업데이트된 데이터를 반환하면 그걸 사용
            document.getElementById('comment-content-' + commentId).textContent = data.content || editedContent;

            // 수정 폼 숨기고 댓글 내용 다시 표시
            cancelEdit(commentId);

            // 성공 메시지
            alert('댓글이 수정되었습니다.');
        })
        .catch(error => {
            console.error('댓글 수정 오류:', error);
            alert('댓글 수정 중 오류가 발생했습니다.');
        });
}
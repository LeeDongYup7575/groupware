// 로드, 추가, 수정, 삭제하는 기능을 처리하는 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const postId = document.getElementById('post-id').value;
    const currentUser = {
        id: localStorage.getItem('empNum'),
        name: localStorage.getItem('name')
    };

    // 댓글 로드
    loadComments();

    // 댓글 폼 제출 이벤트
    document.getElementById('comment-form').addEventListener('submit', function(e) {
        e.preventDefault();

        const content = document.getElementById('comment-content').value.trim();
        if (!content) {
            alert('댓글 내용을 입력해주세요.');
            return;
        }

        addComment(content);
    });

    // 댓글 목록 불러오기
    function loadComments() {
        // 로컬 스토리지에서 저장된 accessToken을 가져옴
        const token = localStorage.getItem('accessToken');

        // 서버에 GET 요청을 보내어 댓글 목록을 가져옴
        fetch(`/api/comments/post/${postId}`, {
            headers: {
                // Authorization 헤더에 Bearer 토큰을 추가하여 인증 정보 전달
                'Authorization': `Bearer ${token}`
            }
        })
            // 응답이 성공적으로 왔을 경우, JSON 형식으로 댓글 데이터를 파싱
            .then(response => response.json())
            .then(comments => {
                // 파싱된 댓글 데이터를 displayComments 함수에 전달하여 화면에 표시
                displayComments(comments);
            })
            // 오류가 발생한 경우, 콘솔에 에러 메시지 출력
            .catch(error => {
                console.error('댓글 로드 오류:', error);
            });
    }

    // 댓글 표시
    function displayComments(comments) {
        const commentList = document.getElementById('comment-list');
        const noCommentsMessage = document.getElementById('no-comments-message');

        // 댓글 목록 초기화
        commentList.innerHTML = '';

        //댓글이 없을 경우, "댓글이 없습니다"라는 메시지를 화면에 표시하고, 그 후의 코드를 실행하지 않음
        if (comments.length === 0) {
            commentList.appendChild(noCommentsMessage);
            return;
        }

        // 댓글 표시
        comments.forEach(comment => {
            if (!comment.isDeleted) {
                // 댓글 데이터를 바탕으로 댓글 요소를 생성
                const commentElement = createCommentElement(comment);
                // 생성된 댓글 요소를 commentList(댓글 리스트)에 추가
                commentList.appendChild(commentElement);
            }
        });
    }

    // 댓글 요소 생성
    function createCommentElement(comment) {
        const template = document.getElementById('comment-template');
        const commentElement = template.content.cloneNode(true).querySelector('.comment');

        // 프로필 이미지 설정
        const avatarImg = commentElement.querySelector('.profile-image');
        avatarImg.src = comment.profileImgUrl || '/assets/images/default-profile.png';

        // 작성자, 날짜 설정
        commentElement.querySelector('.author-name').textContent = comment.empName;
        commentElement.querySelector('.comment-date').textContent = formatDate(comment.createdAt);

        // 내용 설정
        commentElement.querySelector('.comment-content').textContent = comment.content;
        commentElement.querySelector('.edit-comment-content').value = comment.content;

        // 데이터 속성 추가
        commentElement.setAttribute('data-id', comment.id);
        commentElement.setAttribute('data-author-id', comment.empId);

        // 현재 사용자가 작성한 댓글이 아니면 수정/삭제 버튼 숨기기
        if (comment.empId != currentUser.id) {
            const actionButtons = commentElement.querySelector('.comment-actions');
            actionButtons.style.display = 'none';
        }

        // 수정 버튼 이벤트
        const editBtn = commentElement.querySelector('.edit-comment-btn');
        editBtn.addEventListener('click', function() {
            const commentContent = commentElement.querySelector('.comment-content');
            const editForm = commentElement.querySelector('.comment-edit-form');

            commentContent.style.display = 'none';
            editForm.style.display = 'block';
        });

        // 수정 취소 버튼 이벤트
        const cancelEditBtn = commentElement.querySelector('.cancel-edit-btn');
        cancelEditBtn.addEventListener('click', function() {
            const commentContent = commentElement.querySelector('.comment-content');
            const editForm = commentElement.querySelector('.comment-edit-form');

            commentContent.style.display = 'block';
            editForm.style.display = 'none';
        });

        // 수정 저장 버튼 이벤트
        const saveEditBtn = commentElement.querySelector('.save-edit-btn');
        saveEditBtn.addEventListener('click', function() {
            const newContent = commentElement.querySelector('.edit-comment-content').value.trim();
            if (!newContent) {
                alert('댓글 내용을 입력해주세요.');
                return;
            }

            updateComment(comment.id, newContent);
        });

        // 삭제 버튼 이벤트
        const deleteBtn = commentElement.querySelector('.delete-comment-btn');
        deleteBtn.addEventListener('click', function() {
            if (confirm('정말 이 댓글을 삭제하시겠습니까?')) {
                deleteComment(comment.id);
            }
        });

        return commentElement;
    }

    // 댓글 추가 함수
    function addComment(content) {
        const token = localStorage.getItem('accessToken');

        fetch('/api/comments', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                postId: postId,
                empId: currentUser.id,
                content: content
            })
        })
            .then(response => response.json())
            .then(comment => {
                // 폼 초기화
                document.getElementById('comment-content').value = '';

                // 댓글 다시 로드
                loadComments();
            })
            .catch(error => {
                console.error('댓글 추가 오류:', error);
                alert('댓글 추가 중 오류가 발생했습니다.');
            });
    }

    // 댓글 수정 함수
    function updateComment(commentId, content) {
        const token = localStorage.getItem('accessToken');

        fetch(`/api/comments/${commentId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                content: content
            })
        })
            .then(response => response.json())
            .then(comment => {
                // 댓글 다시 로드
                loadComments();
            })
            .catch(error => {
                console.error('댓글 수정 오류:', error);
                alert('댓글 수정 중 오류가 발생했습니다.');
            });
    }

    // 댓글 삭제 함수
    function deleteComment(commentId) {
        const token = localStorage.getItem('accessToken');

        fetch(`/api/comments/${commentId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => response.json())
            .then(data => {
                // 댓글 다시 로드
                loadComments();
            })
            .catch(error => {
                console.error('댓글 삭제 오류:', error);
                alert('댓글 삭제 중 오류가 발생했습니다.');
            });
    }

    // 날짜 포맷팅 함수
    function formatDate(dateString) {
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now - date;
        const diffSec = Math.floor(diffMs / 1000);
        const diffMin = Math.floor(diffSec / 60);
        const diffHour = Math.floor(diffMin / 60);
        const diffDay = Math.floor(diffHour / 24);

        if (diffDay > 0) {
            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
        } else if (diffHour > 0) {
            return `${diffHour}시간 전`;
        } else if (diffMin > 0) {
            return `${diffMin}분 전`;
        } else {
            return '방금 전';
        }
    }
});
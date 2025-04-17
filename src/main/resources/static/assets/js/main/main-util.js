// 브랜드 스토리텔링 캐러셀 기능
document.addEventListener('DOMContentLoaded', function() {
    const valueItems = document.querySelectorAll('.value-item');
    const navDots = document.querySelectorAll('.dot');
    const prevButton = document.querySelector('.nav-button.prev');
    const nextButton = document.querySelector('.nav-button.next');
    let currentIndex = 0;
    let intervalId;

    // 자동 슬라이드 시작
    function startAutoSlide() {
        intervalId = setInterval(() => {
            nextSlide();
        }, 5000); // 5초마다 슬라이드 변경
    }

    // 자동 슬라이드 멈춤
    function stopAutoSlide() {
        clearInterval(intervalId);
    }

    // 슬라이드 업데이트 함수
    function updateSlide(index) {
        // 현재 활성화된 슬라이드 및 도트 비활성화
        document.querySelector('.value-item.active').classList.remove('active');
        document.querySelector('.dot.active').classList.remove('active');

        // 새 슬라이드 및 도트 활성화
        valueItems[index].classList.add('active');
        navDots[index].classList.add('active');

        // 현재 인덱스 업데이트
        currentIndex = index;
    }

    // 다음 슬라이드로 이동
    function nextSlide() {
        let newIndex = currentIndex + 1;
        if (newIndex >= valueItems.length) {
            newIndex = 0;
        }
        updateSlide(newIndex);
    }

    // 이전 슬라이드로 이동
    function prevSlide() {
        let newIndex = currentIndex - 1;
        if (newIndex < 0) {
            newIndex = valueItems.length - 1;
        }
        updateSlide(newIndex);
    }

    // 네비게이션 도트 클릭 이벤트
    navDots.forEach((dot, index) => {
        dot.addEventListener('click', () => {
            stopAutoSlide();
            updateSlide(index);
            startAutoSlide();
        });
    });

    // 이전 버튼 클릭 이벤트
    prevButton.addEventListener('click', () => {
        stopAutoSlide();
        prevSlide();
        startAutoSlide();
    });

    // 다음 버튼 클릭 이벤트
    nextButton.addEventListener('click', () => {
        stopAutoSlide();
        nextSlide();
        startAutoSlide();
    });

    // 마일스톤 호버 효과
    const milestones = document.querySelectorAll('.milestone');
    milestones.forEach(milestone => {
        milestone.addEventListener('mouseenter', function() {
            this.querySelector('.milestone-year').style.transform = 'scale(1.1)';
            this.querySelector('.milestone-year').style.backgroundColor = 'var(--primary-color)';
            this.querySelector('.milestone-year').style.color = 'white';
        });

        milestone.addEventListener('mouseleave', function() {
            this.querySelector('.milestone-year').style.transform = '';
            this.querySelector('.milestone-year').style.backgroundColor = '';
            this.querySelector('.milestone-year').style.color = '';
        });
    });

    // 페이지 로드 시 첫 번째 슬라이드 활성화 및 자동 슬라이드 시작
    updateSlide(0);
    startAutoSlide();

    // 가치 항목에 호버 시 자동 슬라이드 중지
    const valuesCarousel = document.querySelector('.values-carousel');
    valuesCarousel.addEventListener('mouseenter', stopAutoSlide);
    valuesCarousel.addEventListener('mouseleave', startAutoSlide);

    // 미션 문구에 타이핑 효과 추가
    const missionText = document.querySelector('.mission-text');
    const originalText = missionText.textContent;
    let typingSpeed = 50; // 타이핑 속도 (밀리초)

    function typeText() {
        missionText.textContent = '';
        let charIndex = 0;

        function type() {
            if (charIndex < originalText.length) {
                missionText.textContent += originalText.charAt(charIndex);
                charIndex++;
                setTimeout(type, typingSpeed);
            }
        }

        setTimeout(type, 1000); // 1초 후 타이핑 시작
    }

    // 페이지 로드 후 한 번만 실행
    typeText();

    // 스크롤 감지하여 애니메이션 재생
    const brandSection = document.querySelector('.brand-storytelling');

    function isInViewport(element) {
        const rect = element.getBoundingClientRect();
        return (
            rect.top >= 0 &&
            rect.left >= 0 &&
            rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
            rect.right <= (window.innerWidth || document.documentElement.clientWidth)
        );
    }

    function handleScroll() {
        if (isInViewport(brandSection)) {
            // 마일스톤 애니메이션
            milestones.forEach((milestone, index) => {
                setTimeout(() => {
                    milestone.style.opacity = '1';
                    milestone.style.transform = 'translateY(0)';
                }, 300 * index);
            });

            // 스크롤 이벤트 제거 (한 번만 실행)
            window.removeEventListener('scroll', handleScroll);
        }
    }

    // 마일스톤 초기 상태 설정
    milestones.forEach(milestone => {
        milestone.style.opacity = '0';
        milestone.style.transform = 'translateY(20px)';
        milestone.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
    });

    // 스크롤 이벤트 리스너 추가
    window.addEventListener('scroll', handleScroll);
    // 페이지 로드 시 한 번 체크
    handleScroll();
});
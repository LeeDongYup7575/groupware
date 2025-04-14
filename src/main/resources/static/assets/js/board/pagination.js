$(document).ready(function () {
    // 페이지네이션 설정
    const recordsPerPage = 5;
    const naviCountPerPage = 5;

    $("#contents").each(function () { // 선택자를 "#contents"로 변경
        let $container = $(this);
        let $table = $container.find("table.board-list-table"); // 테이블 선택자 명확히
        let $rows = $table.find("tbody tr");
        let totalRecords = $rows.length;
        let totalPages = Math.ceil(totalRecords / recordsPerPage);

        if (totalRecords > recordsPerPage) {
            let $pagination = $container.find(".pagination");
            if ($pagination.length === 0) {
                $pagination = $('<div class="pagination"></div>');
                $container.append($pagination);
            }

            let currentPage = 1;

            function renderPagination(page) {
                currentPage = page;
                $pagination.empty();

                let groupIndex = Math.floor((currentPage - 1) / naviCountPerPage);
                let startPage = groupIndex * naviCountPerPage + 1;
                let endPage = Math.min(startPage + naviCountPerPage - 1, totalPages);

                if (startPage > 1) {
                    $pagination.append('<a href="#" class="page-link prev-group" data-page="' + (startPage - 1) + '"><i class="fas fa-angle-left"></i></a>');
                }

                for (let i = startPage; i <= endPage; i++) {
                    $pagination.append('<a href="#" class="page-link" data-page="' + i + '">' + i + '</a>');
                }

                if (endPage < totalPages) {
                    $pagination.append('<a href="#" class="page-link next-group" data-page="' + (endPage + 1) + '"><i class="fas fa-angle-right"></i></a>');
                }

                $pagination.find("a.page-link").removeClass("active");
                $pagination.find('a.page-link[data-page="' + currentPage + '"]').addClass("active");
            }

            function showPage(page) {
                let start = (page - 1) * recordsPerPage;
                let end = start + recordsPerPage;
                $rows.hide();
                $rows.slice(start, end).show();
            }

            renderPagination(1);
            showPage(1);

            $pagination.on("click", "a.page-link", function (e) {
                e.preventDefault();
                let targetPage = $(this).data("page");
                renderPagination(targetPage);
                showPage(targetPage);

                $('html, body').animate({
                    scrollTop: $container.offset().top - 100
                }, 300);
            });
        } else {
            $rows.show();
            $container.find(".pagination").hide();
        }
    });

    // ... (다른 스크립트)
});
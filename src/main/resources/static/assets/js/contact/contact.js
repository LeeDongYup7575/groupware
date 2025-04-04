document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;

    const newPath = currentPath + '?tab=shared';
    history.replaceState(null, '', newPath);

});

function filterList(team) {
    let rows = document.querySelectorAll("#contactList tr");

    document.querySelectorAll('.contact-sidebar-item').forEach(item => {
        item.classList.remove('active');
    });

    document.querySelector(`[onclick="filterList('${team}')"]`).classList.add('active');

    rows.forEach(row => {
        let department = row.dataset.team;

        if (team === 'all' || department === team) {
            row.style.display = "";
        } else {
            row.style.display = "none";
        }
    });
}

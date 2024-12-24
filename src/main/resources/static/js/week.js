/*<![CDATA[*/
const currentYear = window.currentYear || 2025;
const currentWeek = window.currentWeek || 30;
const serviceId   = window.serviceId || 30;

function prevWeek() {
    navigateToWeek(currentYear, currentWeek - 1);
}

function nextWeek() {
    navigateToWeek(currentYear, currentWeek + 1);
}

function navigateToWeek(year, week) {
    const url = `/service/${serviceId}/week/${year}/${week}`;
    window.location.href = url;
}
/*]]>*/
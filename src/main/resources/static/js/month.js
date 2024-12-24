/*<![CDATA[*/
const translations = window.translations || {week: 'Week'}
const calendarDays = document.getElementById("calendarDays");
const currentMonthDisplay = document.getElementById("currentMonth");
const currentYearDisplay = document.getElementById("currentYear");

const serviceId = window.serviceId || 12;
const month = window.month || 0;
const year = window.year || 2025;
const monthName = window.monthName || "januar";
const occupation = window.occupation || 20

function renderCalendar() {


    currentMonthDisplay.textContent = window.monthName || "git"
    currentYearDisplay.textContent = window.year || 2025
    // Clear previous days
    calendarDays.innerHTML = "";

    const firstDayOfMonth = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    let currentWeek = 0;

    // Add empty slots for days before the first day of the month
    const offset = (firstDayOfMonth || 7) - 1;
    for (let i = 0; i < offset; i++) {
        if (i === 0) {
            // Add week link
            const weekLink = document.createElement("div");
            weekLink.classList.add("week-link");
            weekLink.textContent = `${translations.week} ${getWeekNumber(year, month-1, getPreviousDay(month-1, year))}`;
            weekLink.addEventListener("click", () => navigateToWeek(year, getWeekNumber(year, month-1, getPreviousDay(month-1, year))));
            if (month===0) {
                weekLink.textContent = `${translations.week} ${getWeekNumber(year, month, 1)}`;
                weekLink.addEventListener("click", () => navigateToWeek(year, getWeekNumber(year, month, 1)));
            }
            calendarDays.appendChild(weekLink);
        }

        const emptyCell = document.createElement("div");
        emptyCell.classList.add("day", "disabled");
        calendarDays.appendChild(emptyCell);
    }

    // Add days of the month
    for (let day = 1; day <= daysInMonth; day++) {
        if ((offset + day - 1) % 7 === 0) {
            // Add week link
            const weekLink = document.createElement("div");
            weekLink.classList.add("week-link");
            if (month === 11 && day > 25) {
                weekLink.textContent = `${translations.week} ${getWeekNumber(year+1, 0, 1)}`;
                weekLink.addEventListener("click", () => navigateToWeek(year+1, getWeekNumber(year+1, 0, 1)));
            } else {
                weekLink.textContent = `${translations.week} ${getWeekNumber(year, month, day)}`;
                weekLink.addEventListener("click", () => navigateToWeek(year, getWeekNumber(year, month, day)));
            }
            calendarDays.appendChild(weekLink);
            currentWeek++;
        }

        const dayCell = document.createElement("div");
        dayCell.classList.add("day");
        dayCell.textContent = day;

        const percentOccupied = occupation[day]

        // Ustaw dynamiczny kolor tła
        dayCell.style.background = `linear-gradient(to top, #f5acaf ${percentOccupied}%, #bdf5c2 ${percentOccupied}%)`;

        dayCell.addEventListener("click", () => navigateToBooking(year, month, day));

        calendarDays.appendChild(dayCell);
    }
}

function getPreviousDay(month, year) {
    if (month === 0 || month === 2 || month === 4 || month === 6 || month === 7 || month === 9 || month === 11) {
        return 31
    }
    if (month === 3 || month === 5 || month === 8 || month === 10) {
        return 30
    }
    if (year%4===0) {
        return 29
    }
    return 28
}

function navigateToBooking(year, month, day) {
    const formattedMonth = String(month + 1).padStart(2, "0");
    const formattedDay = String(day).padStart(2, "0");
    const url = `/service/${serviceId}/day/${year}/${formattedMonth}/${formattedDay}`;
    window.location.href = url;
}

function navigateToWeek(year, weekNumber) {
    const url = `/service/${serviceId}/week/${year}/${weekNumber}`;
    window.location.href = url;
}

function getWeekNumber(year, month, day) {
    const date = new Date(year, month, day);
    const startOfYear = new Date(year, 0, 0);
    const pastDaysOfYear = (date - startOfYear) / 86400000;
    return Math.ceil((pastDaysOfYear + startOfYear.getDay()) / 7);
}

function prevMonth() {
    if (month===0){
        changeDate(year-1, 12)
    } else {
        changeDate(year, month);
    }
}

function nextMonth() {
    if (month===11){
        changeDate(year+1, 1)
    } else {
        changeDate(year, month+2)

    }
}

function nextYear(){
    changeDate(year+1, month+1)
}

function prevYear(){
    changeDate(year-1, month+1)
}

function changeDate(year, month){
    const url = `/service/${serviceId}/month/${year}/${month}`;
    window.location.href = url;
}
// Initialize calendar
renderCalendar();
/*]]>*/
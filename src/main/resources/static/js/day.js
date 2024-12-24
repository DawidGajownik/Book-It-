
/*<![CDATA[*/
const year = window.year || 2025;
const month = window.month || 2;
const day = window.day || 20;
const week = window.week || 8; // Numer tygodnia przekazany z backendu
const timeSlotsContainer = document.getElementById("timeSlots");

// Dane przekazywane z backendu
const availableDays = window.availableDays || ['Monday', 'Tuesday', 'Wednesday', 'Friday'];
const occupation = window.occupation || {'07:00': 54, '08:00': 6, '09:00': 43};
const availableHours = Object.keys(occupation)
const serviceId = window.serviceId || 30;
const timeNeeded = window.timeNeeded || 3; // Czas w godzinach

const dayOfWeek = window.dayOfWeek || 'Monday'; // Bieżący dzień tygodnia
const bookedTimes = Object.entries(occupation).filter(([key, value])=>value===100).map(([key])=>key)

function renderTimeSlots() {
    // Wyczyść poprzednie sloty
    timeSlotsContainer.innerHTML = "";

    // Sprawdź, czy dzień jest dostępny
    // if (!availableDays.includes(dayOfWeek)) {
    //     const message = document.createElement("p");
    //     message.textContent = "This day is unavailable for booking.";
    //     timeSlotsContainer.appendChild(message);
    //     return;
    // }

    // Generuj sloty na podstawie godzin i zarezerwowanych terminów
    availableHours.forEach(time => {
        const timeSlot = document.createElement("div");
        timeSlot.classList.add("time-slot");

        if (bookedTimes.includes(time)) {
            timeSlot.classList.add("unavailable");
            timeSlot.textContent = `${time} - Booked`;
        } else {
            timeSlot.style.background = 'linear-gradient(to right, #f5acaf '+occupation[time]+'%, #bdf5c2 '+occupation[time]+'%)';
            timeSlot.innerHTML = `<span>${time}</span> - Available`;
            timeSlot.addEventListener("click", () => bookTime(time));
        }

        timeSlotsContainer.appendChild(timeSlot);
    });
}

function bookTime(time) {
    const url = `/service/${serviceId}/time/${year}/${month}/${day}/${time}`;
    window.location.href = url;
}

function prevDay() {
    const currentDate = new Date(year, month - 1, day); // Utwórz datę z bieżących wartości
    currentDate.setDate(currentDate.getDate() - 1); // Cofnij o jeden dzień
    navigateToDate(currentDate); // Przejdź do poprzedniego dnia
}

function nextDay() {
    const currentDate = new Date(year, month - 1, day); // Utwórz datę z bieżących wartości
    currentDate.setDate(currentDate.getDate() + 1); // Przesuń o jeden dzień do przodu
    navigateToDate(currentDate); // Przejdź do następnego dnia
}

function navigateToDate(date) {
    const newYear = date.getFullYear();
    const newMonth = (date.getMonth() + 1).toString().padStart(2, "0"); // Miesiące są liczone od 0
    const newDay = date.getDate().toString().padStart(2, "0");

    const url = `/service/${serviceId}/day/${newYear}/${newMonth}/${newDay}`; // Budowanie adresu URL
    window.location.href = url; // Przekierowanie na nowy adres
}

// Inicjalizacja
renderTimeSlots();

/*]]>*/
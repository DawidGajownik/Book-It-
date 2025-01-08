/*<![CDATA[*/
const translations = window.translations || {week: 'Week'}
const calendarDays = document.getElementById("calendarDays");
const currentMonthDisplay = document.getElementById("currentMonth");
const currentYearDisplay = document.getElementById("currentYear");
const blockss = document.getElementById("blocks")
const blocks = blockss.querySelectorAll(".block")
const hourHeight = 60; // Height in pixels for one hour block
const quarterHeight = hourHeight / 4; // 15-minute interval height
const scheduleContainer = document.getElementById("schedule")
const scheduleTop = document.querySelector(".blocks").getBoundingClientRect().top;
const scheduleHeight = (scheduleContainer.children.length-1) * hourHeight; // 9 hours from 9:00 to 17:00
let currentBlock = null;
let dayToSend = 0

function renderCalendar() {

    fetch(`/employee-dashboard-occupancy?month=${month+1}&year=${year}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error');
            }
            return response.json();
        })
        .then(data => {
            occupation = data
            currentMonthDisplay.textContent = getMonthName(month) || "git"
            currentYearDisplay.textContent = year || 2025
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
                    if (month===0) {
                        weekLink.textContent = `${translations.week} ${getWeekNumber(year, month, 1)}`;
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

                dayCell.addEventListener("click", () => getDay(year, month, day));

                calendarDays.appendChild(dayCell);
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function getMonthName(monthNumber) {
    monthNumber=monthNumber+1
    if (monthNumber === 1) {
        return window.january;
    } else if (monthNumber === 2) {
        return window.february;
    } else if (monthNumber === 3) {
        return window.march;
    } else if (monthNumber === 4) {
        return window.april;
    } else if (monthNumber === 5) {
        return window.may;
    } else if (monthNumber === 6) {
        return window.june;
    } else if (monthNumber === 7) {
        return window.july;
    } else if (monthNumber === 8) {
        return window.august;
    } else if (monthNumber === 9) {
        return window.september;
    } else if (monthNumber === 10) {
        return window.october;
    } else if (monthNumber === 11) {
        return window.november;
    } else if (monthNumber === 12) {
        return window.december;
    } else {
        return null; // Invalid month number
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

function getDay(year, month, day) {
    dayToSend=day
    blockss.innerHTML=''
    fetch('/employee-day?year='+year+'&month='+month+'&day='+day).
        then(response => {
        if (!response.ok) {
            throw new Error('errrrrorrr')
        }
        return response.json();
    })
        .then(data => {
            data.forEach(appointment => {
                const div = document.createElement("div")
                div.classList.add('block')
                div.style.top=appointment[1][0]+'px'
                div.style.height=appointment[1][1]+'px'
                div.textContent=appointment[0].service+' '+appointment[0].startDateTimeFormatted+' - '+appointment[0].endDateTimeFormatted
                div.setAttribute('data-id', 1)
                div.setAttribute("start", appointment[0].startDateTimeFormatted)
                blockss.appendChild(div)
                makeBlockDraggable(div)
            })
        })
        .catch(error => {
            console.error('error', error)
        })
}

const updateTextContent = (block) => {
    const top = parseInt(block.style.top, 10);
    const height = parseInt(block.style.height, 10);

    const startMinutes = (top - 18) + openingHours[0][0] * 60;
    const startHour = Math.floor(startMinutes / 60);
    const startMinute = String(startMinutes % 60).padStart(2, "0");

    const endMinutes = startMinutes + height+2;
    const endHour = Math.floor(endMinutes / 60);
    const endMinute = String(endMinutes % 60).padStart(2, "0");

    const baseText = block.textContent.split(" ").slice(0, -3).join(" ");
    block.textContent = `${baseText} ${startHour}:${startMinute} - ${endHour}:${endMinute}`;
};

function makeBlockDraggable(block) {
    block.addEventListener("mousedown", (e) => {
        currentBlock = block;
        dragOffsetY = e.clientY - block.getBoundingClientRect().top;
        block.classList.add("dragging");
    });

    document.addEventListener("mousemove", (e) => {
        if (currentBlock) {
            let newTop = e.clientY - scheduleTop - dragOffsetY;
            const height = currentBlock.style.height;
            // Snap to 15-minute intervals and limit position
            newTop = snapToInterval(newTop, height);
            newTop = limitPosition(newTop, parseInt(height));

            const overlapping = isOverlapping(currentBlock, newTop, parseInt(height)).isOverlapping
            const overlappingBlock = isOverlapping(currentBlock, newTop, parseInt(height)).overlappingBlock


            if (overlapping && overlappingBlock) {

                let higherBlock = currentBlock
                let lowerBlock = overlappingBlock


                const topOverlappingBlock = parseInt(overlappingBlock.style.top.substring(0,overlappingBlock.style.top.length-2))
                const topCurrentBlock = parseInt(currentBlock.style.top.substring(0,currentBlock.style.top.length-2))


                if (topOverlappingBlock<topCurrentBlock){
                    higherBlock = overlappingBlock
                    lowerBlock = currentBlock
                }

                const newTopForHigher
                    =parseInt(lowerBlock.style.top.substring(0,lowerBlock.style.top.length-2))
                    +parseInt(lowerBlock.style.height.substring(0,lowerBlock.style.height.length-2))
                    -parseInt(higherBlock.style.height.substring(0,higherBlock.style.height.length-2))

                lowerBlock.style.top = `${higherBlock.style.top}`
                higherBlock.style.top = newTopForHigher+'px'

                updateTextContent(higherBlock);
                updateTextContent(lowerBlock);

                currentBlock.classList.remove("dragging");
                currentBlock = null; // Reset after swapping
            } else {
                currentBlock.style.top = `${newTop}px`;
                currentBlock.classList.remove("overlap");
            }
        }
    });

    document.addEventListener("mouseup", () => {
        if (currentBlock) {
            currentBlock.classList.remove("dragging");
            currentBlock = null;
        }
    });
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

function changeDate(yearInput, monthInput){
    month=monthInput-1
    year=yearInput
    renderCalendar()
}


const isOverlapping = (block, top, height) => {
    const overlappingBlock= Array.from(blockss.querySelectorAll(".block")).find(other => {
        if (other === block) return false;

        const otherTop = parseInt(other.style.top, 10);
        const otherHeight = parseInt(other.style.height, 10);

        if (isNaN(otherTop) || isNaN(otherHeight)) {
            console.warn("Block missing top/height:", other);
            return false;
        }

        return top < otherTop + otherHeight && top + height > otherTop; // Overlap condition
    });

    return {
        isOverlapping: !!overlappingBlock,
        overlappingBlock: overlappingBlock || null,
    };
};


// Snap position to 15-minute intervals
const snapToInterval = (top, height) => {
    return Math.round(top / quarterHeight) * quarterHeight + 3;
};

const changeTextContent = (top) => {
    const height = currentBlock.style.height
    const split = currentBlock.textContent.split(" ")
    let newTextContentBeginning=""
    for (let i = 0; i < split.length-3; i++) {
        newTextContentBeginning=newTextContentBeginning+split[i]+" "
    }
    const heightNumber = parseInt(height.substring(0,height.length-2),10)
    const newStartTimeInMinutes = (top-18)+openingHours[0][0]*60
    const newStartHour = Math.floor(newStartTimeInMinutes/60)
    const newStartMinutes = String(newStartTimeInMinutes%60).padStart(2,"0")
    const newEndTimeInMinutes = (top-16+heightNumber)+openingHours[0][0]*60
    const newEndHour = Math.floor(newEndTimeInMinutes/60)
    const newEndMinutes = String(newEndTimeInMinutes%60).padStart(2,"0")
    newTextContentBeginning=newTextContentBeginning+newStartHour+":"+newStartMinutes+" - " + newEndHour+":"+newEndMinutes
    if(!isOverlapping(currentBlock, top, parseInt(height)).isOverlapping){
        currentBlock.textContent=newTextContentBeginning
    }

}

// Limit position to within schedule
const limitPosition = (top, blockHeight) => {
    const newTop =  Math.max(
        18,
        Math.min(
            top,
            scheduleHeight-blockHeight+16
        )
    );
    changeTextContent(newTop)
    return newTop
};

// Save changes
document.getElementById("save-button").addEventListener("click", () => {
    const newBlocks = document.querySelectorAll(".block")
    const updatedBlocks = Array.from(newBlocks).map(block => {

        const top = parseInt(block.style.top);

        const startMinutes = (top - 18) + openingHours[0][0] * 60;
        const startHour = Math.floor(startMinutes / 60);
        const startMinute = String(startMinutes % 60).padStart(2, "0");

        // const startHour = 9 + Math.floor(top / hourHeight);
        // const startMinutes = Math.round((top % hourHeight) / quarterHeight) * 15;
        const endHour = startHour + Math.floor(parseInt(block.style.height) / hourHeight);
        const endMinutes = (startMinutes + Math.round((parseInt(block.style.height) % hourHeight) / quarterHeight) * 15) % 60;
        return {
            id: block.dataset.id,
            label: block.textContent,
            start: block.getAttribute("start"),
            year: year,
            month: month+1,
            day: dayToSend
        };
    });

    // Send updated data to the server
    fetch("/appointments-update", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedBlocks)
    }).then(response => {
        if (response.ok) {
            alert("Schedule updated successfully!");
        } else {
            alert("Failed to update schedule.");
        }
    });
});

function renderMessages (x) {
    const chatMessages = document.getElementById("chat-messages")
    const newChatMessages = document.createElement('div')
    newChatMessages.id='chat-messages'
    //chatMessages.innerHTML=''
    fetch(`/get-company-chat`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error');
            }
            return response.json();
        })
        .then(data => {
            data.forEach(message => {
                const msg = document.createElement("div")
                msg.classList.add('message')
                msg.setAttribute('bis_skin_checked', 1)
                if (message.amISender===true) {
                    msg.classList.add('user')
                } else {
                    msg.classList.add('other')
                }
                const nick = document.createElement("div")
                nick.classList.add('message-info')
                nick.textContent=message.sender
                nick.setAttribute('bis_skin_checked', 1)
                const content = document.createElement("div")
                content.classList.add('message-content')
                content.textContent=message.content
                content.setAttribute('bis_skin_checked', 1)
                msg.appendChild(nick)
                msg.appendChild(content)
                newChatMessages.appendChild(msg)
            })
            if (newChatMessages.innerHTML!==chatMessages.innerHTML){
                chatMessages.innerHTML=newChatMessages.innerHTML
            }
            if(x){
                chatMessages.scrollTop = chatMessages.scrollHeight;
            }

        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function send() {
    const message = document.getElementById("message")
    fetch("/send-company-message", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: message.value
    }).then(r => {
        renderMessages(true)
        message.value=''
    })
}

document.querySelector(".chat-input input").addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        event.preventDefault(); // Zatrzymaj domyślne zachowanie formularza
        send();
    }
});

setInterval(renderMessages, 1000)
renderCalendar();
renderMessages(true)
getDay(year, month, 1)
/*]]>*/
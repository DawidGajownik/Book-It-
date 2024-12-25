
document.addEventListener("DOMContentLoaded", () => {
    const blocks = document.querySelectorAll(".block");
    const hourHeight = 60; // Height in pixels for one hour block
    const quarterHeight = hourHeight / 4; // 15-minute interval height
    const scheduleContainer = document.getElementById("schedule")
    const scheduleTop = document.querySelector(".blocks").getBoundingClientRect().top;
    console.log(scheduleContainer)
    const scheduleHeight = (scheduleContainer.children.length-1) * hourHeight; // 9 hours from 9:00 to 17:00
    let dragOffsetY = 0;
    let currentBlock = null;


    const openingHours = window.openingHours


    // Check if a block overlaps another
    const isOverlapping = (block, top, height) => {
        return Array.from(blocks).some(other => {
            if (other === block) return false;
            const otherTop = parseInt(other.style.top);
            const otherHeight = parseInt(other.style.height);
            return (
                top < otherTop + otherHeight && top + height > otherTop // Overlap condition
            );
        });
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
        const newEndTimeInMinutes = (top-18+heightNumber)+openingHours[0][0]*60
        const newEndHour = Math.floor(newEndTimeInMinutes/60)
        const newEndMinutes = String(newEndTimeInMinutes%60).padStart(2,"0")
        newTextContentBeginning=newTextContentBeginning+newStartHour+":"+newStartMinutes+" - " + newEndHour+":"+newEndMinutes
        if(!isOverlapping(currentBlock, top, parseInt(height))){
            currentBlock.textContent=newTextContentBeginning
        }

    }

    // Limit position to within schedule
    const limitPosition = (top, blockHeight) => {
        const newTop =  Math.max(
            18,
            Math.min(
                top,
                scheduleHeight-blockHeight+18
            )
        );
        changeTextContent(newTop)
        return newTop
    };

    // Make blocks draggable
    blocks.forEach(block => {
        block.addEventListener("mousedown", (e) => {
            currentBlock = block;
            dragOffsetY = e.clientY - block.getBoundingClientRect().top;
            block.classList.add("dragging");
        });

        document.addEventListener("mousemove", (e) => {
            if (currentBlock) {
                let newTop = e.clientY - scheduleTop - dragOffsetY;
                const height = currentBlock.style.height
                // Snap to 15-minute intervals and limit position
                newTop = snapToInterval(newTop, height);
                newTop = limitPosition(newTop, parseInt(height));

                if (!isOverlapping(currentBlock, newTop, parseInt(height))) {

                    currentBlock.style.top = `${newTop}px`;
                    currentBlock.classList.remove("overlap");
                } else {
                    currentBlock.classList.add("overlap");
                }
            }
        });

        document.addEventListener("mouseup", (e) => {
            if (currentBlock) {
                currentBlock.classList.remove("dragging");
                currentBlock = null;
            }
        });
    });

    // Save changes
    document.getElementById("save-button").addEventListener("click", () => {
        const updatedBlocks = Array.from(blocks).map(block => {
            const top = parseInt(block.style.top);
            const startHour = 9 + Math.floor(top / hourHeight);
            const startMinutes = Math.round((top % hourHeight) / quarterHeight) * 15;
            const endHour = startHour + Math.floor(parseInt(block.style.height) / hourHeight);
            const endMinutes = (startMinutes + Math.round((parseInt(block.style.height) % hourHeight) / quarterHeight) * 15) % 60;
            return {
                id: block.dataset.id,
                label: block.textContent,
                start: `${String(startHour).padStart(2, "0")}:${String(startMinutes).padStart(2, "0")}`,
                end: `${String(endHour).padStart(2, "0")}:${String(endMinutes).padStart(2, "0")}`
            };
        });

        // Send updated data to the server
        fetch("/api/schedule/update", {
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
});
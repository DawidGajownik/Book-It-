const pageInput = document.getElementById('page');
const pagesQuantity = document.getElementById('pagesQuantity')


//getting available pages
function getPages() {
    fetch('/pages')
        .then(response => response.text())
        .then(pagesQ => {
            pagesQuantity.value=pagesQ
            if (pageInput.value>pagesQuantity.value){
                pageInput.value=pagesQuantity.value
                filter()
            }
            pageButtonGenerate(pagesQ)
        })
        .catch(error => {
            console.error('gettingPages:', error);
        });
}

function pageButtonGenerate(pages){

    const pageButtonsDiv = document.getElementById("pages")
    const pageButtons = document.getElementsByName("page-nr");

    //default pages buttons quantity = 5, with chosen page in the middle of buttons; first button distraction = -2
    let firstButtonValueDistraction = -2
    let pagesButtons = pages
    if (pages>5){
        pagesButtons=5
    }

    //cleaning buttons
    pageButtonsDiv.innerHTML=''


    //first and previous buttons
    const firstPageButton = document.createElement('button')
    firstPageButton.classList.add('button')
    firstPageButton.onclick = function () {
        page(1);
    };
    firstPageButton.innerText=firstPage
    pageButtonsDiv.appendChild(firstPageButton)

    const previousPageButton = document.createElement('button')
    previousPageButton.classList.add('button')
    previousPageButton.onclick = function () {
        page('prev');
    };
    previousPageButton.innerText=previousPage
    pageButtonsDiv.appendChild(previousPageButton)


    //numeric buttons, quantity depends on pages quantity, max = 5
    for (let i = 1; i <= pagesButtons ; i++) {
        const button = document.createElement('button')
        button.classList.add('button')
        button.name='page-nr'
        button.style.width='10%'
        pageButtonsDiv.appendChild(button)
    }


    //next/last buttons
    const nextPageButton = document.createElement('button')
    nextPageButton.classList.add('button')
    nextPageButton.onclick = function () {
        page('next');
    };
    nextPageButton.innerText=nextPage
    pageButtonsDiv.appendChild(nextPageButton)

    const lastPageButton = document.createElement('button')
    lastPageButton.classList.add('button')
    lastPageButton.onclick = function () {
        page('last');
    };
    lastPageButton.innerText=lastPage
    pageButtonsDiv.appendChild(lastPageButton)


    //changing first button distraction depending on chosen page
    //if prev search has 0 result and 0 pages it changes value to 1
    if (pageInput.value==0){
        pageInput.value=1
    }

    //if we are on page 1 we don't want page -2 and -1
    if (pageInput.value==1){
        firstButtonValueDistraction=0
    }
    //similar
    if (pageInput.value==2){
        firstButtonValueDistraction=-1
    }

    //last and pre-last pages
    if(pages>4){
        if (pageInput.value==pages){
            firstButtonValueDistraction=-4
        }
        if (pageInput.value==pages-1){
            firstButtonValueDistraction=-3
        }
    }


    //innertext for all buttons, and function with argument
    for (let i = 0; i < pageButtons.length; i++) {
        pageButtons[i].innerText=parseInt(pageInput.value) + firstButtonValueDistraction + i
        pageButtons[i].onclick = function () {
            page(pageButtons[i].innerText);
        };
    }


    //disabling active page button
    pageButtons.forEach(button => {
        if(pageInput.value===button.textContent){
            button.style.backgroundColor = "coral"
            button.style.fontWeight = "bold"
            button.disabled = true
        } else {
            button.style.backgroundColor = ""
            button.style.fontWeight = ""
            button.disabled = false
        }
    })
}


function page(page){
    if(page=='last'){
        pageInput.value=pagesQuantity.value
    }
    else if(page=='next'){
        if (pageInput.value<pagesQuantity.value){
            pageInput.value=parseInt(pageInput.value)+1
        }
    }
    else if(page=='prev'){
        if(pageInput.value>1){
            pageInput.value=parseInt(pageInput.value)-1
        }
    } else {
        pageInput.value=page
    }
    filter()
}

function processAddress() {
    const addressInput = document.getElementById('address');
    const processedAddressOutput = document.getElementById('processedAddress');

    if (addressInput.value.trim() === '') {
        processedAddressOutput.textContent = '';
        return;
    }

    fetch(`/process?address=${encodeURIComponent(addressInput.value)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error processing address');
            }
            return response.text();
        })
        .then(data => {
            processedAddressOutput.textContent = data;
        })
        .catch(error => {
            processedAddressOutput.textContent = 'Error processing address';
            console.error('Error:', error);
        });
}

function setParam(element, params){
    if(element.value.length>0){
        params.append(element.name, element.value)
    }
}

function sort(typeOfSort){
    const processedAddressOutput = document.getElementById('processedAddress');
    if (!(typeOfSort==="distance"&&(processedAddressOutput.textContent.length===0||processedAddressOutput.textContent===(document.getElementById("addressNotFound").textContent)))){
        const sort = document.getElementById("sort")
        if (sort.value===typeOfSort){
            typeOfSort = typeOfSort+"-desc"
        }
        sort.value=typeOfSort
        filter()
    }
}

function filter(x) {

    //html elements
    const name = document.getElementById("name")
    const industry = document.getElementById("industry")
    const address = document.getElementById("address")
    const maxDistance = document.getElementById("maxDistance")
    const priceMin = document.getElementById("priceMin")
    const priceMax = document.getElementById("priceMax")
    const sort = document.getElementById("sort")


    //collect selected industries
    const selectedIndustries = Array.from(industry.selectedOptions)
        .map(option => option.value);

    //params to filter
    const params = new URLSearchParams();

    //setting all params
    setParam(name, params)
    selectedIndustries.forEach(industry => params.append("industry", industry));
    setParam(address, params)
    setParam(maxDistance, params)
    setParam(priceMin, params)
    setParam(priceMax, params)
    if (sort.value!=="none"){
        setParam(sort, params)
    }
    params.append("page", pageInput.value)


    //filtering
    fetch(`/filter-services?${params.toString()}`)

        .then(response => {
            if (!response.ok) {
                throw new Error('Error processing search');
            }
            return response.json();
        })
        .then(services => {
            getPages()
            const servicesList = window.services;
            servicesList.innerHTML = ''; // Wyczyść istniejący HTML
            services.forEach(service => {
                const serviceDiv = document.createElement('div');
                serviceDiv.classList.add('service-card');
                const price = service[0].price.toFixed(2)
                serviceDiv.innerHTML = `
                        <a href="/company/${service[0].id}" class="company-logo-link">
                        <img src="data:image/png;base64,${service[0].companyImage}" alt="${service[0].companyName}">
                        </a>
                        <h2>${service[0].companyName}</h2>
                        <h4>${service[0].name}</h4>
                        <p>${service[0].description}</p>
                        <p><strong>Price: ${price} ${currency}</span></strong></p>
                        <h5>${service[0].address}</h5>
                        <h5>${service[1]}</h5>
                        <a href="/service/${service[0].id}" class="button">${details}</a>
                    `;
                servicesList.appendChild(serviceDiv);
            });

        })
        .catch(error => {
            console.error('Error fetching services:', error);
        });
}
window.onload = filter
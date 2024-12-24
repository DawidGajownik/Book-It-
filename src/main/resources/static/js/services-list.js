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

function sortServices(criteria) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('sort', criteria);
    window.location.search = urlParams.toString();
}

function setParam(element, params) {
    if (element.value.length > 0) {
        params.append(element.name, element.value);
    }
}

function filter() {
    // HTML elements
    const name = document.getElementById("name");
    const industry = document.getElementById("industry");
    const address = document.getElementById("address");
    const maxDistance = document.getElementById("maxDistance");
    const priceMin = document.getElementById("priceMin");
    const priceMax = document.getElementById("priceMax");

    // Collect selected industries
    const selectedIndustries = Array.from(industry.selectedOptions)
        .map(option => option.value);

    // Params to filter
    const params = new URLSearchParams();

    // Setting all params
    setParam(name, params);
    selectedIndustries.forEach(industry => params.append("industry", industry));
    setParam(address, params);
    setParam(maxDistance, params);
    setParam(priceMin, params);
    setParam(priceMax, params);

    // Filtering
    fetch(`/filter-services?${params.toString()}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error processing search');
            }
            return response.json();
        })
        .then(services => {
            const servicesList = window.services;
            servicesList.innerHTML = ''; // Clear existing HTML
            services.forEach(service => {
                const li = document.createElement('li');
                li.classList.add('service-item');
                const price = service[0].price.toFixed(2);
                li.innerHTML = `
                    <div class="service-content">
                        <a href="/company/${service[0].id}" class="company-logo-link">
                            <img src="data:image/png;base64,${service[0].companyImage}" alt="${service[0].companyName}">
                        </a>
                        <div>
                            <h4>${service[0].companyName}</h4>
                            <h5>${service[0].name}</h5>
                            <p>${service[0].description}</p>
                            <p><strong>Price: ${price} ${currency}</strong></p>
                            <p>${service[0].address}</p>
                            <p>${service[1]}</p>
                            <a href="/service/${service[0].id}" class="button">${details}</a>
                        </div>
                    </div>
                `;
                servicesList.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Error fetching services:', error);
        });
}

window.onload = filter;

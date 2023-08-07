const API = "http://127.0.0.1:8000/api/v1/geography";

function populateSelection(url, dataKey, selectionElementId) {
  fetch(url, {
    method: "GET",
    mode: "cors",
  })
    .then(response => response.json())
    .then(data => {
      const selectOptions = document.getElementById(selectionElementId);
      countries = data[dataKey];

      selectOptions.innerHTML = '';

      const defaultOption = document.createElement('option');
      defaultOption.textContent = "Select";
      defaultOption.value = "Select"
      selectOptions.appendChild(defaultOption);

      countries.forEach(item => {
        const option = document.createElement('option');
        option.textContent = item;
        option.value = item;
        selectOptions.appendChild(option);
      });
    })
    .catch(error => console.error(`Error fetching ${dataKey}:`, error));
}

function fetchCountries() {
  populateSelection(`${API}/country`, "countries", "country");
}

window.onload = fetchCountries;

function fetchSubcountries(countrySelectionEvent) {
  const country = countrySelectionEvent.target.value;
  populateSelection(`${API}/subcountry?country=${country}`, "subcountries", "subcountry");
}

document.getElementById("country").addEventListener("change", fetchSubcountries);

function fetchCities(subcountrySelectionEvent) {
  const countrySelection = document.getElementById("country");
  const country = countrySelection.options[countrySelection.selectedIndex].text;
  const subcountry = subcountrySelectionEvent.target.value;

  populateSelection(`${API}/city?country=${country}&subcountry=${subcountry}`, "cities", "city");
}

document.getElementById("country").addEventListener("change", fetchCities);
document.getElementById("subcountry").addEventListener("change", fetchCities);

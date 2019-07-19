var searchFilter = document.getElementById("search-filter");

searchFilter.onchange = function () {
	var value = this.options[this.options.selectedIndex].value;
	switch(value) {
		case "all": this.style.width = "70px"; break;
		case "theme": this.style.width = "auto"; break;
		case "messages": this.style.width = "110px"; break;
	}
}

var popupBg = document.getElementsByClassName("popup-bg")[0];
var popupHolder = document.getElementsByClassName("popup-holder")[0];

var sectorCreateBtn = document.getElementById("sector-create-btn");
var sectorCreateForm = document.getElementById("sector-create-form");
var sectorDeleteBtns = document.getElementsByClassName("sector-delete-btn");
var sectorDeleteForm = document.getElementById("sector-delete-form");
var sectorDeleteFormCloseBtn = document.getElementById("sector-delete-form-close-btn");

var boardCreateBtns = document.getElementsByClassName("board-create-btn");
var boardCreateForm = document.getElementById("board-create-form");
var boardDeleteBtns = document.getElementsByClassName("board-delete-btn");
var boardDeleteForm = document.getElementById("board-delete-form");
var boardDeleteFormCloseBtn = document.getElementById("board-delete-form-close-btn");

var boardSelect = document.getElementById("board-list");

function enablePopup() {
	popupBg.classList.remove("hide");
	popupHolder.classList.remove("hide");
}

function disablePopup() {
	popupBg.classList.add("hide");
	popupHolder.classList.add("hide");
	sectorCreateForm.classList.add("hide");
	sectorDeleteForm.classList.add("hide");
	boardCreateForm.classList.add("hide");
	boardDeleteForm.classList.add("hide");
}

sectorCreateBtn.onclick = function() {
	enablePopup();
	sectorCreateForm.classList.remove("hide");
}

function openSectorDeleteForm() {
	enablePopup();
	sectorDeleteForm.classList.remove("hide");
	var sectorId = document.getElementById("sector-id-sector-delete-form");
	sectorId.value = this.getAttribute("sectorid");
}

function openBoardCreateForm() {
	enablePopup();
	boardCreateForm.classList.remove("hide");
	var sectorId = document.getElementById("sector-id-board-create-form");
	sectorId.value = this.getAttribute("sectorid");
}

function openBoardDeleteForm() {
	loadBoardsInSector(this.getAttribute("sectorid"));
	enablePopup();
	boardDeleteForm.classList.remove("hide");
}

popupBg.onclick = function() {
	disablePopup();
}

sectorDeleteFormCloseBtn.onclick = function() {
	disablePopup();
}

for (var i = 0; i < sectorDeleteBtns.length; i++) {
	sectorDeleteBtns[i].onclick = openSectorDeleteForm;
}
for (var i = 0; i < boardCreateBtns.length; i++) {
	boardCreateBtns[i].onclick = openBoardCreateForm;
}
for (var i = 0; i < boardDeleteBtns.length; i++) {
	boardDeleteBtns[i].onclick = openBoardDeleteForm;
}

function loadBoardsInSector(sectorId) {
	for (var i = boardSelect.options.length - 1; i > -1; i--) {
		boardSelect.options.remove(i);
	}
	var response;
	var http = new XMLHttpRequest();
	http.open("GET", "/sector/getsubs/" + sectorId, true)

	http.onreadystatechange = function () {
		if (this.readyState == 4 && this.status == 200) {
			response = JSON.parse(this.responseText);
			for (var i = 0; i < response.length; i++) {
				var option = new Option(response[i].name, response[i].id, false, false);
				boardSelect.options.add(option);
			}
		}
	}

	http.send();
}
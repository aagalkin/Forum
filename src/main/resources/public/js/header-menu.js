var dropMenuTrigger = document.getElementById("forum-user-nickname");
var dropdownMenu = document.getElementById("dropdown-menu");
var dropdownMenuBtn = document.getElementById("dropdown-menu-btn");

var extraSearchButton = document.getElementById("extra-search-button");
var searchSymbol = document.getElementById("search-open");
var hr = document.getElementById("extra-search-separator");
var searchPanel = document.getElementsByClassName("search-panel")[0];
var extraSearchPanel = document.getElementById("extra-search-panel");
extraSearchButton.onclick = function() {
	searchSymbol.innerText = searchSymbol.innerText === "▼" ? "▲" : "▼";
	searchPanel.classList.toggle("open-extra-search");
	hr.classList.toggle("hide-menu");
	extraSearchPanel.classList.toggle("hide-menu");
}

dropMenuTrigger.onclick = function () {
	dropdownMenu.classList.toggle("hide-menu");
	dropdownMenuBtn.innerText = dropdownMenuBtn.innerText === "▼" ? "▲" : "▼";
}



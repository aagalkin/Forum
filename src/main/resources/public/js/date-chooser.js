var yearSelector = document.getElementById("year-selector");
var monthSelector = document.getElementById("month-selector");
var daySelector = document.getElementById("day-selector");

var leapYear = false;

var previousYear = new Date().getFullYear() - 1;

for (var i = previousYear; i != previousYear - 100; i--) {
	yearSelector.options.add(new Option(i, i, false, false));
}

var selectYear = function () {
	if (+yearSelector.options[yearSelector.selectedIndex].value % 4 == 0) {
		leapYear = true;
	} else {
		leapYear = false;
	}
	monthSelector.removeAttribute("disabled");
	monthSelector.value = "none";
	daySelector.setAttribute("disabled", "");
	daySelector.value = "none";
}

var selectMonth = function() {
	var days = 0;
	switch(+monthSelector.options[monthSelector.selectedIndex].value) {
		case 1: days = 31; break;
		case 2: days = leapYear ? 29 : 28; break;
		case 3: days = 31; break;
		case 4: days = 30; break;
		case 5: days = 31; break;
		case 6: days = 30; break;
		case 7: days = 31; break;
		case 8: days = 31; break;
		case 9: days = 30; break;
		case 10: days = 31; break;
		case 11: days = 30; break;
		case 12: days = 31; break;
	}
	daySelector.removeAttribute("disabled");
	daySelector.value = "none";
	for (var i = daySelector.options.length - 1; i > 0; i--) {
		daySelector.options.remove(i);
	}
	for (var i = 0; i < days; i++) {
		dayNum = i + 1;
		daySelector.add(new Option(dayNum, dayNum, false, false));
	}
}

yearSelector.onchange = selectYear;

monthSelector.onchange = selectMonth;
window.onload = function() {
	var userId = document.getElementById("forum-user-id");
	var http = new XMLHttpRequest();
	var path = "user/dob/" + userId.value;
	http.open("GET", path, true);

	http.onreadystatechange = function () {
		if (this.readyState == 4 && this.status == 200) {
			var response = JSON.parse(this.responseText);
			if (response == null) {return;}
			response = response.split("-");
			yearSelector.value = response[0];
			selectYear();
			monthSelector.value = response[1][0] == "0" ? response[1].substring(1) : response[1];
			selectMonth();
			daySelector.value = response[2][0] == "0" ? response[2].substring(1) : response[2];
		}
	}

	http.send();
}
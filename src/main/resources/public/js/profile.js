var btns = document.getElementsByClassName("profile-menu");

for(var i = 0; i < btns.length; i++) {
	btns[i].onclick = function() {
		var activeBtn = document.getElementsByClassName("profile-menu-active")[0];
		activeBtn.classList.remove("profile-menu-active");
		this.classList.add("profile-menu-active");
	}
}

var popupBg = document.getElementsByClassName("popup-bg")[0];
var popupHolder = document.getElementsByClassName("popup-holder")[0];

var avaBg = document.getElementById("ava-bg");
avaBg.onmouseout = function() {
	avaBg.classList.add("hidden");
}

var avatar = document.getElementById("avatar");
avatar.onmouseover = function() {
	avaBg.classList.remove("hidden")
}

avaBg.onclick = function() {
	popupBg.classList.remove("hidden");
	popupHolder.classList.remove("hidden");
}

popupBg.onclick = function() {
	popupBg.classList.add("hidden");
	popupHolder.classList.add("hidden");
}
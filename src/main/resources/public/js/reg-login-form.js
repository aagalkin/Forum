var passwordField = document.getElementById("password-field");
var login = document.getElementById("login");
var reg = document.getElementById("reg");
var password = document.getElementById("password")
var repasswordField = document.getElementById("repassword-field");
var repassword = document.getElementById("repassword");
var salt = document.getElementById("salt");

if (login != null) {
	login.onclick = function() {
		password.value = sha256(sha256(passwordField.value) + salt.value);
	}
}

if (reg != null) {
	reg.onclick = function() {
		password.value = sha256(passwordField.value);
		repassword.value = sha256(repasswordField.value);
	}
}
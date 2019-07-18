var newPassword = document.getElementById("new-password");
var renewPassword = document.getElementById("renew-password");
var oldPassword = document.getElementById("old-password");
var newPasswordField = document.getElementById("new-password-field");
var renewPasswordField = document.getElementById("renew-password-field");
var oldPasswordField = document.getElementById("old-password-field");
var salt = document.getElementById("salt");
var cp = document.getElementById("cp") //change password submit
var ce = document.getElementById("ce") //change email submit

if (cp != null) {
	cp.onclick = function() {
		newPassword.value = sha256(newPasswordField.value);
		renewPassword.value = sha256(renewPasswordField.value);
		oldPassword.value = sha256(sha256(oldPasswordField.value) + salt.value);
	}
}

if (ce != null) {
	ce.onclick = function() {
		oldPassword.value = sha256(sha256(oldPasswordField.value) + salt.value);
	}
}
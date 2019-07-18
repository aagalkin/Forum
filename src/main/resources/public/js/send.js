var repicientPanel = document.getElementsByClassName("fromto-panel")[0];
var themePanel = document.getElementsByClassName("theme-panel")[0];
var textPanel = document.getElementsByClassName("text-panel")[0];
var recipient = document.getElementById("recipient");
var theme = document.getElementById("theme");
var text = document.getElementById("text");

recipient.value = repicientPanel.innerText.replace("Кому: ", "").replace("От: ", "");
theme.value = themePanel.innerText.replace("Тема: ", "");
text.value = textPanel.innerHTML;
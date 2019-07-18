var btns = document.getElementsByClassName("btn3");
var messageBody = document.getElementById("message-body");

for (var i = 0; i < btns.length; i++) {
	btns[i].onclick = function() {
		this.classList.toggle("btn3-active");
		placeCaretAtEnd(messageBody);
		document.execCommand(this.getAttribute("value"), null, null);

	}
}

var fontColorSelector = document.getElementById("font-color-selector");
fontColorSelector.onchange = function() {
	placeCaretAtEnd(messageBody);
	document.execCommand("foreColor", null, fontColorSelector.options[fontColorSelector.selectedIndex].value);
}

function placeCaretAtEnd(el) {
    el.focus();
    if (typeof window.getSelection != "undefined"
            && typeof document.createRange != "undefined") {
        var range = document.createRange();
        range.selectNodeContents(el);
        range.collapse(false);
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
    } else if (typeof document.body.createTextRange != "undefined") {
        var textRange = document.body.createTextRange();
        textRange.moveToElementText(el);
        textRange.collapse(false);
        textRange.select();
    }
}

var submitBtn = document.getElementById("submit");
var hiddenText = document.getElementById("text");

submitBtn.onclick = function() {
	hiddenText.value = messageBody.innerHTML;
}

var wysiwygBtn = document.getElementById("wysiwyg-btn");
wysiwygBtn.onclick = function() {
	placeCaretAtEnd(messageBody);
}
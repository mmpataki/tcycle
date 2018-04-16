var keymap = {};

document.onkeypress =
document.onkeydown = function(e) {
    var keycomb =
        (e.ctrlKey  ? "CTRL + " : "") +
        (e.shiftKey ? "SHIFT + " : "") +
        e.key;
    if(keycomb in keymap) {
        e.preventDefault();
        e.stopPropagation();
        window[keymap[keycomb]]();
    }
}

function get(url) {
    var data;
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200)
            data = this.responseText;
    }
    xhttp.open("GET", url, false);
    xhttp.send();
    return data;
}

function setKeyMap() {
    var json = get("/keymap.json");
    rkeymap = JSON.parse(json);
    for(var k in rkeymap) {
        keymap[rkeymap[k]] = k;
    }
}

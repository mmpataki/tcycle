var contentbox;
var priority;
var notification;
var shortcutdiv;
var ipform;
var head;
var data = [];
var colors = ["", "#3498db", "#27ae60", "gold", "orange", "#f00"];
var r = 8;

function g(id) { return document.getElementById(id); }

function queryData(endpoint, func) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200)
            func(JSON.parse(this.responseText));
    }
    xhttp.open("GET", endpoint, true);
    xhttp.send();
}

function putAll() {
    if (data.length == 0) {
        container.innerHTML = `<center>It seems currently you don't have any tasks.<br/> Press CTRL + '+' to create one.</center>`;
        return;
    }
    container.innerHTML = "";
    for (var i = 0; i < data.length; i++) {
        put(data[i]);
    }
    window.document.title = head.innerHTML = "tcycle(" + data.length + ")";
}

function put(d) {
    container.innerHTML +=
        `
            <div class='entry' ondblclick="ppone(${d.id})" tabindex='0' id='${d.id}'>
                <svg height="${2 * r}" width="${2 * r}" style='margin-top: 5px;'>
                    <circle cx="${r}" cy="${r}" r="${r}" fill="${colors[d.priority]}" />
                </svg> 
                <span>Task #${d.id}</span>
                <span class='delete button' title='Delete Task' onclick='del(${d.id})'>&#x2716;</span><br/>
                <pre class='contentpre'>${d.content}</pre>
            </div>
        `;
}

function del(idn) {
    var obj = {
        id: +idn
    };
    queryData("/delete?" + encodeURIComponent(JSON.stringify(obj)),
        function (d) {
            if (d.status == 'failure')
                return;
            /* let's do a linear search, we don't have billions here */
            for (var i = 0; i < data.length; i++) {
                if (data[i].id == idn)
                    data.splice(i, 1);
            }
            putAll();
            notify(3, `Deleted task T#${idn}`);
        });
}

function notify(lev, mes) {
    notification.style.background = colors[lev];
    notification.innerHTML = mes;
}

function add() {
    if (contentbox.value == "") {
        notify(4, 'Content cannot be null');
        return;
    }
    var obj = {
        content: contentbox.value,
        priority: +priority.value,
        dprio: +priority.value
    };
    queryData("/put?" + encodeURIComponent(JSON.stringify(obj)),
        function (d) {
            data.push(d);
            putAll();
            notify(1, `Task#${d.id} added`);
        }
    );
}

window.onload = function () {
    contentbox = g('content');
    priority = g('priority');
    ipform = g("ipform");
    notification = g("notification");
    shortcutdiv = g("shortcutdiv");
    head = g("head");
    setKeyMap();
    queryData("/get", function (d) {
        data = d;
        putAll();
    });
}

function compare(a, b) {
    if (a.dprio > b.dprio)
        return -1;
    if (a.dprio < b.dprio)
        return 1;
    return 0;
}

function printDprio() {
    var xs = document.createElement('pre');
    for (var i = 0; i < data.length; ++i)
        xs.innerHTML += `Task #${data[i].id} => ${data[i].dprio}<br/>`;
    document.body.appendChild(xs);
}

var cfactor = 0.9;
function ppone(id) {
    /* find the ID's index */
    var i = 0;
    for (i = 0; i < data.length; i++)
        if (data[i].id == id)
            break;

    var x = {
        id : id
    };
    queryData("/resched?" + encodeURIComponent(JSON.stringify(x)),
        function (d) {
            if (d.status == "success") {
                notify(2, `Postponed Task #${id}`);
                var t = data[i];
                data.splice(i, 1);
                data.push(t);
                putAll();
            } else {
                notify(4, "Couldn't update the tasks sevrer");
            }
        });
}

/* keybinding */
function postpone() {
    ppone(document.activeElement.id);
}

/* keybinding */
function deletetask() {
    del(document.activeElement.id);
}

/* Keybinding */
function newtask() {
    ipform.style.display = ipform.style.display == "block" ? "none" : "block";
    contentbox.focus();
}

/* Keybinding */
function shortcuts() {
    shortcutdiv.style.display = shortcutdiv.style.display == "block" ? "none" : "block";
    var tc = '<center><table>';
    for (var k in keymap) {
        tc += `<tr><td align='right'><b>${k}</b></td><td>${keymap[k]}</td></tr>`;
    }
    tc += "</table></center>"
    shortcutdiv.innerHTML = tc;
}

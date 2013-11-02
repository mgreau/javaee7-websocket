var wsUrl;
var idMatch = '1234';

var appPath = window.location.pathname.split('/')[1];
var host = window.location.hostname;
var port = "8000";

if (host == 'localhost') {
	port = '8080';
}

if (window.location.protocol == 'https:') {
	port = '8443';
	wsUrl = 'wss://' + host + ':'+ port +'/' + appPath +'/matches/'+idMatch;
} else {
	wsUrl = 'ws://' + host + ':'+ port +'/' + appPath +'/matches/'+idMatch;
}
//wsUrl = 'ws://localhost:8080/usopen/matches/1234';

var socket; // websocket
var m1comments, m1title; 
// Player1 elements
var m1p1, m1p1games, m1p1sets, m1p1points, m1p1set1, m1p1set2, m1p1set3;
// Player2 elements
var m1p2, m1p2games, m1p2sets, m1p2points, m1p2set1, m1p2set2, m1p2set3;

function connect() {
	iniHtmlElements();
	createWebSocket(wsUrl);
}

function iniHtmlElements() {
	m1p1 = document.getElementById("m1-p1");
	m1p1games = document.getElementById("m1-p1-games");
	m1p1sets = document.getElementById("m1-p1-sets");
	m1p1points = document.getElementById("m1-p1-points");
	m1p1set1 = document.getElementById("m1-p1-set1");
	m1p1set2 = document.getElementById("m1-p1-set2");
	m1p1set3 = document.getElementById("m1-p1-set3");
	m1p1serve = document.getElementById("m1-p1-serve");
	// player2
	m1p2 = document.getElementById("m1-p2");
	m1p2games = document.getElementById("m1-p2-games");
	m1p2sets = document.getElementById("m1-p2-sets");
	m1p2points = document.getElementById("m1-p2-points");
	m1p2set1 = document.getElementById("m1-p2-set1");
	m1p2set2 = document.getElementById("m1-p2-set2");
	m1p2set3 = document.getElementById("m1-p2-set3");
	m1p2serve = document.getElementById("m1-p2-serve");
	// comments
	m1comments = document.getElementById("m1-comments");
	m1title = document.getElementById("m1-title");
}

function createWebSocket(host) {
	if (!window.WebSocket) {
		var spanError = document.createElement('span');
		spanError.setAttribute('class', 'alert alert-danger');
		spanError.innerHTML = "Votre navigateur ne supporte pas les WebSockets!";
		document.body.appendChild(spanError);
		return false;
	} else {
		socket = new WebSocket(host);
		socket.onopen = function() {
			document.getElementById("m1-status").innerHTML = 'CONNECTING...';
		};
		socket.onclose = function() {
			document.getElementById("m1-status").innerHTML = 'FINISHED';
		};
		socket.onerror = function() {
			document.getElementById("m1-status").innerHTML = 'ERROR - Please refresh this page';
		};
		socket.onmessage = function(msg) {
			try { 
				console.log(data);
				var obj = JSON.parse(msg.data);
				
				if (obj.hasOwnProperty("match")){
					//title
					m1title.innerHTML = obj.match.title;
					// comments
					m1comments.value = obj.match.comments;
					m1comments.scrollTop = 999999;
					// serve
					if (obj.match.serve === obj.match.players[0].name) {
						m1p1serve.innerHTML = "S";
						m1p2serve.innerHTML = "";
					} else {
						m1p1serve.innerHTML = "";
						m1p2serve.innerHTML = "S";
					}
					// player1
					m1p1.innerHTML = obj.match.players[0].name;
					m1p1games.innerHTML = obj.match.players[0].games;
					m1p1sets.innerHTML = obj.match.players[0].sets;
					m1p1points.innerHTML = obj.match.players[0].points;
					m1p1set1.innerHTML = obj.match.players[0].set1;
					m1p1set2.innerHTML = obj.match.players[0].set2;
					m1p1set3.innerHTML = obj.match.players[0].set3;
					// player2
					m1p2.innerHTML = obj.match.players[1].name;
					m1p2games.innerHTML = obj.match.players[1].games;
					m1p2sets.innerHTML = obj.match.players[1].sets;
					m1p2points.innerHTML = obj.match.players[1].points;
					m1p2set1.innerHTML = obj.match.players[1].set1;
					m1p2set2.innerHTML = obj.match.players[1].set2;
					m1p2set3.innerHTML = obj.match.players[1].set3;
					
					document.getElementById("m1-status").innerHTML = 'LIVE';
				}
				else {
					document.getElementById("m1-betmatchwinner-result").innerHTML = obj.result;
				}

			} catch (exception) {
				data = msg.data;
				console.log(data);
			}
		};
	}
}
function betMatchWinner(player) {
	var msg = "{ \"type\" : \"betMatchWinner\", \"name\" : \""
		+ player + "\", \"idMatch\" : \""
		+ idMatch + "\"}";
	console.log(msg);
	document.getElementById("m1-betmatchwinner").innerHTML = player;
	document.getElementById("m1-betmatchwinner-result").innerHTML = "";
	socket.send(msg);
}


window.addEventListener("load", connect, false);
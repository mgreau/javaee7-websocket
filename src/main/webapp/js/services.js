//Service to handle WebSocket protocol
app.factory('WebSocketService', function($window) {

	var service = {};
	service.ws = new Object();

	service.connect = function(idMatch) {
		if (service.ws[idMatch]
				&& service.ws[idMatch].readyState == WebSocket.OPEN) {
			return;
		}

		var appPath = "/";//$window.location.pathname.split('/')[1];
		var host = $window.location.hostname;
		var port = $window.location.port;
		var protocol = "ws";
		if (angular.equals($window.location.protocol,'https:')){
			protocol = "wss";
		}

		var wsUrl = protocol + '://'+ host + ':'+ port + '/' + appPath + '/matches/' + idMatch;
		var websocket = new WebSocket(wsUrl);

		websocket.onopen = function() {
			service.callback(idMatch,"CONNECTED");
		};

		websocket.onerror = function() {
			service.callback(idMatch,"Failed to open a connection" );
		};

		websocket.onclose = function() {
			service.callback(idMatch,"DISCONNECTED");
		};

		websocket.onmessage = function(message) {
			service.callback(idMatch, message.data);
		};

		service.ws[idMatch] = websocket;
	};

	// Bet on the winner of the match
	service.sendBetMatchWinner = function(idMatch, player) {
		var jsonObj = {"type" : "betMatchWinner", "name" : player};
		service.ws[idMatch].send(JSON.stringify(jsonObj));
	};

	// Close the WebSocket connection
	service.disconnect = function(idMatch) {
		service.ws[idMatch].close();
	};

	// WebSocket connection status
	service.status = function(idMatch) {
		if (service.ws == null || angular.isUndefined(service.ws[idMatch])){
			return WebSocket.CLOSED;
		}
		return service.ws[idMatch].readyState;
	};

	service.statusAsText = function(idMatch) {
		var readyState = service.status(idMatch);
		if (readyState == WebSocket.CONNECTING){
			return "CONNECTING";
		} else if (readyState == WebSocket.OPEN){
			return "OPEN";
		} else if (readyState == WebSocket.CLOSING){
			return "CLOSING";
		} else if (readyState == WebSocket.CLOSED){
			return "CLOSED";
		} else {
			return "UNKNOW";
		}
	};

	// handle callback
	service.subscribe = function(callback) {
		service.callback = callback;
	};

	return service;
});


app.factory('TournamentRESTService', function($http, $window) {
	var appPath = $window.location.pathname.split('/')[1];
	var urlRest = '/rest/tournament/lives';
	var myService = {
		    async: function() {
		      // $http returns a promise, which has a then function, which
				// also returns a promise
		      var promise = $http.get(urlRest).then(function (response) {
		        // The then function here is an opportunity to modify the
				// response
		        console.log(response);
		        // The return value gets picked up by the then in the
				// controller.
		        return response.data;
		      });
		      // Return the promise to the controller
		      return promise;
		    }
		  };
	return myService;
});

app.factory('MatchesService', function($window, WebSocketService) {
	var service = {};

 	service.whoIsTheWinner = function(match) {
        if (angular.isUndefined(match.players)){
                return null;
        }
        if (parseInt(match.players[0].sets) >
                        parseInt(match.players[1].sets)){
            return match.players[0].name;
        } else {
            return match.players[1].name;
        }
    };

    return service;

});

app.factory('BetsService', function($window, WebSocketService) {
	var service = {};

	service.betOnWinner = function(idMatch, player) {
       WebSocketService.sendBetMatchWinner(idMatch, player);
    };
	return service;
});

var myApp = angular.module('myApp', []);

myApp.factory('MatchService', function() {
	var service = {};
	service.ws = new Object();

	service.connect = function(idMatch) {
		if (service.ws[idMatch]
				&& service.ws[idMatch].readyState == WebSocket.OPEN) {
			return;
		}

		var wsUrl = 'ws://localhost:8080/usopen/' + 'matches/' + idMatch;
		var websocket = new WebSocket(wsUrl);
		var key = idMatch;

		websocket.onopen = function() {
			service.callback(key,"CONNECTED");
		};

		websocket.onerror = function() {
			service.callback(key,"Failed to open a connection" );
		};

		websocket.onclose = function() {
			service.callback(key,"DISCONNECTED");
		};

		websocket.onmessage = function(message) {
			service.callback(key, message.data);
		};

		service.ws[idMatch] = websocket;
	};

	service.send = function(message) {
		service.ws[idMatch].send(message);
	};

	// Close the WebSocket connection
	service.disconnect = function(idMatch) {
		service.ws[idMatch].close();
	};
	
	service.status = function(idMatch) {
		if (service.ws == null || angular.isUndefined(service.ws[idMatch])){
			return WebSocket.CLOSED;
		}
		return service.ws[idMatch].readyState;
	};

	//handle callback
	service.subscribe = function(callback) {
		service.callback = callback;
	};

	return service;
});

function AppCtrl($scope, $http, MatchService) {
	
	$scope.matches = [];
	$scope.lives = new Object();
	$scope.msg = new Object();

	//Load datas
	$http({
		method : 'GET',
		url : '/usopen/rest/tournament/lives'
	}).success(function(data) {
	    $scope.matches = [];
		angular.forEach(data, function(m) {
		      $scope.matches.push(m); // response data
		    });
	});

	MatchService.subscribe(function(key, message) {
		console.log(key + " : callback");
		try {
			var m = JSON.parse(message);
			$scope.lives[key] = m.match;
		} catch (exception) {
			$scope.msg[key] = message;
		}
		$scope.$apply();
	});

	$scope.connect = function(id) {
		$scope.msg[id] = "CONNECTING...";
		MatchService.connect(id);
	};

	$scope.disconnect = function(id) {
		$scope.lives[id] = "";
		MatchService.disconnect(id);
	};
	
	$scope.isOpen = function(id) {
		return angular.equals(WebSocket.OPEN, MatchService.status(id));
	};
	
	$scope.isClosed = function(id) {
		return angular.equals(WebSocket.CLOSED, MatchService.status(id));
	};
	
	$scope.getStatus = function(id) {
		if (angular.isUndefined($scope.msg[id])){
			$scope.msg[id] = "CLOSED";
		}
		return $scope.msg[id];
	};
	
	//return true if there is no players received 
	$scope.noMessage = function(id) {
		return (angular.isUndefined($scope.lives[id]) || angular.isUndefined($scope.lives[id].players));
	};

	$scope.betOnWinner = function() {
		var msg = "{ \"type\" : \"betMatchWinner\", \"name\" : \""
				+ $scope.text + "\" }";
		MatchService.send(msg);
		$scope.text = "";
	};
	
	//Utils for img source
	$scope.splitForImage = function(string, nb) {
		$scope.array = string.toLowerCase().split(' ');
		return $scope.result = $scope.array[nb];
	};
}
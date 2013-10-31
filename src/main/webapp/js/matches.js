var myApp = angular.module('myApp', []);

myApp.factory('MatchService', function() {
	var service = {};
	service.ws = new Object();

	service.connect = function(idMatch) {
//		if (service.ws) {
//			return;
//		}

		var wsUrl = 'ws://localhost:8080/wildfly-websocket/'
				+ 'matches/' + idMatch;
		var ws = new WebSocket(wsUrl);

		ws.onopen = function() {
			console.log("open ws " + wsUrl);
			service.callback("Connected to livemode");
		};

		ws.onerror = function() {
			console.log("error ws " + wsUrl);
			service.callback("Failed to open a connection");
		};

		ws.onclose = function() {
			console.log("close ws " + wsUrl);
			service.callback("Disconnected to livemode");
		};

		ws.onmessage = function(message) {
			console.log(message);
			service.callback(message.data);
		};

		service.ws[idMatch] = ws;
	};

	service.send = function(message) {
		service.ws.send(message);
	};
	
	//Close the WebSocket connection
	service.disconnect = function(idMatch) {
		service.ws[idMatch].close();
	};

	
	service.subscribe = function(callback) {
		service.callback = callback;
	};

	return service;
});

function AppCtrl($scope, $http) {
	
	$scope.splitForImage = function(string, nb) {
		$scope.array = string.toLowerCase().split(' ');
		return $scope.result = $scope.array[nb];
	};

	$http({
		method : 'GET',
		url : '/wildfly-websocket/rest/tournament/lives'
	}).success(function(data) {
		$scope.matches = data; // response data
	});
}

function MatchCtrl($scope, MatchService) {
	$scope.messages = [];
	
	MatchService.subscribe(function(message) {
	    //$scope.messages.push(message);
	    $scope.$apply();
	  });

	$scope.connect = function(id) {
		MatchService.connect(id);
	};
	
	$scope.disconnect = function(id) {
		MatchService.disconnect(id);
	};


	$scope.betOnWinner = function() {
		var msg = "{ \"type\" : \"betMatchWinner\", \"name\" : \"" + $scope.text + "\" }";
		MatchService.send(msg);
		$scope.text = "";
	};
}
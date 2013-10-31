var myApp = angular.module('myApp', []);

myApp.factory('MatchService', function() {
	var service = {};

	service.connect = function(idMatch) {
		if (service.ws) {
			return;
		}

		var wsUrl = 'ws://localhost:8080/usopen/'
				+ 'matches/' + idMatch;
		var ws = new WebSocket(wsUrl);

		ws.onopen = function() {
			service.callback("Connected to livemode");
		};

		ws.onerror = function() {
			service.callback("Failed to open a connection");
		};

		ws.onclose = function() {
			service.callback("Disconnected to livemode");
		};

		ws.onmessage = function(message) {
			console.log(message);
			service.callback(message.data);
		};

		service.ws = ws;
	};

	service.send = function(message) {
		service.ws.send(message);
	};
	
	//Close the WebSocket connection
	service.disconnect = function() {
		service.ws.close();
	};

	
	service.subscribe = function(callback) {
		service.callback = callback;
	};

	return service;
});

function AppCtrl($scope, MatchService, $http) {
	$scope.messages = [];
	
	MatchService.subscribe(function(message) {
	    //$scope.messages.push(message);
	    $scope.$apply();
	  });

	$scope.connect = function(id) {
		MatchService.connect(id);
	};
	
	$scope.disconnect = function() {
		MatchService.disconnect();
	};


	$scope.betOnWinner = function() {
		var msg = "{ \"type\" : \"betMatchWinner\", \"name\" : \"" + $scope.text + "\" }";
		MatchService.send(msg);
		$scope.text = "";
	};

	$scope.splitForImage = function(string, nb) {
		$scope.array = string.toLowerCase().split(' ');
		return $scope.result = $scope.array[nb];
	};

	$http({
		method : 'GET',
		url : '/usopen/rest/tournament/lives'
	}).success(function(data) {
		$scope.matches = data; // response data
	});
}
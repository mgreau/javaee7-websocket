var app = angular.module('app', []);

app.factory('MatchService', function() {
	var service = {};

	service.connect = function(idMatch) {
		if (service.ws) {
			return;
		}

		var wsUrl = 'ws://' + window.location.host + window.location.pathname
				+ 'matches/' + idMatch;
		var ws = new WebSocket(wsUrl);

		ws.onopen = function() {
			service.callback("Connected to livemode");
		};

		ws.onerror = function() {
			service.callback("Failed to open a connection");
		}

		ws.onclose = function() {
			service.callback("Disconnected to livemode");
		}

		ws.onmessage = function(message) {
			service.callback(message.data);
		};

		service.ws = ws;
	}

	service.send = function(message) {
		service.ws.send(message);
	}

	service.subscribe = function(callback) {
		service.callback = callback;
	}

	return service;
});

function AppCtrl($scope, MatchService, $http) {
	$scope.messages = [];

	MatchService.subscribe(function(message) {
		$scope.messages.push(message);
		$scope.$apply();
	});

	$scope.connect = function(key) {
		MatchService.connect(key);
	};

	$scope.send = function() {
		MatchService.send($scope.text);
		$scope.text = "";
	};

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
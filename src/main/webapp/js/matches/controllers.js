function TournamentCtrl($scope, MatchRESTService, MatchWebSocketService) {
	
	$scope.matches = [];
	$scope.lives = new Object();
	$scope.msg = new Object();
	
	MatchRESTService.async().then(function(d) {
	    $scope.matches = d;
	  });
	
	MatchWebSocketService.subscribe(function(idMatch, message) {
		console.log(idMatch + " : callback");
		try {
			var obj = JSON.parse(message);
			if (obj.hasOwnProperty("match")){
				$scope.lives[idMatch] = obj.match;
			}else if (obj.hasOwnProperty("betMatchWinner")){
				//todo handle betmessages;
			}
		} catch (exception) {
			$scope.msg[idMatch] = message;
		}
		$scope.$apply();
	});

	$scope.connect = function(idMatch) {
		MatchWebSocketService.connect(idMatch);
	};

	$scope.disconnect = function(idMatch) {
		$scope.lives[idMatch] = "";
		MatchWebSocketService.disconnect(idMatch);
	};
	
	$scope.isOpen = function(idMatch) {
		return angular.equals(WebSocket.OPEN, MatchWebSocketService.status(idMatch));
	};
	
	$scope.isClosed = function(idMatch) {
		return angular.equals(WebSocket.CLOSED, MatchWebSocketService.status(idMatch));
	};
	
	$scope.getStatus = function(idMatch) {
		if (angular.isUndefined($scope.msg[idMatch])){
			$scope.msg[idMatch] = "CLOSED";
		}
		return $scope.msg[idMatch];
	};
	
	//return true if there is no players received 
	$scope.noMessage = function(idMatch) {
		return (angular.isUndefined($scope.lives[idMatch]) || angular.isUndefined($scope.lives[idMatch].players));
	};
	
	
	$scope.betOnWinner = function(idMatch, player) {
		MatchWebSocketService.sendBetMatchWinner(idMatch, player);
	};
	
	$scope.isBet = function(idMatch) {
		return (angular.isUndefined($scope.lives[idMatch]) == false  
				&& angular.equals($scope.lives[idMatch].betOn,"") == false);
	};
	
	$scope.isBetOnHim = function(idMatch, player) {
		return $scope.isBet(idMatch) && angular.equals($scope.lives[idMatch].betOn, player); 
	};
	
	//Utils for img source
	$scope.splitForImage = function(string, nb) {
		$scope.array = string.toLowerCase().split(' ');
		return $scope.result = $scope.array[nb];
	};
	
}

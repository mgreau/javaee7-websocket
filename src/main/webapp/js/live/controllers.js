app.controller("TournamentCtrl", function($scope, TournamentRESTService, MatchesService, WebSocketService) {
	
	$scope.lives = new Object();

	TournamentRESTService.async().then(function(datas) {
	    	angular.forEach(datas, function(value, key){
	    		$scope.lives[value.key] = new Object();
	    		$scope.lives[value.key].key = value.key;
	    		$scope.lives[value.key].status = 'DISCONNECTED';
	    		$scope.lives[value.key].title = value.title;
	    		$scope.lives[value.key].p1 = value.playerOneName;
	    		$scope.lives[value.key].p2 = value.playerTwoName;
	    		$scope.lives[value.key].p1c = value.p1Country;
	    		$scope.lives[value.key].p2c = value.p2Country;    		
	    	});
	    	/*
			    $scope.lives['1235'] = new Object();
			    $scope.lives['1235'].key = '1235';
			    $scope.lives['1235'].status = 'CLOSED';
	    	*/
	});

	//Messages sent by peer server are handled here
	WebSocketService.subscribe(function(idMatch, message) {
		try {
			var obj = JSON.parse(message);

			//Match Live message from server
			if (obj.hasOwnProperty("match")){
				$scope.lives[idMatch].match = obj.match;
				$scope.lives[idMatch].winner = "";
				$scope.lives[idMatch].key = idMatch;

				if(obj.match.finished){
					$scope.lives[idMatch].winner = MatchesService.whoIsTheWinner(obj.match);
				} 
			} 
			//Bet Message from server
			else if (obj.hasOwnProperty("winner")){
				$scope.lives[idMatch].bet = obj;
			}

		} catch (exception) {
			//Message WebSocket lifcycle
			$scope.lives[idMatch].status = message;
			console.log(message);
		}
		$scope.$apply();
	});

	$scope.connect = function(idMatch) {
		WebSocketService.connect(idMatch);
	};

	$scope.disconnect = function(idMatch) {
	  	$scope.lives[idMatch].match = {};
        $scope.lives[idMatch].winner = "";
        $scope.lives[idMatch].bet = {};
		WebSocketService.disconnect(idMatch);
	};

	 //Utils for img source
    $scope.splitForImage = function(string, nb) {
            $scope.array = string.toLowerCase().split(' ');
            return $scope.result = $scope.array[nb];
    };

});
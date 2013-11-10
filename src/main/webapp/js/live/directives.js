app.directive("match", function(BetsService){
	return{
		restrict: 'AE',
		templateUrl: "templates/match.html",
		replace: true,
		scope: false,
		link: function (scope, element, attrs, ctrl) {
			scope.theMatch = [];
			scope.theMatchId = "";
    		scope.$watch(attrs.match, function(newVal, oldVal) {
        		scope.theMatch = newVal;
    		});

    		scope.$watch(attrs.key, function(newVal, oldVal) {
        		scope.theMatchId = newVal;
    		});

		    scope.betOn =  function(idMatch, player){
                BetsService.betOnWinner(idMatch, player);
            };

            scope.cssStyleForSet =  function(player, idSet){
	         	var cssClass = "label label-default";
                angular.forEach(scope.theMatch.players, function(p, key){
                  if (angular.equals(p.name, player.name) == false)
                          if(idSet == 1 && player.set1 > p.set1){
                                  cssClass = 'label label-success';
                          } else if(idSet == 2 && player.set2 > p.set2){
                                  cssClass = 'label label-success';
                          } else if(idSet == 3 && player.set3 > p.set3){
                                  cssClass = 'label label-success';
                          }
                }, cssClass);
	        	return cssClass;
	        };
		}
	};
});

app.directive("msg", function(MatchesService){
	return{
		restrict: 'AE',
		templateUrl: "templates/msg.html",
		replace: true,
		link: function (scope, element, attrs, ctrl) {
			scope.typeAlert = "";
			scope.finalMessage = "";
			scope.theWinner = "";

			scope.$watch(attrs.winner, function(newVal, oldVal){  
		        if (angular.isUndefined(newVal) == false){
		        	if (angular.equals(newVal, "") == false){
		        		scope.theWinner = newVal;
		        	}
		        }
		    });

    		scope.$watch(attrs.message, function(newVal, oldVal) {
    			 if (angular.isUndefined(newVal) == false){
    			 	scope.finalMessage = scope.theWinner.concat(" WINS the match. ");

                	if (angular.equals(newVal.winner, "") == false){
                        if (angular.equals(newVal.result, "OK")){
                            scope.finalMessage = scope.finalMessage.concat("CONGRATS !! You won your bet !");
                            scope.typeAlert = "success";
                        } else if (angular.equals(newVal.result, "KO")){
                            scope.finalMessage = scope.finalMessage.concat("SORRY, you've lost your bet, try again :) ");
                            scope.typeAlert = "danger";
                        } else {
                        	//scope.finalMessage = MatchesService.whoIsTheWinner(scope.theMatchId) + scope.finalMessage;
                        	scope.finalMessage = ("Next time, bet on a player to win :)");
                        	scope.typeAlert = "info";
                        }
                	}
    			 } else {
                	scope.finalMessage = MatchesService.whoIsTheWinner(scope.theMatchId) + scope.finalMessage;
                	scope.finalMessage = scope.finalMessage.concat("Next time, bet on a player to win :)");
                	scope.typeAlert = "info";
                }
    		});
		}
	};
});

app.directive("bet", function(){
	return{
		restrict: 'EA',
		templateUrl: "templates/bet.html",
		replace: true,
		scope:false,
		link: function (scope, element, attrs, ctrl) {

			scope.betOnWinnerName = "";
			scope.nbBets = 0;
			scope.betOnTheMatch = false;

			scope.$watch(attrs.bet, function(newVal, oldVal) {
    			 if (angular.isUndefined(newVal) == false && newVal.hasOwnProperty('nbBets')){
    			 	scope.nbBets = newVal.nbBets;
    			 	if (angular.equals(newVal.winner, "") == false){
    			 		scope.betOnTheMatch = true;
    			 		scope.betOnWinnerName = newVal.winner;
    			 	}
    			 } else {
    			 	scope.betOnWinnerName = "";
					scope.nbBets = 0;
					scope.betOnTheMatch = false;
    			 }
    		});
		}
	};
});

function MatchesCtrl($scope, $http)
{
$http({method: 'GET', url: '/usopen/rest/tournament/lives'}).success(function(data)
{
$scope.matches = data; // response data 
});
}
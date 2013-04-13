/**
 * Filter 
 * @author Dennis Höting
 */
var filtersModule = angular.module('filters', []);
filtersModule.filter('booleanToString', function() {
	return function(input) {
		var output = ''+input;
		
		console.log(typeof input, typeof output);
		// input is bool
		return output;
	};
});

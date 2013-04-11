/*
 * ID service modules
 */
var idServices = angular.module('idServices', []);

/*
 * Simple message id implementation
 */
idServices.factory('SimpleMessageIDService', function() {
	var messageID = 1000;
	return {
		get : function() {
			return messageID++;
		}
	};
});
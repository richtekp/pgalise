/*
 * ID service modules
 * @author Dennis Höting
 */
var idServices = angular.module('idServices', []);

/*
 * Simple message id implementation
 * @author Dennis Höting
 */
idServices.factory('SimpleMessageIDService', function() {
	var messageID = 1000;
	return {
		get : function() {
			return messageID++;
		}
	};
});
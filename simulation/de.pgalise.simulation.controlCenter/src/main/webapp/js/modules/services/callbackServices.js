/**
 * callback service 
 */
var callbackServices = angular.module('callbackServices', []);
callbackServices.factory('SimpleCallbackService', function() {
	var registry = {};
	var entryCount = 0;
	
	return {
		register : function(id, callback) {
		    console.log('registering');
			if(!registry.hasOwnProperty(id)) {
				registry[id] = callback;
				entryCount++;
				ctrl.headCtrl.$scope.loadingStatus = 'loading';
			} else {
				throw 'Attempt to register multiple callback';
			}
		},
		invoke : function(id, message) {
			if(registry.hasOwnProperty(id)) {
				ctrl.mainCtrl.$scope.$apply(function() {
					registry[id](message);
					
					delete registry[id];
					--entryCount;
					if(entryCount<1) {
						ctrl.headCtrl.$scope.loadingStatus = 'notLoading';
					}
				});
				
				return true;
			} else return false;
		},
		registry : registry
	};
});
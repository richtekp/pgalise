/*
 * Callback Service module
 * @author Dennis Höting
 */
var callbackServices = angular.module('callbackServices', []);

/*
 * Simple Implementation
 * @author Dennis Höting
 */
callbackServices.factory('SimpleCallbackService', function() {
	// registry
	var registry = {};
	
	// entry counter
	var entryCount = 0;
	
	return {
		/*
		 * Register a callback
		 * @param id message id
		 * @param callback callback to be invoked
		 */
		register : function(id, callback) {
			if(!registry.hasOwnProperty(id)) {
				// add callback to registry
				registry[id] = callback;
				entryCount++;
				
				// loading
				ctrl.headCtrl.$scope.loadingStatus = 'loading';
			} else {
				throw 'Attempt to register multiple callback';
			}
		},
		
		/*
		 * Invoke given callback
		 * @param id message id
		 * @param message server-side message
		 */
		invoke : function(id, message) {
			if(registry.hasOwnProperty(id)) {
				// Invoke
				registry[id](message);
					
				// delete from registry
				delete registry[id];
				--entryCount;
				
				// loading finished (maybe)
				if(entryCount<1) {
					ctrl.headCtrl.$scope.loadingStatus = 'notLoading';
				}

				// apply
		        ctrl.mainCtrl.applyView();
				
				return true;
			} else return false;
		},
		
		/*
		 * Return callback registry
		 */
		registry : registry
	};
});
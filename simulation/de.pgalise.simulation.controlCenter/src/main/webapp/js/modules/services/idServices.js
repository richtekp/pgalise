var idSeed = 0;
/**
 * ID Service for sensors 
 * @author Dennis Höting
 */
var idServices = angular.module('idServices', []);
idServices.factory('SimpleSensorObjectIDService', function() {
	var takenIds = [];
	return {
		get : function() {
			var id, exists;
			do {
				exists = false;
				id = idSeed++;
				angular.forEach(takenIds, function(exId) {
					if(exId === id) {
						exists = true;
					}
				});
			} while(exists);
			takenIds.push(id);
			console.log('adding '+ id);
			console.log(takenIds);
			return id;
		},
		peek : function() {
			return idSeed;
		},
		add : function(id) {
			takenIds.push(id);
		},
		takenIds : takenIds
	};
});

/**
 * ID service for events 
 */
idServices.factory('SimpleEventObjectIDService', function() {
	var takenIds = [];
	return {
		get : function() {
			var id, exists;
			do {
				exists = false;
				id = idSeed++;
				angular.forEach(takenIds, function(exId) {
					if(exId === id) {
						exists = true;
					}
				});
			} while(exists);
			takenIds.push(id);
			return id;
		},
		add : function(id) {
			takenIds.push(id);
		},
		peek : function() {
			return idSeed;
		},
		takenIds : takenIds
	};
});

/**
 * ID service for messages
 */
idServices.factory('SimpleMessageIDService', function() {
	var takenIds = [];
	var currentId = 0;
	return {
		get : function() {
			var id, exists;
			do {
				exists = false;
				id = currentId++;
				angular.forEach(takenIds, function(exId) {
					if(exId === id) {
						exists = true;
					}
				});
			} while(exists);
			takenIds.push(id);
			return id;
		},
		add : function(id) {
			takenIds.push(id);
		},
		peek : function() {
			return idSeed;
		},
		takenIds : takenIds
	};
});

/**
 * UUID Service
 */
idServices.factory('SimpleUUIDService', function() {
	//00000000-0000-0000-0000-000000000001
	var takenUuids = [];
	var currentUUIDSeed = 1;
	var uuidBase='00000000-0000-0000-0000-';
	var uuidTopPattern = '000000000000';  // max number of zero fill ever asked for
	
	function zeroFill(number) {
	    var input = number + "";  // make sure it's a string
	    return(uuidTopPattern.slice(0, 12 - input.length) + input);
	}
	
	return {
		get : function() {
			var exists;
			do {
				exists = false;
				var uuid = uuidBase + zeroFill(currentUUIDSeed++);
				angular.forEach(takenUuids, function(exUuid) {
					if(uuid === exUuid) {
						exists = true;
					}
				});
			} while(exists);
			takenUuids.push(uuid);
			return uuid;
		},
		add : function(uuid) {
			takenUuids.push(uuid);
		},
		peek : function() {
			return uuidBase + zeroFill(currentUUIDSeed);
		},
		takenUUIDs : takenUuids
	};
});
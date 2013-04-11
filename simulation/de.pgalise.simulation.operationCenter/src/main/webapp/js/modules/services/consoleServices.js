/*
 * Console service module
 */
var consoleServices = angular.module('consoleServices', []);

/*
 * Simple implementation
 */
consoleServices.factory('SimpleConsoleService', function() {
	// listeners
	var listeners = [];
	
	// content of console
	var content = '';
	
	return {
		/*
		 * Register a scope and object
		 * @param scope Scope
		 * @param contentString Reference name to object
		 */
		register : function(scope, contentString) {
			// add to listeners
			listeners.push({
				scope:scope,
				content:contentString
				});
			
			// set content 
			scope[contentString] = content;
		},
		
		/*
		 * Push a line into console
		 */
		pushLine : function(entry) {
			content += new Date().secondBased() + ': ' + entry + '\n';
			angular.forEach(listeners, function(listener) {
				listener.scope[listener.content] += new Date().secondBased() + ': ' + entry + '\n';
			});
		},
		
		/*
		 * Push an object into console.
		 * This will result in a tree-like string-representation in console.
		 */
		pushObject : function(comment, entryObj) {
			var depth = 0;
			function writeObjectRepresentation(object) {
				var result = '';
				for(var prop in object) {
					var margin = '';
					for(var i=0;i<depth;i++){
						margin += '  ';
					}
					if(typeof object[prop] === 'object') {
						depth++;
						result += margin + prop + ' : {\n';
						result += writeObjectRepresentation(object[prop]);
						result += margin + '}\n';
						depth--;
					} else if(typeof object[prop] === 'function') {
						result += margin + prop + ' : [function]\n';
					} else {
						result += margin + prop + ' : ' + object[prop] + '\n';
					}
				}
				return result;
			};
			
			var string = writeObjectRepresentation(entryObj);
			var newEntry = new Date().secondBased() + ': ' + comment + '\n' + string + '----------------------------------------\n';
			content = newEntry + content;
			angular.forEach(listeners, function(listener) {
				listener.scope[listener.content] = newEntry + listener.scope[listener.content];
			});
		},
		
		/*
		 * Return content
		 */
		content : content
	};
});

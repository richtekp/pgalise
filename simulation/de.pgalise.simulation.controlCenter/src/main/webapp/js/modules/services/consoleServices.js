/**
 * Service for the output console
 * @author Dennis Höting
 */
var consoleServices = angular.module('consoleServices', []);
consoleServices.factory('SimpleConsoleService', function() {
	var listeners = [];
	var content = '';
	return {
		register : function(scope, contentString) {
			listeners.push({
				scope:scope,
				content:contentString
				});
			scope[contentString] = content;
		},
		pushLine : function(entry) {
			content += new Date().secondBased() + ': ' + entry + '\n';
			angular.forEach(listeners, function(listener) {
				listener.scope[listener.content] += new Date().secondBased() + ': ' + entry + '\n';
			});
		},
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
		content : content
	};
});

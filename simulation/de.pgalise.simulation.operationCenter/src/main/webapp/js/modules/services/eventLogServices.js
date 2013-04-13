/*
 * EventLog service module
 * @author Dennis Höting
 */
var eventLogServices = angular.module('eventLogServices', []);

/*
 * Simple implementation
 * @author Dennis Höting
 */
eventLogServices.factory('SimpleEventLogService', function() {
		// listeners to eventLog
        var listeners = [];
        
        // messages in eventlog
        var messages = [];
        
        // messageCounter
        var counter = 0;
        return {
        	/*
        	 * Register a scope with object
        	 * @param scope Scope
        	 * @param contentArrayName object reference
        	 */
            register : function(scope, contentArrayName) {
                if(typeof scope === 'undefined' 
                || typeof contentArrayName === 'undefined' 
                || typeof scope[contentArrayName] === 'undefined') {
                    return;
                }
                
                listeners.push({
                        scope : scope,
                        contentArrayName : contentArrayName
                    });
                scope[contentArrayName] = this.content;
            },
            
            /*
             * Push new message into message array
             * @param type Type of message
             * @param text Message
             */
            push : function(type, text) {
                var newEntry = {
                        timestamp : new Date().getTime(),
                        type : type,
                        text : text
                    };
                var toPush = {};
                toPush[counter] = newEntry;
                angular.forEach(listeners, function(listener) {
                    listener.scope[listener.contentArrayName].push(newEntry);
                    
                    if (!listener.scope.$$phase) {
                    	listener.scope.$apply();
    				}
                });
                counter++;
            },
            
            /*
             * Clear messages
             */
            clear : function() {
                messages = [];  
                angular.forEach(listeners, function(listener) {
                    listener.scope[listener.contentArrayName] = [];
                });
                counter = 0;  
            },
            
            /*
             * Available types
             */
            types : {
                ERROR : 0,
                NOTIFICATION : 1
            },
            
            /*
             * TypeNames
             */
            getName : function(type) {
                switch(type) {
                    case this.types.ERROR: return 'Error';
                    case this.types.NOTIFICATION: return 'Notification';
                }
            },
            
            /*
             * Return content
             */
            content : messages
        };
    });


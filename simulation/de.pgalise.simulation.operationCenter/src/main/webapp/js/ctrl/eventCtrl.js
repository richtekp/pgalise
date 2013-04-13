/*
 * Controller for event tab view
 * 
 * @param $scope Scope
 * @param EventLogService Injected service for eventLog
 * @author Dennis Höting
 * 
 * Events will be pushed into event log and eventLog will notify registered scopes
 */
function EventCtrl($scope, EventLogService) {
    this.$scope = $scope;
    
    // Amount of messages to be shown
    this.$scope.limit = 10;

    // messages
    this.$scope.messages = [];

    // Register at eventLogService
    EventLogService.register(this.$scope, 'messages');

    // make types available in view
    this.$scope.messageType = EventLogService.types;
    
    // clear
    this.$scope.clear = function() {
        EventLogService.clear();
    };
}

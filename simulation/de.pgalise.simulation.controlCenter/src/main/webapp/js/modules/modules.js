/**
 * Module which contains all given services as a factory
 * @author Dennis Höting
 */
var appModule = angular.module('app', ['directives', 
                                       'filters', 
                                       'callbackServices', 
                                       'consoleServices', 
                                       'idServices', 
                                       'mapServices', 
                                       'popupServices', 
                                       'servletCommunicationServices']).
	factory('MapService', function(OpenLayersService) {
		return OpenLayersService;
	}).
	factory('ServletCommunicationService', function(WebSocketService) {
		return WebSocketService;
	}).
	factory('PopupService', function(SimplePopupService) {
		return SimplePopupService;
	}).
	factory('ConsoleService', function(SimpleConsoleService) {
		return SimpleConsoleService;
	}).
	factory('CallbackService', function(SimpleCallbackService) {
		return SimpleCallbackService;
	}).
	factory('MessageIDService', function(SimpleMessageIDService) {
		return SimpleMessageIDService;
	}).
	factory('SensorObjectIDService', function(SimpleSensorObjectIDService) {
		return SimpleSensorObjectIDService;
	}).
	factory('EventObjectIDService', function(SimpleEventObjectIDService) {
		return SimpleEventObjectIDService;
	}).
	factory('UUIDService', function(SimpleUUIDService) {
		return SimpleUUIDService;
	});

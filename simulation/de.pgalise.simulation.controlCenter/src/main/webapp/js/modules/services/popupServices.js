/**
 * Pop-up service  
 */
var popupServices = angular.module('popupServices', []);
popupServices.factory('SimplePopupService', function($http, $compile) {
    var popupService = {};

    // Get the pop-up
    function getPopup(layerID, create) {
        if ( typeof layerID === 'undefined')
            throw "serviceModule.js:PopupService.getPopup(layerId, create) | layerID is undefined";
        if ( typeof create !== 'undefined') {
            if ($('div.dialog.' + layerID).length > 0) {
                $('div.dialog.' + layerID).each(function() {
                    $(this).remove();
                });
            }
            $('<div class="dialog ' + layerID + '"></div>').appendTo('body');
        }
        $('div.dialog.' + layerID).css('z-index', 3000);
        return $('div.dialog.' + layerID);
    }

    // Loads the pop-up
    popupService.openInitialDialog = function() {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('initial', true);

        $http.get('partials/dialogs/initDialog.html').success(function(data) {
            popup.html(data);
            popup.dialog({
                autoOpen : true,
                modal : true,
                closeOnEscape : false,
                draggable : false,
                resizable : false,
                width : 600,
                title : 'Initial dialog',
                zindex : 1500
            });
            popup.parent().find('a.ui-dialog-titlebar-close').css('display', 'none');

            $compile(popup)(scope);
        });
    };

	/**
	 * Opens the pop-up based on the given sensor 
	 * @param {Object} sensor
	 */
    popupService.openSensorDialog = function(sensor) {
        if ( typeof sensor === 'undefined')
            throw 'PopupService.openSensorDialog(sensor): sensor is undefined';
        var _this = this;
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('sensor' + sensor.id, true);

        var mapService = angular.element(document).injector().get('MapService');
        
        switch(sensor.type) {
            case model.SensorType.TRAFFIC_LIGHT_INTERSECTION:
                $http.get('partials/dialogs/sensorDialogs/trafficLightDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        title : 'Sensor details for ' + sensor.name,
                        close : function() {
                            mapService.unselect(sensor.id);
                        },
                        buttons : {
                            Delete : function() {
                                mapService.removeSensor(sensor.id);
                                _this.close('sensor' + sensor.id);
                            },
                            Ok : function() {
                                ctrl.sensorDialogCtrl.$scope.sensorInFocus = undefined;
                                _this.close('sensor' + sensor.id);
                            }
                        },
                        zIndex : 1500
                    });
                    $compile(popup)(scope);
                    ctrl.sensorDialogCtrl.$scope.sensorInFocus = sensor;
                });
                break;
            case model.SensorType.WINDPOWERSENSOR:
                $http.get('partials/dialogs/sensorDialogs/windPowerSensorDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        title : 'Sensor details for ' + sensor.name,
                        close : function() {
                            mapService.unselect(sensor.id);
                        },
                        buttons : {
                            Delete : function() {
                                _this.close('sensor' + sensor.id);
                                mapService.removeSensor(sensor.id);
                            },
                            Ok : function() {
                                ctrl.sensorDialogCtrl.$scope.sensorInFocus = undefined;
                                _this.close('sensor' + sensor.id);
                            }
                        },
                        zIndex : 1500
                    });
                    $compile(popup)(scope);
                    ctrl.sensorDialogCtrl.$scope.sensorInFocus = sensor;
                });
                break;
            case model.SensorType.PHOTOVOLTAIK:
                $http.get('partials/dialogs/sensorDialogs/photovoltaikDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        title : 'Sensor details for ' + sensor.name,
                        close : function() {
                            mapService.unselect(sensor.id);
                        },
                        buttons : {
                            Delete : function() {
                                _this.close('sensor' + sensor.id);
                                mapService.removeSensor(sensor.id);
                            },
                            Ok : function() {
                                ctrl.sensorDialogCtrl.$scope.sensorInFocus = undefined;
                                _this.close('sensor' + sensor.id);
                            }
                        },
                        zIndex : 1500
                    });
                    $compile(popup)(scope);
                    ctrl.sensorDialogCtrl.$scope.sensorInFocus = sensor;
                });
                break;
            case model.SensorType.INDUCTIONLOOP:
                $http.get('partials/dialogs/sensorDialogs/inductionLoopDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        title : 'Sensor details for ' + sensor.name,
                        close : function() {
                            mapService.unselect(sensor.id);
                        },
                        buttons : {
                            Delete : function() {
                                _this.close('sensor' + sensor.id);
                                mapService.removeSensor(sensor.id);
                            },
                            Ok : function() {
                                ctrl.sensorDialogCtrl.$scope.sensorInFocus = undefined;
                                _this.close('sensor' + sensor.id);
                            }
                        },
                        zIndex : 1500
                    });
                    $compile(popup)(scope);
                    ctrl.sensorDialogCtrl.$scope.sensorInFocus = sensor;
                });
                break;
            case model.SensorType.WEATHER_STATION:
                $http.get('partials/dialogs/sensorDialogs/weatherStationDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        title : 'Weather station ' + sensor.name,
                        close : function() {
                            mapService.unselect(sensor.id);
                        },
                        buttons : {
                            Delete : function() {
                                _this.close('sensor' + sensor.id);
                                mapService.removeSensor(sensor.id);
                            },
                            Ok : function() {
                                ctrl.sensorDialogCtrl.$scope.sensorInFocus = undefined;
                                _this.close('sensor' + sensor.id);
                            }
                        },
                        zIndex : 1500
                    });
                    $compile(popup)(scope);
                    ctrl.sensorDialogCtrl.$scope.sensorInFocus = sensor;
                });
                break;
            case model.SensorType.SMARTMETER:
                $http.get('partials/dialogs/sensorDialogs/smartMeterDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        title : 'Sensor details for ' + sensor.name,
                        close : function() {
                            mapService.unselect(sensor.id);
                        },
                        buttons : {
                            Delete : function() {
                                _this.close('sensor' + sensor.id);
                                mapService.removeSensor(sensor.id);
                            },
                            Ok : function() {
                                ctrl.smartMeterDialogCtrl.$scope.sensorInFocus = undefined;
                            	mapService.drawRadius(sensor);
                                _this.close('sensor' + sensor.id);
                            }
                        },
                        zIndex : 1500
                    });
                    $compile(popup)(scope);
                    ctrl.smartMeterDialogCtrl.$scope.sensorInFocus = sensor;
                });
                break;
            case model.SensorType.TOPORADAR:
                $http.get('partials/dialogs/sensorDialogs/topoRadarDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        title : 'Sensor details for ' + sensor.name,
                        close : function() {
                            mapService.unselect(sensor.id);
                        },
                        buttons : {
                            Delete : function() {
                                _this.close('sensor' + sensor.id);
                                mapService.removeSensor(sensor.id);
                            },
                            Ok : function() {
                                ctrl.sensorDialogCtrl.$scope.sensorInFocus = undefined;
                            	mapService.drawRadius(sensor);
                                _this.close('sensor' + sensor.id);
                            }
                        },
                        zIndex : 1500
                    });
                    $compile(popup)(scope);
                    ctrl.sensorDialogCtrl.$scope.sensorInFocus = sensor;
                });
                break;
            default:
                this.close(sensor.id);
        }
    };
	
	/**
	 * Opens the pop-up based on the given event 
 	 * @param {Object} eventObject
	 */
    popupService.openEventDialog = function(eventObject) {
        if ( typeof eventObject === 'undefined')
            throw 'PopupService.openEventDialog(eventObject): eventObject is undefined';
        var _this = this;
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('event' + eventObject.id, true);

        var mapService = angular.element(document).injector().get('MapService');
        switch(eventObject.type) {
            case model.EventType.PATH:
                $http.get('partials/dialogs/eventDialogs/pathDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        width : 550,
                        height : 485,
                        title : 'Path',
                        close : function() {
                            mapService.unselect(eventObject.start.id);
                            mapService.unselect(eventObject.end.id);
                        },
                        buttons : {
                            Delete : function() {
                                _this.close('event' + eventObject.id);
                                ctrl.commitCtrl.$scope.$apply();
                                mapService.removeEventObject(eventObject.end.id);
                            },
                            Ok : function() {
                                ctrl.pathDialogCtrl.$scope.path = undefined;
                                _this.close('event' + eventObject.id);
                            }
                        },
                        zIndex : 1500
                    });
                    $compile(popup)(scope);
                    ctrl.pathDialogCtrl.$scope.path = eventObject;
                });
                break;
            case model.EventType.ATTRACTION:
                $http.get('partials/dialogs/eventDialogs/attractionDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        width : 570,
                        height : 410,
                        title : 'Attraction',
                        close : function() {
                            mapService.unselect(eventObject.id);
                        },
                        buttons : {
                            Delete : function() {
                                _this.close('event' + eventObject.id);
                                ctrl.commitCtrl.$scope.$apply();
                                mapService.removeEventObject(eventObject.id);
                            },
                            Ok : function() {
                                ctrl.attractionDialogCtrl.$scope.attraction = undefined;
                                _this.close('event' + eventObject.id);
                            }
                        },
                        zIndex : 1500
                    });
                    $compile(popup)(scope);
                    ctrl.attractionDialogCtrl.$scope.attraction = eventObject;
                });
                break;
            case model.EventType.STREETBLOCK:
                $http.get('partials/dialogs/eventDialogs/streetBlockDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        title : 'Street Block',
                        close : function() {
                        	mapService.unselect(eventObject.id);
                        },
                        buttons : {
                            Delete : function() {
                                _this.close('event' + eventObject.id);
                                ctrl.commitCtrl.$scope.$apply();
                                mapService.removeEventObject(eventObject.id);
                            },
                            Ok : function() {
                                ctrl.streetBlockDialogCtrl.$scope.streetBlock = undefined;
                                _this.close('event' + eventObject.id);
                            }
                        },
                        zIndex : 1500,
                        width : 550
                    });
                    $compile(popup)(scope);
                    ctrl.streetBlockDialogCtrl.$scope.streetBlock = eventObject;
                });
            	break;
            case model.EventType.ENERGYEVENT:
                $http.get('partials/dialogs/eventDialogs/energyEventDialog.html').success(function(data) {
                    popup.html(data);
                    popup.dialog({
                        autoOpen : true,
                        modal : false,
                        closeOnEscape : true,
                        draggable : true,
                        resizable : false,
                        title : 'Energy Event',
                        close : function() {
                        	mapService.unselect(eventObject.id);
                        },
                        buttons : {
                            Delete : function() {
                                _this.close('event' + eventObject.id);
                                ctrl.commitCtrl.$scope.$apply();
                                mapService.removeEventObject(eventObject.id);
                            },
                            Ok : function() {
                                ctrl.energyEventDialogCtrl.$scope.energyEvent = undefined;
                            	mapService.drawRadius(eventObject);
                                _this.close('event' + eventObject.id);
                            }
                        },
                        zIndex : 1500,
                        width : 550
                    });
                    $compile(popup)(scope);
                    ctrl.energyEventDialogCtrl.$scope.energyEvent = eventObject;
                });
            	break;
            default:
                this.close(eventObject.id);
        }
    };

	/**
	 * Opens a pop-up with the given message 
 	 * @param {Object} message
	 */
    popupService.openMessageDialog = function(message) {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('message' + new Date().getTime(), true);
        $http.get('partials/dialogs/messageDialog.html').success(function(data) {
            popup.html(data);
            popup.dialog({
                autoOpen : true,
                modal : false,
                closeOnEscape : true,
                draggable : true,
                resizable : false,
                title : 'Message',
                buttons : {
                    Ok : function() {
                        $(this).dialog("close");
                    }
                },
                zIndex : 1500
            });

            $compile(popup)(scope);
            ctrl.messageDialogCtrl.$scope.message = message;
        });
    };

	/**
	 * Opens a console pop-up  
	 */
    popupService.openConsoleDialog = function() {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('console', true);

        $http.get('partials/dialogs/consoleDialog.html').success(function(data) {
            popup.html(data);
            popup.dialog({
                autoOpen : true,
                modal : false,
                closeOnEscape : true,
                draggable : true,
                resizable : false,
                title : 'Console',
                width : 760,
                height : 385,
                buttons : {
                    Ok : function() {
                        $(this).dialog("close");
                    }
                },
                zIndex : 1500
            });
            $compile(popup)(scope);
        });
    };
    
    /**
     * opens the settings dialog 
     */
    popupService.openSettingsDialog = function() {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('settings', true);

        $http.get('partials/dialogs/settingsDialog.html').success(function(data) {
            popup.html(data);
            popup.dialog({
                autoOpen : true,
                modal : false,
                closeOnEscape : true,
                draggable : true,
                resizable : false,
                title : 'Settings',
                width : 300,
                height : 200,
                buttons : {
                    Ok : function() {
                        $(this).dialog("close");
                    }
                },
                zIndex : 1500
            });
            $compile(popup)(scope);
        });
    };

    popupService.openLinkDialog = function(fileName) {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('link', true);

        $http.get('partials/dialogs/linkDialog.html').success(function(data) {
            popup.html(data);
            popup.dialog({
                modal : true,
                closeOnEscape : true,
                draggable : true,
                resizable : false,
                title : 'Your link',
                zIndex : 1500
            });
            $compile(popup)(scope);
            ctrl.linkDialogCtrl.$scope.link.url = document.location.toString() + fileName;
            ctrl.linkDialogCtrl.$scope.link.name = 'start parameter as xml';
        });
    };

	/**
	 * Opens a confirmation dialog 
	 */
    popupService.openConfirmationDialog = function(message, buttons) {
        if ( typeof message === 'undefined')
            throw 'serviceModule.js:PopupService.openConfirmationDialog(message, buttons) | message is undefined';
        if ( typeof buttons === 'undefined')
            throw 'serviceModule.js:PopupService.openConfirmationDialog(message, buttons) | buttons is undefined';

        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('confirmation', true);

        $http.get('partials/dialogs/confirmationDialog.html').success(function(data) {
            popup.html(data);
            popup.dialog({
                resizable : false,
                closeOnEscape : true,
                draggable : true,
                modal : true,
                buttons : buttons,
                zIndex : 1500
            });
            $compile(popup)(scope);
            ctrl.confirmationDialogCtrl.$scope.message = message;
        });
    };

	/**
	 * closes the pop-up based on the layer-id 
	 * @param {Object} layerId
	 */
    popupService.close = function(layerId) {
        var popup = getPopup(layerId);
        if (popup) {
            popup.dialog('close');
        }
    };

    return popupService;
});

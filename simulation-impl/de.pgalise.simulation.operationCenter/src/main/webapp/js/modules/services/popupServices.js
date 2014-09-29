/*
 * Popup services
 * @author Dennis Höting
 */
var popupServices = angular.module('popupServices', []);

/*
 * Simple implementation
 * @param $http AngularService for AJAX http loading
 * @param $compile AngularService for compiling purposes
 * @author Dennis Höting
 */
popupServices.factory('SimplePopupService', function($http, $compile) {
	// Package
    var popupService = {};

    /* 
     * Get the popup
     * @param layerId layerId of the html-element (<div>) to be created
     * @param create create new dialog
     */
    function getPopup(layerID, create) {
        if ( typeof layerID === 'undefined')
            throw "serviceModule.js:PopupService.getPopup(layerId, create) | layerID is undefined";
        if ( typeof create !== 'undefined') {
            if ($('div.dialog.' + layerID).length > 0) {
                $('div.dialog.' + layerID).each(function() {
                    $(this).remove();
                });
            }
            $('<div class="dialog ' + layerID + '"></div>').prependTo('body');
        }
        return $('div.dialog.' + layerID);
    }

    /*
     * Open console dialog
     */
    popupService.openConsoleDialog = function() {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('consoleDialog', true);

        /*
         * Request html
         */
        $http.get('partials/dialogs/consoleDialog.html').success(function(data) {
        	/*
        	 * Write html into popup, 
        	 * invoke JQuery UI constructor and
        	 * compile (Angular)
        	 */
            popup.html(data);
            popup.dialog({
                autoOpen : true,
                modal : false,
                closeOnEscape : true,
                draggable : true,
                resizable : false,
                title : 'Console',
                width : 670,
                height : 250,
                zIndex : 3000
            });
            $compile(popup)(scope);
        });
    };

    /*
     * Console simulation info dialog
     */
    popupService.openSimulationInfoDialog = function() {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('simulationInfo', true);

        $http.get('partials/dialogs/simulationInfoDialog.html').success(function(data) {
        	/*
        	 * Write html into popup, 
        	 * invoke JQuery UI constructor and
        	 * compile (Angular)
        	 */
            popup.html(data);
            popup.dialog({
                autoOpen : true,
                modal : false,
                closeOnEscape : true,
                draggable : true,
                resizable : true,
                title : 'Simulation Information',
                zIndex : 3000
            });
            $compile(popup)(scope);
        });
    };

    /*
     * Open cognos dialog
     */
    popupService.openCognosDialog = function() {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('cognos', true);

        $http.get('partials/dialogs/CognosDialog.html').success(function(data) {
        	/*
        	 * Write html into popup, 
        	 * invoke JQuery UI constructor and
        	 * compile (Angular)
        	 */
            popup.html(data);
            popup.dialog({
                autoOpen : true,
                modal : false,
                closeOnEscape : true,
                draggable : true,
                resizable : true,
                title : 'Cognos Dialog',
                width : 1000,
                height : 600,
                zIndex : 3000
            });
            $compile(popup)(scope);
        });
    };

    /*
     * Open config gate dialog
     */
    popupService.openConfigGatePopup = function() {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('gate', true);

        $http.get('partials/dialogs/configGateDialog.html').success(function(data) {
        	/*
        	 * Write html into popup, 
        	 * invoke JQuery UI constructor and
        	 * compile (Angular)
        	 */
            popup.html(data);
            popup.dialog({
                autoOpen : true,
                modal : false,
                closeOnEscape : true,
                draggable : true,
                resizable : true,
                title : 'Configure gate',
                width : 500,
                zIndex : 3000,
                position : { at: 'top'}
            });
            $compile(popup)(scope);
        });
    };

    /*
     * Open confirmation dialog
     */
    popupService.openConfirmationDialog = function(message, buttons) {
        if ( typeof message === 'undefined')
            throw 'serviceModule.js:PopupService.openConfirmationDialog(message, buttons) | message is undefined';
        if ( typeof buttons === 'undefined')
            throw 'serviceModule.js:PopupService.openConfirmationDialog(message, buttons) | buttons is undefined';

        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('confirmation', true);

        $http.get('partials/dialogs/confirmationDialog.html').success(function(data) {
        	/*
        	 * Write html into popup, 
        	 * invoke JQuery UI constructor,
        	 * compile (Angular) and
        	 * set message
        	 */
            popup.html(data);
            popup.dialog({
                resizable : false,
                closeOnEscape : true,
                draggable : true,
                modal : true,
                buttons : buttons,
                zIndex : 3000
            });
            $compile(popup)(scope);
            ctrl.confirmationDialogCtrl.$scope.message = message;
        });
    };

    /*
     * Open message dialogs
     */
    popupService.openMessageDialog = function(message, title) {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('message' + new Date().getTime(), true);
        var myTitle = title || 'Message';
        
        $http.get('partials/dialogs/messageDialog.html').success(function(data) {
        	/*
        	 * Write html into popup, 
        	 * invoke JQuery UI constructor,
        	 * compile (Angular) and
        	 * set messages
        	 */
            popup.html(data);
            popup.dialog({
                autoOpen : true,
                modal : false,
                closeOnEscape : true,
                draggable : true,
                resizable : true,
                title : myTitle,
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
    
    /*
     * Open detail dialog
     */
    popupService.openDetailDialog = function(sensorInDetail) {
        var scope = ctrl.mainCtrl.$scope;
        var popup = getPopup('detail' + sensorInDetail.id, true);
        var myTitle = sensorInDetail.name;
        $http.get('partials/dialogs/detailsDialog.html').success(function(data) {
        	/*
        	 * Write html into popup, 
        	 * invoke JQuery UI constructor,
        	 * compile (Angular) and
        	 * set sensorInDetail
        	 */
            popup.html(data);
            popup.dialog({
                autoOpen : true,
                modal : false,
                closeOnEscape : true,
                draggable : true,
                resizable : true,
                title : myTitle,
                buttons : {
                    Close : function() {
                        $(this).dialog("close");
                    }
                },
                zIndex : 1500,
                height: 400
            });

            $compile(popup)(scope);
            ctrl.detailsDialogCtrl.$scope.sensorInDetail = sensorInDetail;
        });
    };

    /*
     * Close dialog
     * @param layerId layerId of the popup
     */
    popupService.close = function(layerId) {
        var popup = getPopup(layerId);
        if (popup) {
            popup.dialog('close');
        }
    };

    return popupService;
});

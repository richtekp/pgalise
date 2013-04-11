(function(model) {
    /*
     * Base Class of all model elements
     * Children: DynamicMarker, StaticMarker
     */
    var OCMarker = (function() {
        function OCMarker(id, latitude, longitude, name, updateSteps) {
            var _this = this;

            if ( typeof OCMarker.mapService === 'undefined')
                OCMarker.mapService = angular.element(document).injector().get('MapService');
            if ( typeof OCMarker.popupService === 'undefined')
                OCMarker.popupService = angular.element(document).injector().get('PopupService');
            this.id = id;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.icon = undefined;
            this.className = 'OCMarker';
            this.isShown = false;
            this.updateSteps = updateSteps;
        }


        OCMarker.mapService = undefined;

        OCMarker.prototype.setPosition = function(lat, lng) {
            this.latitude = lat;
            this.longitude = lng;
            OCMarker.mapService.updateMarker(this);
        };
        OCMarker.prototype.show = function() {
            this.isShown = true;
            OCMarker.mapService.show(this.id);
        };
        OCMarker.prototype.hide = function() {
            this.isShown = false;
            OCMarker.mapService.hide(this.id);
        };

        return OCMarker;
    })();
    model.OCMarker = OCMarker;

    /*
     * Base Class for StaticMarkers
     * Parent: OCMarker
     * Children: TrafficLight, InductionLoop
     */
    var StaticMarker = (function(_super) {
        __extends(StaticMarker, _super);

        function StaticMarker(id, latitude, longitude, name, updateSteps) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            this.className = 'StaticMarker';
            this.nodeId = -1;
        }

        return StaticMarker;
    })(model.OCMarker);
    model.StaticMarker = StaticMarker;
})(model);

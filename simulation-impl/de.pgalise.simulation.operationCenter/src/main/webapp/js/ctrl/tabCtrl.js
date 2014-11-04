/*
 * Controller for tab navigation
 * 
 * @param $scope Scope
 * @param $element HTML-Element
 * @author Dennis Höting
 */
function TabCtrl($scope, $element) {
    var _this = this;
    ctrl.tabCtrl = this;
    this.$scope = $scope;
    this.$element = $element;

    /*
     * URLs
     */
    this.$scope.contentURLs = [
        'partials/content/gate.html', 
        'partials/content/list.html', 
        'partials/content/details.html', 
        'partials/content/event.html'];

    /*
     * Switch
     */
    this.switchTo = function(tabName) {
        var id = undefined;
        switch(tabName) {
            case 'gate':
                id = 0;
                break;
            case 'list':
                id = 1;
                break;
            case 'details':
                id = 2;
                break;
            case 'favorites':
                id = 3;
                break;
            case 'event':
                id = 4;
                break;
        }
        $(_this.$element).tabs("option", "active", id);
    };
}
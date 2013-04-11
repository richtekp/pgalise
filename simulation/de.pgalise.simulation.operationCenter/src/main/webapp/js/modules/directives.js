/*
 * Directives
 */
var directivesModule = angular.module('directives', []);

/*
 * Slider (JQuery UI)
 */
directivesModule.directive('slider', function() {
    return function(scope, element, attrs) {
        $(document).ready(function() {
            element.slider({
                min: parseInt(attrs.min),
                max: attrs.max,
                value: parseInt(scope[attrs.ngModel]),
                slide: function(event, ui) {
                    var modelPath = attrs.ngModel;
                    putObject(modelPath, scope, parseInt(ui.value));
                    scope.$apply();
                }
            });
            
            scope.$watch(attrs.ngModel, function(value) {
                element.slider('value',parseInt(value));
            });
        });
    };
});

/*
 * Button (JQuery UI)
 */
directivesModule.directive('button', function() {
	return function(scope, element, attrs) {
		$(document).ready(function() {
			element.button();
			
			scope.$watch(attrs.ngDisabled, function(value) {
		    	element.button('refresh');
		    });
		});
	};
});

/*
 * TreeView (JQuery TreeView)
 */
directivesModule.directive('treeview', function() {
	return function(scope, element, attrs) {
		$(document).ready(function() {
			element.treeview({collapsed: false});	
		});
	};
});

/*
 * Tabs (JQuery UI)
 */
directivesModule.directive('tabs', function() {
	return function(scope, element, attrs) {
		$(document).ready(function() {
			element.tabs();
		});
	};
});

/*
 * Progress bar (JQuery UI)
 */
directivesModule.directive('progressbar', function() {
	return function(scope, element, attrs) {
		$(document).ready(function() {
			element.progressbar({
				value: attrs.ngModel.current - attrs.ngModel.start,
			});
		});
	};
});

/*
 * Help-Button + Popup
 */
directivesModule.directive('help', function(PopupService) {
    return function(scope, element, attrs) {
        $(document).ready(function() {
        	var content = element[0].innerHTML;
        	element[0].innerHTML = 'Help';
            $(element[0]).button();
            $(element[0]).addClass('fullWidth');
            $(element[0]).click(function() {
            	PopupService.openMessageDialog(content, 'Help');
            });
        });
    };
}); 

/*
 * Object view (JQuery TreeView)
 */
directivesModule.directive('objectView', function() {
    return function(scope, element, attrs) {
        function writeList(object) {
            var $ul = $('<ul></ul>');
            for(var prop in object) {
                if(typeof object[prop] === 'object') {
                    $ul.append('<li>');
                    $ul.append(writeList(object[prop]));
                    $ul.append('</li>');
                } else if(typeof object[prop] === 'function' || prop.startsWith('$')) {
                    continue;
                } else {
                    $ul.append('<li>' + prop + ': ' + object[prop] + '</li>');
                }
            }
            return $ul;
        };
        element.append('<div id="objectViewTreeControl" class="fullWidth centered"><img src="lib/jquery.treeview/images/minus.gif"> <a href="#">collapse all</a>&nbsp;&nbsp;|&nbsp&nbsp<img src="lib/jquery.treeview/images/plus.gif"> <a href="#">open all</a></div><hr />');
        element.append(writeList(scope[attrs.ngModel]).treeview({collapsed: true,animated: "fast", control: "#objectViewTreeControl"}));
    };
});
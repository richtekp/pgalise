/*
 * Module for directives
 *
 * @author dhoeting
 */
var directivesModule = angular.module('directives', []);

/*
 * Slider
 *
 * @author dhoeting
 */
directivesModule.directive('slider', function() {
	return function(scope, element, attrs) {
		$(document).ready(function() {
			element.slider({
				min: parseInt(attrs.min),
				max: parseInt(attrs.max),
				value: parseInt(scope[attrs.ngModel]),
				slide: function(event, ui) {
					scope[attrs.ngModel] = ui.value;
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
 * Button
 *
 * @author dhoeting
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
 * Datepicker
 *
 * @author dhoeting
 */
directivesModule.directive('datepicker', function() {
	return function(scope, element, attrs) {
		$(document).ready(function() {
			element.datepicker({
				inline: true,
				showButtonPanel: false,
				onSelect: function(selectedDate) {
					putObject(attrs.ngModel, scope, $(this).datepicker('getDate').getTime());
					scope.$apply();
				}
			});
			element.datepicker('setDate', new Date(scope[attrs.ngModel]));
		});
	};
});

directivesModule.directive('datepickerSimple', function() {
	return function(scope, element, attrs) {
		$(document).ready(function() {
			element.datepicker({
				inline: false,
				showButtonPanel: false,
				onSelect: function(selectedDate) {
					putObject(attrs.ngModel, scope, $(this).datepicker('getDate').getTime());
					scope.$apply();
				}
			});
			element.datepicker('setDate', new Date(scope[attrs.ngModel]));
		});
	};
});

directivesModule.directive('rating', function() {
	return function(scope, element, attrs) {
		$(document).ready(function() {
			element.children('input[value=' + scope[attrs.ngModel] + ']').attr('checked', 'checked');
			element.children('input').rating(
				{
					callback : function(value) {
						putObject(attrs.ngModel, scope, value);
						scope.$apply();
					}
				}
			);
		});
	}; 
});

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

directivesModule.directive('objectView', function() {
	return function(scope, element, attrs) {
		function writeList(object) {
			var $ul = $('<ul>');
			for(var prop in object) {
				if(typeof object[prop] === 'object') {
					var $li = $('<li>');
					$li.append(prop);
					$li.append(writeList(object[prop]));
					$ul.append($li);
				} else if(typeof object[prop] === 'function' || prop.startsWith('$')) {
					continue;
				} else {
					$ul.append('<li>' + prop + ': ' + object[prop] + '</li>');
				}
			}
			return $ul;
		};
		var code;
		if(attrs.showControl === 'true') {
		    code = new Date().getTime();
		    element.append('<div id="objectViewTreeControl' + code + '" class="fullWidth centered"><img src="lib/jquery.treeview/images/minus.gif"> <a href="#">collapse all</a>&nbsp;&nbsp;|&nbsp&nbsp<img src="lib/jquery.treeview/images/plus.gif"> <a href="#">open all</a></div><hr />')
		}
		element.append(writeList(scope[attrs.ngModel]).treeview({collapsed: true,animated: "fast", control: "#objectViewTreeControl"+code}));
	};
});
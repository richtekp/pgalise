/*
 * Helper method for extention of classes
 * @author Dennis Höting
 */
var __extends = this.__extends || function(d, b) {
	function __() {
		this.constructor = d;
	}
	__.prototype = b.prototype;
	d.prototype = new __();
};

/*
 * Counting function
 * @param mySet set to be counted
 * @author Dennis Höting
 */
var count = function(mySet) {
	if (!undef(mySet)) {
		return Object.keys(mySet).length;
	}
	return 0;
};

/*
 * Checking function
 * @param ngModel model for view
 * @param value value to be set (true/false)
 * @author Dennis Höting
 */
var check = function(ngModel, value) {
	if (value) {
		$('input[ng-model=' + ngModel + ']').attr('checked', true);
	} else {
		$('input[ng-model=' + ngModel + ']').removeAttr('checked');
	}
};

/*
 * MouseCoords
 * @author Dennis Höting
 */
function Point(x, y) {
	this.x = x || 0;
	this.y = y || 0;
};
var mouseCoords = {};
mouseCoords.current = new Point(0, 0);

/*
 * Overrides
 * @author Dennis Höting
 */
// By John Resig (JQuery)
Array.prototype.remove = function(from, to) {
	var rest = this.slice((to || from) + 1 || this.length);
	this.length = from < 0 ? this.length + from : from;
	return this.push.apply(this, rest);
};

/*
 * 
 * @author Dennis Höting
 */
Date.prototype.dateBased = function() {
	return this.getDate() + '.' + (this.getMonth() + 1) + '.' + this.getFullYear();
};

/*
 * 
 * @author Dennis Höting
 */
Date.prototype.minuteBased = function() {
	return this.getDate() + '.' + (this.getMonth() + 1) + '.' + this.getFullYear() + ' ' + ((this.getHours() <= 9) ? ('0' + this.getHours()) : this.getHours()) + ':'
			+ ((this.getMinutes() <= 9) ? ('0' + this.getMinutes()) : this.getMinutes());
};

/*
 * 
 * @author Dennis Höting
 */
Date.prototype.secondBased = function() {
	return this.getDate() + '.' + (this.getMonth() + 1) + '.' + this.getFullYear() + ' ' + this.getHours() + ':' + this.getMinutes() + ':' + this.getSeconds();
};

/* 
 * JQuery-Extension
 * By Neal 
 * http://stackoverflow.com/questions/1206203/how-to-distinguish-between-left-and-right-mouse-click-with-jquery
 */
(function($) {
	$.fn.rightClick = function(method) {
		$(this).bind('contextmenu rightclick', function(e) {
			e.preventDefault();
			method();
			return false;
		});

	};
})(jQuery);

/*
 * http://snipplr.com/view.php?codeview&id=12491
 * POSTED BY
 * Sephr on 02/23/09
 */
function getFunctionName(func) {
	if (typeof func == "function" || typeof func == "object") {
		var fName = ("" + func).match(/function\s*([\w\$]*)\s*\(/);
	}
	if (fName !== null) {
		return fName[1];
	}
};

/**
 * jQuery.ajax mid - CROSS DOMAIN AJAX 
 * ---
 * @author James Padolsey (http://james.padolsey.com)
 * @version 0.11
 * @updated 12-JAN-10
 * ---
 * Note: Read the README!
 * ---
 * @info http://james.padolsey.com/javascript/cross-domain-requests-with-jquery/
 */
jQuery.ajax = (function(_ajax) {

	var protocol = location.protocol, hostname = location.hostname, exRegex = RegExp(protocol + '//' + hostname), YQL = 'http' + (/^https/.test(protocol) ? 's' : '')
			+ '://query.yahooapis.com/v1/public/yql?callback=?', query = 'select * from html where url="{URL}" and xpath="*"';

	function isExternal(url) {
		return !exRegex.test(url) && /:\/\//.test(url);
	}

	return function(o) {

		var url = o.url;

		if (/get/i.test(o.type) && !/json/i.test(o.dataType) && isExternal(url)) {

			// Manipulate options so that JSONP-x request is made to YQL

			o.url = YQL;
			o.dataType = 'json';

			o.data = {
				q : query.replace('{URL}', url + (o.data ? (/\?/.test(url) ? '&' : '?') + jQuery.param(o.data) : '')),
				format : 'xml'
			};

			// Since it's a JSONP request
			// complete === success
			if (!o.success && o.complete) {
				o.success = o.complete;
				delete o.complete;
			}

			o.success = (function(_success) {
				return function(data) {

					if (_success) {
						// Fake XHR callback.
						_success.call(this, {
							responseText : (data.results[0] || '')
							// YQL screws with <script>s
							// Get rid of them
							.replace(/<script[^>]+?\/>|<script(.|\s)*?\/script>/gi, '')
						}, 'success');
					}

				};
			})(o.success);

		}

		return _ajax.apply(this, arguments);

	};

})(jQuery.ajax);

// From http://www.zehnet.de/2010/11/19/document-elementfrompoint-a-jquery-solution/
(function($) {
	var check = false, isRelative = true;

	$.elementFromPoint = function(x, y) {
		if (!document.elementFromPoint)
			return null;

		if (!check) {
			var sl;
			if ((sl = $(document).scrollTop()) > 0) {
				isRelative = (document.elementFromPoint(0, sl + $(window).height() - 1) == null);
			} else if ((sl = $(document).scrollLeft()) > 0) {
				isRelative = (document.elementFromPoint(sl + $(window).width() - 1, 0) == null);
			}
			check = (sl > 0);
		}

		if (!isRelative) {
			x += $(document).scrollLeft();
			y += $(document).scrollTop();
		}

		return document.elementFromPoint(x, y);
	};

})(jQuery);

// from https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/Object/keys
if (!Object.keys) {
	Object.keys = (function() {
		var hasOwnProperty = Object.prototype.hasOwnProperty, hasDontEnumBug = !({
			toString : null
		}).propertyIsEnumerable('toString'), dontEnums = ['toString', 'toLocaleString', 'valueOf', 'hasOwnProperty', 'isPrototypeOf', 'propertyIsEnumerable', 'constructor'], dontEnumsLength = dontEnums.length

		return function(obj) {
			if (typeof obj !== 'object' && typeof obj !== 'function' || obj === null)
				throw new TypeError('Object.keys called on non-object')

			var result = []

			for ( var prop in obj) {
				if (hasOwnProperty.call(obj, prop))
					result.push(prop)
			}

			if (hasDontEnumBug) {
				for ( var i = 0; i < dontEnumsLength; i++) {
					if (hasOwnProperty.call(obj, dontEnums[i]))
						result.push(dontEnums[i])
				}
			}
			return result
		};
	})();
};

var undef = function(v) {
	return typeof (v) === 'undefined';
};

/*
 * String extention for startsWith
 * http://stackoverflow.com/questions/646628/javascript-startswith
 */
if (typeof String.prototype.startsWith != 'function') {
	// see below for better implementation!
	String.prototype.startsWith = function(str) {
		return this.indexOf(str) == 0;
	};
}

/*
 * Helper method to put model into AngularJS scope
 */
function putObject(path, object, value) {
	var modelPath = path.split(".");

	function fill(object, elements, depth, value) {
		var hasNext = ((depth + 1) < elements.length);
		if (depth < elements.length && hasNext) {
			if (!object.hasOwnProperty(modelPath[depth])) {
				object[modelPath[depth]] = {};
			}
			fill(object[modelPath[depth]], elements, ++depth, value);
		} else {
			object[modelPath[depth]] = value;
		}
	}
	fill(object, modelPath, 0, value);
}

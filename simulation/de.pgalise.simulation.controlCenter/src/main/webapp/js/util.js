/*
 * Helper method for extention of classes
 * @author Dennis Höting
 */
var __extends = this.__extends || function(d, b) {
	function __() {this.constructor = d;}
	__.prototype = b.prototype;
	d.prototype = new __();
};

/*
 * @author Dennis Höting
 */
var count = function(mySet) {
    if (typeof mySet !== 'undefined') {
        return Object.keys(mySet).length;
    }
    return 0;
};

/*
 * MouseCoords
 * //TODO: Put somewhere else
 * 
 * @author Dennis Höting
 */
var mouseCoords = {};
function Point(x,y) {
	this.x = x || 0;
	this.y = y || 0;
};
mouseCoords.current = new Point(0,0);

/*
 * Overrides
 * =============================
 */
 
/*
 * Array remove method
 * By John Resig (JQuery)
 */
Array.prototype.remove = function(from, to) {
	var rest = this.slice((to || from) + 1 || this.length);
	this.length = from < 0 ? this.length + from : from;
	return this.push.apply(this, rest);
};

/*
 * Date toString method, date based
 * E.g. 24.11.2012
 * 
 * @author dhoeting
 */
Date.prototype.dateBased = function() {
	return this.getDate() + '.' + (this.getMonth()+1) + '.' + this.getFullYear();
};	

/*
 * Date toString method, minute based
 * E.g. 24.11.2012 11:53
 * 
 * @author dhoeting
 */
Date.prototype.minuteBased = function() {
	return this.getDate() + '.' + (this.getMonth()+1) + '.' + this.getFullYear() + ' ' + this.getHours() + ':' + this.getMinutes();
};

/*
 * Date toString method, minute based
 * E.g. 24.11.2012 11:53
 * 
 * @author dhoeting
 */
Date.prototype.secondBased = function() {
	return this.getDate() + '.' + (this.getMonth()+1) + '.' + this.getFullYear() + ' ' + this.getHours() + ':' + this.getMinutes() + ':' + this.getSeconds();
};
	
/* 
 * JQuery-Extension for rightClick listener
 * By Neal 
 * http://stackoverflow.com/questions/1206203/how-to-distinguish-between-left-and-right-mouse-click-with-jquery
 */
(function($) {
  $.fn.rightClick = function(method) {
    $(this).bind('contextmenu rightclick', function(e){
        e.preventDefault();
        method();
        return false;
    });

  };
})(jQuery);

/*
 * String extention for startsWith
 * http://stackoverflow.com/questions/646628/javascript-startswith
 */
if (typeof String.prototype.startsWith != 'function') {
  // see below for better implementation!
  String.prototype.startsWith = function (str){
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
        if(depth < elements.length && hasNext) {
            if(!object.hasOwnProperty(modelPath[depth])) {
                object[modelPath[depth]] = {};
            }
            fill(object[modelPath[depth]], elements, ++depth, value);
        } else {
            object[modelPath[depth]] = value;
        }
    }
    fill(object, modelPath, 0, value);
}

/*
 * Stack trace function by
 * http://ivan-gandhi.livejournal.com/942493.html
 */
function stacktrace() { 
  function st2(f) {
    return !f ? [] : 
        st2(f.caller).concat([f.toString().split('(')[0].substring(9) + '(' + f.arguments.join(',') + ')']);
  }
  return st2(arguments.callee.caller);
}
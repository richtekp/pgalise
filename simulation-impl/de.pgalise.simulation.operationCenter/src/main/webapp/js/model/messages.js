/*
 * Package model
 * @author Dennis Höting
 */
(function(model) {
	/*
	 * Gate message
	 * @author Dennis Höting
	 */
	var GateMessage = (function() {
		/*
		 * Constructor
		 */
		function GateMessage(id, map) {
			if ( typeof map === 'undefined')
				map = {};
			this.messageType = model.MessageType.GATE_MESSAGE;
			this.messageId = id;
			this.content = map;
		}

		/*
		 * Add key-value-pair
		 */
		GateMessage.prototype.add = function(key, value) {
			this.content[key] = value;
		};

		return GateMessage;
	})();
	model.GateMessage = GateMessage;
})(model);

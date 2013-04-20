define(['TodolistQubutoWebSocket'], function(TodolistQubutoWebSocket) {
	function Todolist() {
		var self = this;
		this.websocket = null;
	
		/**
		 * Init the todolist pages.
		 */
		this.init = function(/* routes */) {
			/*
			 * init the routes
			 */
			
			this.initWebsocket();
			
			$(document).on("click", "#btn-create-task", function() {
				self.newTask();
			});
		}
		
		this.newTask = function() {
			// show the popup
			$("div#new-task").modal({
				'containerCss': { 'min-width': '350px' },
				'overlayClose': true,
				'closeClass': 'close-modal'
			});
		}
		
		this.initWebsocket = function() {
			// websocketUri comes from the DOM.
			if (websocketUri != undefined) {
				this.websocket = new TodolistQubutoWebSocket(this);
				this.websocket.open(websocketUri);
			}
		}
	}
	
	return Todolist;
});
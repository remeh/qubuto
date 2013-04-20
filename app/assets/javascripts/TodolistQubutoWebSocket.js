define(function() {
	function TodolistQubutoWebSocket(todolist) {
		this.websocketUri = null;
		this.websocket = null;
		this.todolist = todolist;
		
		this.open = function(uri) {
			var $this = this;
			this.websocketUri = uri;
			this.websocket = new WebSocket(uri);
			this.websocket.onopen = function() { $this.onOpen(); };
			this.websocket.onmessage = function(message) { $this.onMessage(message); };
			this.websocket.onclose = function() { $this.onClose(); };
		}
		
		this.onMessage = function(message) {
			var json = JSON.parse(message.data);
			if (json != undefined) {
				if (json.error < 1) {
					this.processMessage(json);
				} else {
					if (json.error == 1) { // NOT_AUTHENTICATED
						alert("You're not not authenticated or your session has expired.");
						document.location.href = "/login";
					} else {
						alert('An error occured : ' + json.message);
					}
				}
			}
		}
		
		this.onOpen = function() {
		}
		
		this.onClose = function() {
		}
		
		/*
		 * When the websocket receive a message, process the matching action.
		 */
		this.processMessage = function(json) {
			switch (json.action) {
				case "TopicUpdate":
					this.topicUpdate(json);
					break;
			}
		}
	}
	
	return TodolistQubutoWebSocket;
});
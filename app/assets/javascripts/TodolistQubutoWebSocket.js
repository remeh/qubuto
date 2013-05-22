define(function() {
	function TodolistQubutoWebSocket(todolist) {
        var self = this;
		this.websocketUri = null;
		this.websocket = null;
		this.todolist = todolist;
		
		this.open                       = function(uri) {
			var $this = this;
			this.websocketUri = uri;
			this.websocket = new WebSocket(uri);
			this.websocket.onopen = function() { $this.onOpen(); };
			this.websocket.onmessage = function(message) { $this.onMessage(message); };
			this.websocket.onclose = function() { $this.onClose(); };
		}
		
		this.onMessage                  = function(message) {
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
		
		this.onOpen                     = function() {
		}
		
		this.onClose                    = function() {
		}

        this.addTag                     = function(json) {
            if (json == undefined) {
                return;
            }
            self.todolist.activeTag(json.taskId, json.tag);
        }

        this.removeTag                  = function(json) {
            if (json == undefined) {
                return;
            }
            self.todolist.removeTag(json.taskId, json.tag);
        }

        this.newTask                  = function(json) {
            if (json == undefined) {
                return;
            }
            self.todolist.insertTask(json);
        }

        this.deleteTask               = function(json) {
            if (json == undefined) {
                return;
            }
            self.todolist.removeTask(json.id);
        }
		
		/*
		 * When the websocket receive a message, process the matching action.
		 */
		this.processMessage = function(json) {
			switch (json.action) {
                // TODO
                case "AddTag":
                    this.addTag(json);
                    break;
                case "RemoveTag":
                    this.removeTag(json);
                    break;
				case "NewTask":
					this.newTask(json);
					break;
				case "DeleteTask":
					this.deleteTask(json);
					break;
			}
		}
	}
	
	return TodolistQubutoWebSocket;
});

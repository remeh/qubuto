define(function() {
	function ConversationQubutoWebSocket(conversation) {
		this.websocketUri = null;
		this.websocket = null;
		this.editorTopic = null;
		this.conversation = conversation;
		
		this.open = function(uri) {
			var $this = this;
			this.websocketUri = uri;
			this.websocket = new WebSocket(uri);
			this.websocket.onopen = function() { $this.onOpen(); };
			this.websocket.onmessage = function(message) { $this.onMessage(message); };
			this.websocket.onclose = function() { $this.onClose(); };
		}
		
		this.setEditorTopic = function(editorTopic) {
			this.editorTopic = editorTopic;
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
		
		this.processMessage = function(json) {
			switch (json.action) {
				case "TopicUpdate":
					this.topicUpdate(json);
					break;
				case "NewMessage":
					this.newMessage(json);
					break;
				case "UpdateMessage":
					this.updateMessage(json);
					break;
				case "DeleteMessage":
					this.removeMessage(json);
					break;
			}
		}

        this.removeMessage               = function(json) {
            conversation.removeMessage(json.id);
        }
		
		this.newMessage = function(json) {
			conversation.insertMessage(json.id, json.position, json.content, json.username, json.creationDate);
		}
		
		this.updateMessage = function(json) {
			conversation.updateMessage(json.id, json.position, json.content, json.lastUpdate);
		}
	
		/**
		 * Action called when the topic of the conversation is updated.
		 * @param json the JSON received from Qubuto.
		 */ 	
		this.topicUpdate = function(json) {
			// refresh the content
			$("#wmd-input-topic").val(json.content);
			$preview = $("#wmd-preview-topic");
			$preview.hide();		
			this.editorTopic.refreshPreview();
			
			// refresh the last update date
			$(".topic").find('.last-update-date').html(json.lastUpdate);
			
			$preview.fadeIn(150);
		}
	}
	
	return ConversationQubutoWebSocket;
});

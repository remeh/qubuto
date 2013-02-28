define(['ConversationQubutoWebSocket'], function(ConversationQubutoWebSocket) {
	function Conversation() {
		var _this = this;
		this.editorTopic = null;
		this.editors = [];
		
		/**
		 * Inits an editor.
		 * @param the suffix for Pagedown, could be "topic" or a message id
		 * @param the position of the editor in the message (-1 means that it's the topic)
		 * @param the initial content of the editor.
		 */		
		this.initAnEditor = function(suffix, position, content) {
			/*
			 * Pagedown Markdown converter and hook to remove HTML.
			 */
			var converter = Markdown.getSanitizingConverter();
			
			converter.hooks.chain("preBlockGamut", function (text, rbg) {
			    return text.replace(/^ {0,3}""" *\n((?:.*?\n)+?) {0,3}""" *$/gm, function (whole, inner) {
			        return "<blockquote>" + rbg(inner) + "</blockquote>\n";
			    });
			});
			
			/*
			 * Create and launch the editor.
			 */
			var editor = new Markdown.Editor(converter, "-" + suffix);
			
			editor.run();
			
			if (position == -1) {
				this.editorTopic = editor;
			} else {
				this.editors[position] = editor;
			}
			
			/*
			 *
			 */
			 $("#wmd-input-" + suffix).val(content);
			 			
			$("button#save-" + suffix).on("click", function() {
				_this.save(suffix);
				// hide the edit mode
				_this.switchEditMode('hide', 100, suffix);
			});
			
			$("button#edit-" + suffix).on("click", function() {
				_this.switchEditMode('show', 100, suffix);
			});
		}
		
		/**
		 * Saves the content of the provided editor.
		 */
		this.save = function(suffix) {
			var content = $("#wmd-input-" + suffix).val();
			$.ajax({
				type: "POST",
				url: _this.routeSaveTopic,
				data: {
				  	'content': content
				  }
				})
				.done(function(data) {
					if (data != null) {
						var json = JSON.parse(data);
						if (json.error != 0) {
							alert("Error: " + json.message);
						}
					}
				})
				.fail(function(jqxhr) {
					if (jqxhr != null) {
						var json = JSON.parse(jqxhr.responseText);
						if (json.error == 1) { // NOT_AUTHENTICATED
							alert("You're not authenticated or your session has expired.");
							document.location.href = "/login";
						} else {
							if (json.message != undefined) {
								alert("An error occurred : " + json.message);
							} else { 
								alert("An unknown error occurred.");
							}
						}
					}
				});
		}
	
		/**
		 * Switch the preview/edit mode
		 */
		this.switchEditMode = function(state, duration, suffix) {
			if (state == 'hide') {
				$("div#wmd-" + suffix).fadeOut(duration);
				$("button#save-" + suffix).hide();
				$("button#edit-" + suffix).fadeIn(duration);
			} else {
				$("div#wmd-" + suffix).fadeIn(duration);
				$("button#edit-" + suffix).hide();
				$("button#save-" + suffix).fadeIn(duration);
			}
		}
	
		/**
		 * Init the conversations pages.
		 * @param domain the domain ex: http://localhost/
		 * @param routeGetTopic the route to get the conversation topic content
		 */
		this.init = function(domain, routeGetTopic, routeSaveTopic) {
			this.routeGetTopic = routeGetTopic;
			this.routeSaveTopic = routeSaveTopic;
			
			// init the topic editor
			// the var 'content' used here come from the view	
			this.initAnEditor("topic", -1, content);
			
			this.initWebsocket();
			
			this.switchEditMode('hide', 1, "topic");
		}
		
		this.initWebsocket = function() {
			// websocketUri comes from the DOM.
			if (websocketUri != undefined) {
				websocket = new ConversationQubutoWebSocket();
				websocket.setEditorTopic(this.editorTopic);
				websocket.open(websocketUri);
			}
		}
		
		this.initMessages = function() {
			if (messages == undefined || messagesCount == 0) {
				return;
			}
			
			for (var i = 0; i < messagesCount; i++) {
				var message = messages[i];
				if (message == undefined) {
					continue;
				}
				this.insertMessage(message.id, message.position, message.content);
			}
		}
		
		this.insertMessage = function(id, position, content) {
			// clone the prototype
			
			var $message = $(".topic-message-prototype").clone();
			
			/*
			 * remove and set some useful values
			 */
			 
			// it is no more a prototype
			
			$message.removeClass("topic-message-prototype");
			
			// replace in html content the __id__ tag by the real id
			
			var messageDomContent = $message.html();
			messageDomContent = messageDomContent.replace(/__id__/g, id);
			$message.html(messageDomContent);
			
			// set some data in the dom and in the jQuery cache
			
			$message.attr("data-id", id);
			$message.data("id", id);
			$message.attr("data-position", position);
			$message.data("position", position);
			
			// append the message in the DOM
			
			$(".topic-message-container").append($message);
			
			// init the editor
			
			this.initAnEditor(id, position, content); 
			
			// show the message
			
			$message.fadeIn(300);
		}
		
	}
	
	return Conversation;
});
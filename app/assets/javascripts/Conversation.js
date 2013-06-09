define(['ConversationQubutoWebSocket'], function(ConversationQubutoWebSocket) {
	function Conversation(routeGetTopic, routeSaveTopic, routeNewMessage, routeSaveMessage) {
		var self = this;
		this.editorTopic = null;
		this.editors = [];
		this.websocket = null;
		
		/**
		 * Inits an editor.
		 * @param the suffix for Pagedown, could be "topic" or a message id
		 * @param the position of the editor in the message (-1 means that it's the topic)
		 * @param the initial content of the editor.
		 * @param isNew true if this is a new message
		 */		
		this.initAnEditor = function(suffix, position, content, isNew) {
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
			 * Append the content.
			 */
			 
		 	$("#wmd-input-" + suffix).val(content);
			 
			/*
			 * Save clicks
			 */		
			 
			$("button#save-" + suffix).on("click", function() {
				if (isNew) {
					self.saveNewMessage(suffix);
				} else {
					/*
					 * Detects whether its a message or the topic.
					 */
					if ($("#wmd-"+suffix).parent().hasClass("topic-message")) {
						/*
						 * Save the message
						 */
						var routeMessage = $("#wmd-"+suffix).parent().data('route-update');
						self.save(suffix, routeMessage);
					} else {
						/*
						 * Save the topic
						 */
						self.save(suffix);
					}
					
					/*
					 * Hide the edit mode.
					 */
					self.switchEditMode('hide', 100, suffix);
				}
			});
			
			
			$("button#edit-" + suffix).on("click", function() {
				self.switchEditMode('show', 100, suffix);
			});
			
			if (isNew) {
				self.switchEditMode('show', 1, suffix);
			}
		}
		
		/**
		 * Saves the content of the provided editor.
		 * @param suffix the suffix of the editor
		 * @param routeMessage if we want to save a message, the route to save it. (otherwise, we save the topic.)
		 */
		this.save = function(suffix, routeMessage) {
			var content = $("#wmd-input-" + suffix).val();
			
			var route = self.routeSaveTopic;
			if (routeMessage != undefined) {
				route = routeMessage;
			}
			
			self.makeAjaxCall("POST", route, { "content": content });
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
		 * @param routeGetTopic the route to get the conversation topic content
		 */
		this.construct = function(routeGetTopic, routeSaveTopic, routeNewMessage) {
			self.routeGetTopic = routeGetTopic;
			self.routeSaveTopic = routeSaveTopic;
			self.routeNewMessage = routeNewMessage;
			
			// init the topic editor
			// the var 'content' used here come from the view	
			self.initAnEditor("topic", -1, content);
			
			self.initWebsocket();
			
			self.switchEditMode('hide', 1, "topic");

            self.bindEvents();
			
		    self.initMessages();
		}
		
        this.bindEvents                 = function() {
			$(document).on("click", "#conversation-add-message", function() {
				self.newMessage();
			});
        }

		this.newMessage = function() {
			$(".conversation-add-message").hide();
			
			$message = this.prepareNewEditor("new", -1);
			
			$(".container-new-message").append($message);
			
			this.initAnEditor("new", -1, "", true);
			
			$message.fadeIn(300);
		}
		
		
		this.saveNewMessage = function(suffix) {
			$topic = $('.topic-message-' + suffix);
			
			/*
			 * Saves the new message.
			 */
			
			var content = $("#wmd-input-" + suffix).val();
			
			self.makeAjaxCall("POST", self.routeNewMessage, { "content": content });
			
			// remove the edit and re-display the new message div
			$topic.remove();
			$(".conversation-add-message").fadeIn(300);
		}
		
		this.initWebsocket = function() {
			// websocketUri comes from the DOM.
			if (websocketUri != undefined) {
				this.websocket = new ConversationQubutoWebSocket(this);
				this.websocket.setEditorTopic(this.editorTopic);
				this.websocket.open(websocketUri);
			}
		}
		
		this.initMessages = function() {
			if (messages == undefined || messagesCount == 0) {
				return;
			}
			
			for (var i = 0; i < messagesCount; i++) {
				if (i in messages) {
					var message = messages[i];
					this.insertMessage(message.id, message.position, message.content, message.author, message.creationDate);
				}
			}
		}
		
		this.prepareNewEditor = function(id, position) {
			// clone the prototype
			
			var $message = $(".topic-message-prototype").clone();
			
			/*
			 * remove and set some useful values
			 */
			 
			// it is no more a prototype
			
			$message.removeClass("topic-message-prototype");
			
			// add a class instead
			
			$message.addClass("topic-message-" + id);
			
			// replace in html content the __id__ tag by the real id
			
			var messageDomContent = $message.html();
			messageDomContent = messageDomContent.replace(/__id__/g, id);
			$message.html(messageDomContent);
			
			// same for the route to update
			
			var routeToUpdate = $message.data('route-update');
			routeToUpdate = routeToUpdate.replace(/__id__/g, id);
			
			// set the data in the dom and in the jQuery cache
			
			$message.attr("data-route-update", routeToUpdate);
			$message.data("route-update", routeToUpdate);
			$message.attr("data-id", id);
			$message.data("id", id);
			$message.attr("data-position", position);
			$message.data("position", position);
			
			return $message;
		}
		
		this.insertMessage = function(id, position, content, author, creationDate) {
			var $message = this.prepareNewEditor(id, position);
			
			// fill creation information
			
			$message.find('span.message-author').html(author);
			$message.find('span.message-creation-date').html(creationDate);
			$message.find('div.message-author').show();
			
			// append the message in the DOM
			
			$(".topic-message-container").append($message);

			// Hide the edit button if the user isn't the author.
			if (author != $('span.username').data('username')) {
				$("button#edit-" + id).hide();
			}
			
			
			// init the editor
			
			this.initAnEditor(id, position, content); 
			
			// show the message
			
			$message.fadeIn(300);
		}
		
		this.updateMessage = function(id, position, content, lastUpdate) {
			var $message = $(".topic-message-" + id);
			if ($message == undefined) {
				// TODO FIXME better looking alert or no alert but a better solution.
				alert('An error occurred while update a message. You should refresh the page.');
			}
			
			// TODO adds an edit date ?
//			$message.find('.message-creation-date').html(lastUpdate);
			
			/*
			 * Refresh the content and the preview
			 */
			$("#wmd-input-" + id).val(content);
			$preview = $("#wmd-preview-" + id);
			$preview.hide();		
			this.editors[position].refreshPreview();
			$preview.fadeIn(150);
		}
		

		/**
		 * Do an AJAX call.
		 * @param type the type of call ("GET", "POST")
		 * @param route the route to call
		 * @param values the value to put in the request.
		 */
		this.makeAjaxCall = function(type, route, values) {
			$.ajax({
				type: type,
				url: route,
				data: values
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
								alert("An problem occurred : " + json.message);
							} else { 
								alert("An unknown error occurred.");
							}
						}
					}
				});
		}

        // ---------------------- 
    
        self.construct(routeGetTopic, routeSaveTopic, routeNewMessage, routeSaveMessage);
	}
	
	return Conversation;
});

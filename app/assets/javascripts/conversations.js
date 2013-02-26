var editorTopic = null;

$(function() {
	var domain = $("div#main-div").data("domain");
	var routeGetTopic = $("div#main-div").data("route-get-topic");
	var routeSaveTopic = $("div#main-div").data("route-save-topic");
	
	function initEditor() {
		var converter = Markdown.getSanitizingConverter();
		
		converter.hooks.chain("preBlockGamut", function (text, rbg) {
		    return text.replace(/^ {0,3}""" *\n((?:.*?\n)+?) {0,3}""" *$/gm, function (whole, inner) {
		        return "<blockquote>" + rbg(inner) + "</blockquote>\n";
		    });
		});
		
		editorTopic = new Markdown.Editor(converter, "-topic");
		
		editorTopic.run();
		
		// fills the topic
		$("#wmd-input-topic").val(content);
		
		$("button#save-topic").on("click", function() {
			saveTopic();
			// hide the edit mode
			switchEditMode('hide', 100, "-topic");
		});
		
		$("button#edit-topic").on("click", function() {
			switchEditMode('show', 100, "-topic");
		});
	}
	
	function saveTopic() {
		var content = $("#wmd-input-topic").val();
		$.ajax({
			type: "POST",
			url: routeSaveTopic,
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
	function switchEditMode(state, duration, editor) {
		if (state == 'hide') {
			$("div#wmd" + editor).fadeOut(duration);
			$("button#save" + editor).hide();
			$("button#edit" + editor).fadeIn(duration);
		} else {
			$("div#wmd" + editor).fadeIn(duration);
			$("button#edit" + editor).hide();
			$("button#save" + editor).fadeIn(duration);
		}
	}

	/**
	 * Init the conversations pages.
	 * @param domain the domain ex: http://localhost/
	 * @param routeGetTopic the route to get the conversation topic content
	 */
	function init(domain, routeGetTopic) {
		// the var content used here come from the view		
		initEditor(content);
		initWebsocket();
		
		switchEditMode('hide', 1, "-topic");
	}
	
	function initWebsocket() {
		// websocketUri comes from the DOM.
		if (websocketUri != undefined) {
			websocket = new ConversationQubutoWebSocket$$module$websocket();
			websocket.setEditorTopic(editorTopic);
			websocket.open(websocketUri);
		}
	}
	
	/*
	 * On ready.
	 */
	init(domain, routeGetTopic);
});

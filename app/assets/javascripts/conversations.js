require(["Conversation"], function(Conversation) {
	/*
	 * On ready.
	 */
	$(function() {
		/*
		 * Read needed data in the DOM.
		 */
		var routeGetTopic       = $("div#main-div").data("route-get-topic");
		var routeSaveTopic      = $("div#main-div").data("route-save-topic");
		var routeNewMessage     = $("div#main-div").data("route-new-message");
		var routeDeleteMessage  = $("div#main-div").data("route-delete-message");
		
		var conversation = new Conversation(routeGetTopic, routeSaveTopic, routeNewMessage, routeDeleteMessage);
	});
});

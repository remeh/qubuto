require(["Conversation"], function(Conversation) {
	/*
	 * On ready.
	 */
	$(function() {
		/*
		 * Read needed data in the DOM.
		 */
		var domain = $("div#main-div").data("domain");
		var routeGetTopic = $("div#main-div").data("route-get-topic");
		var routeSaveTopic = $("div#main-div").data("route-save-topic");
		var routeNewMessage = $("div#main-div").data("route-new-message");
		var routeSaveMessage = $("div#main-div").data("route-save-message");
		
		var conversation = new Conversation();
		
		/*
		 * Init the conversation.
		 */
		 
		conversation.init(domain, routeGetTopic, routeSaveTopic, routeNewMessage, routeSaveMessage);
		
		/*
		 * Init the existing messages.
		 */
		
		conversation.initMessages();
	});
});

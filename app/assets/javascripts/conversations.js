$(function() {
	var editor = null;
	var domain = $("div#main-div").data("domain");
	var routeGetTopic = $("div#main-div").data("route-get-topic");
	var routeSaveTopic = $("div#main-div").data("route-save-topic");
	
	/**
 	 * Init the editor and fill it with the provided content.
 	 * @param content the content to fill the editor with.
 	 */
	function initEditor(content) {
		var opts = {
		  basePath: domain + 'assets/external/epiceditor',
		  theme: {
		    base: '/base/epiceditor.css',
		    preview: '/preview/q.css',
		  	editor: '/editor/epic-light.css'
		  },
		  file: {
		  	autoSave: false
		  },
		  clientSideStorage: false
		};
		
		editor = new EpicEditor(opts).load(function() {
			// Loads the content
			this.importFile('content', content);
			this.preview();
		});
		
		// removes the HTML tags on preview
		editor.on("preview", function() {
			onPreview(this);
		});
	}
	
	function onPreview(editor) {
		var content = editor.exportFile();
		content = content.replace(/<.*?>/, '');
		editor.importFile('content', content);
		editor.save();
	}
	
	function saveTopic() {
		editor.save();
		var content = editor.exportFile();
		$.ajax({
				  type: "POST",
				  url: routeSaveTopic,
				  data: {
				  	'content': content
				  }
				})
			.done(function(data) {
				var json = JSON.parse(data);
				if (json.error != 0) {
					alert("Error: " + json.message);
				}
			})
			.fail(function() {
				alert("AJAX Fail. TODO");
			});
	}

	/**
	 * Init the conversations pages.
	 * @param domain the domain ex: http://localhost/
	 * @param routeGetTopic the route to get the conversation topic content
	 */
	function init(domain, routeGetTopic) {
		var content = null;
		
		$.ajax(routeGetTopic)
			.done(function(data) {
				var json = JSON.parse(data);
				if (json.error == 0) {
					initEditor(json.content);
				} else {
					alert("Error: " + json.message);
				}
			})
			.fail(function() {
				alert("AJAX Fail. TODO");
			});
			
		$("button#save").on("click", function() {
			saveTopic();
		});
	}
	
	/*
	 * On ready.
	 */
	init(domain, routeGetTopic);
});

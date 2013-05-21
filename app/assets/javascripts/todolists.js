require(["Todolist"], function(Todolist) {
	/*
	 * On ready.
	 */
	$(function() {
		/*
		 * Read needed data in the DOM.
		 */
		var routeAddTask    = $('#main-div').data('route-add-task');
		var routeAddTag     = $('#main-div').data('route-add-tag');
		var routeRemoveTag  = $('#main-div').data('route-remove-tag');
		var todolist = new Todolist();
		
		/*
		 * Init the Todolist.
		 */
		 
		todolist.init(routeAddTask, routeAddTag, routeRemoveTag);
	});
});

require(["Todolist"], function(Todolist) {
	/*
	 * On ready.
	 */
	$(function() {
		/*
		 * Read needed data in the DOM.
		 */
		var routeCloseTask      = $('#main-div').data('route-close-task');
		var routeOpenTask       = $('#main-div').data('route-open-task');
		var routeAddTask        = $('#main-div').data('route-add-task');
		var routeDeleteTask     = $('#main-div').data('route-delete-task');
		var routeAddTag         = $('#main-div').data('route-add-tag');
		var routeRemoveTag      = $('#main-div').data('route-remove-tag');
		var routeAddComment     = $('#main-div').data('route-add-comment');
		var routeDeleteComment  = $('#main-div').data('route-delete-comment');

		/*
		 * Create the Todolist.
		 */
		var todolist = new Todolist(routeAddTask, routeDeleteTask, routeAddTag, routeRemoveTag, routeCloseTask, routeOpenTask, routeAddComment, routeDeleteComment);
	});
});

require(["Todolist"], function(Todolist) {
	/*
	 * On ready.
	 */
	$(function() {
		/*
		 * Read needed data in the DOM.
		 */
		var routeAddTask = $('#main-div').data('route-add-task');
		var todolist = new Todolist();
		
		/*
		 * Init the Todolist.
		 */
		 
		todolist.init(routeAddTask);
	});
});

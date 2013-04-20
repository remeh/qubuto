require(["Todolist"], function(Todolist) {
	/*
	 * On ready.
	 */
	$(function() {
		/*
		 * Read needed data in the DOM.
		 */
		var todolist = new Todolist(/* routes ? */);
		
		/*
		 * Init the Todolist.
		 */
		 
		todolist.init();
	});
});

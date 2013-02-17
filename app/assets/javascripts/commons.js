$(function() {
	$(document).on("click", 'a[href="#"]', function(e) {
	     return false;
	     e.preventDefault();
	});
});

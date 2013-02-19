$(function() {
	/**
	 * Function to clean every inputs.
	 */
	function cleanInputs() {
		$(':input')
	   .not(':button, :submit, :reset, :hidden')
	   .val('')
	   .removeAttr('checked')
	   .removeAttr('selected');
   }

	/**
	 * Trigger the popup of new todolist
	 */
	$("a.new-todolist").on("click", function() {
		cleanInputs();
		$("div#new-todolist").modal({
			'containerCss': { 'min-width': '250px' },
			'overlayClose': true,
			'closeClass': 'close-modal'
		});
	});
	
	/**
	 * Trigger the popup of new conversation
	 */
	$("a.new-conversation").on("click", function() {
		cleanInputs();
		$("div#new-conversation").modal({
			'containerCss': { 'min-width': '220px', 'min-height': '160px' },
			'overlayClose': true,
			'closeClass': 'close-modal'
		});
	});
});
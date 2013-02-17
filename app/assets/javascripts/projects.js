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

	$("a.new-todolist").on("click", function() {
		cleanInputs();
		$("div#new-todolist").modal({
			'containerCss': { 'min-width': '250px' },
			'overlayClose': true,
			'closeClass': 'close-modal'
		});
	});
});
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
			'containerCss': { 'min-width': '350px' },
			'overlayClose': true,
			'closeClass': 'close-modal',
			onOpen: function (dialog) {
				dialog.overlay.fadeIn(100, function () {
					dialog.data.fadeIn(50, function () {
						dialog.container.show();
					});
				});
			},
			onClose: function (dialog) {
				dialog.container.fadeOut(150, function () {
					dialog.data.fadeOut(50, function () {
						dialog.overlay.fadeOut(50);
						$.modal.close();
					});
				});
			}
		});
	});
	
	/**
	 * Trigger the popup of new conversation
	 */
	$("a.new-conversation").on("click", function() {
		cleanInputs();
		$("div#new-conversation").modal({
			'containerCss': { 'min-width': '250px', 'min-height': '180px' },
			'overlayClose': true,
			'closeClass': 'close-modal',
			onOpen: function (dialog) {
				dialog.overlay.fadeIn(100, function () {
					dialog.data.fadeIn(50, function () {
						dialog.container.show();
					});
				});
			},
			onClose: function (dialog) {
				dialog.container.fadeOut(150, function () {
					dialog.data.fadeOut(50, function () {
						dialog.overlay.fadeOut(50);
						$.modal.close();
					});
				});
			}
		});
	});
});
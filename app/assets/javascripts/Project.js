define([], function() {
	function Project() {
		var self = this;

        this.bindEvents                  = function() {
            $(document).on("click", "a.new-todolist", function() {
                self.newTodolist();
            });
            $(document).on("click", "a.new-conversation", function() {
                self.newConversation();
            });
            $(document).on("click", "#add-collaborator", function() {
                self.addCollaborator($(this));
            });
            $(document).on("click", ".tab-toggle", function() {
                self.tabToggle($(this));
            });
        }

        /**
         * Triggered when an user clicks to add a collaborator
         * @param $button       the jQuery selector of the button element.
         */
        this.addCollaborator            = function($button) {
            // clean the error message
            $('#collaborator-error').text('');

            var collaboratorName = $('input#collaborator-name').val();

            if (collaboratorName == undefined || collaboratorName.length == 0) {
               $('#collaborator-error').text('You must give a name.');
               return;
            }

            // Loader!
            $('#add-loader').show();

            // Prepares the call
			var route = $('#rights-container').data('route-add-collaborator');
			var values = {
				"collaborator": collaboratorName
			}
            var doneCallback = function(json) {
                self.hideLoaders();
		    }
            var failCallback = function(json) {
                $('#collaborator-error').text(json.message);
                self.hideLoaders();
            }

            // sends the AJAX call.
            sendAjaxCall(route, values, doneCallback, failCallback);
        }

        this.hideLoaders                =  function() {
            $('.loader').hide();
        }

        /**
         * Triggered when an user click on a tab.
         * @param $a            the jQuery selector of the a element
         */
        this.tabToggle                  = function($a) {
            var toShow = $a.data('toggle');
            if (toShow == undefined) {
                return;
            }

            // reset all tabs
            $('.tab').removeClass('tab-active');
            $('.tab').removeClass('tab-inactive');
            $('.tab').addClass('tab-inactive');
            // select the new one
            $a.children('span').addClass('tab-active');
            // hide the content
            $oldContent = $('.tab-content-active');
            $oldContent.hide();
            $oldContent.removeClass('tab-content-active');
            // show the new content
            $newContent = $('#'+toShow);
            if ($newContent != undefined) {
                $newContent.fadeIn(100);
                $newContent.addClass('tab-content-active');
            }
        }

        /**
         * Trigger the popup of new conversation
         */
        this.newConversation            = function() {
            // clean the inputs
            self.cleanInputs();
            // show the modal
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
        }

        /**
         * Trigger the popup of new todolist
         */
        this.newTodolist                 = function() {
            // starts be cleaning the input
            self.cleanInputs();
            // display the modal
            $("#new-todolist").modal({
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
        }

        /**
         * Function to clean every inputs.
         */
        this.cleanInputs                 = function() {
            $(':input')
           .not(':button, :submit, :reset, :hidden')
           .val('')
           .removeAttr('checked')
           .removeAttr('selected');
       } 

        // ---------------------- 
        
        this.construct                  = function() {
            self.bindEvents();
        }

        self.construct();
	}
	
	return Project;
});


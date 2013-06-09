define([], function() {
	function Project() {
		var self = this;

        this.bindEvents                  = function() {
            $(document).on("click", "a.new-todolist", function() {
                self.newTodolist();
            });
            $(document).on("click","a.new-conversation", function() {
                self.newConversation();
            });
        }

        /**
         * Trigger the popup of new conversation
         */
        this.newConversation             = function() {
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


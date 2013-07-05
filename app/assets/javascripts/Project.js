define([], function() {
	function Project() {
		var self = this;

        var collaboratorTemplate = null;

        var projectTemplate      = null;

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
            $(document).on("click", ".remove-collaborator", function() {
                self.removeCollaborator($(this));
            });
            $(document).on("click", ".tab-toggle", function() {
                self.tabToggle($(this));
            });
            $(document).on("keydown", "input#collaborator-name", function(event) {
                if (event.keyCode == 13) {
                    self.addCollaborator();
                    event.preventDefault();
                    return false;
                }
            });
        }

        /**
         * Triggered when an user clicks to remove a collaborator
         * @param $a            the jQuery selector of the a element.
         */
        this.removeCollaborator         = function($a) {
            // clean the error message
            $('#collaborator-error').text('');

            var collaboratorName = $a.data('collaborator');

            // Loader!
            $('#add-loader').show();

            // Prepares the call
			var route = $('#rights-container').data('route-remove-collaborator');
			var values = {
				"collaborator": collaboratorName
			}
            var doneCallback = function(json) {
                $a.parents('li').remove();
                $('input#collaborator-name').val('');
                self.hideLoaders();
		    }
            var failCallback = function(json) {
                $('#collaborator-error').text(json.message);
                $('input#collaborator-name').val('');
                self.hideLoaders();
            }

            // sends the AJAX call.
            sendAjaxCall(route, values, doneCallback, failCallback);
        }

        /**
         * Triggered when an user clicks to add a collaborator
         */
        this.addCollaborator            = function() {
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
                self.addCollaboratorName(json.collaborator.username);
                $('input#collaborator-name').val('');
                self.hideLoaders();
		    }
            var failCallback = function(json) {
                $('#collaborator-error').text(json.message);
                $('input#collaborator-name').val('');
                self.hideLoaders();
            }

            // sends the AJAX call.
            sendAjaxCall(route, values, doneCallback, failCallback);
        }

        this.addCollaboratorName        = function(username) {
            var html = self.collaboratorTemplate({collaborator: username});
            var existing = false;
            var collaboratorsName = $('span.collaborator-name');
            for (var i = 0; i < collaboratorsName.length; i++) {
                if ($(collaboratorsName[i]).text() == username) {
                    existing = true;
                    break;
                }
            }
            if (!existing) {
                $('ul.collaborators').append(html);
            }
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

		this.initHandlebars              = function() {
			/*
			 * Prepare the Handlebars template.
			 */
			var collaboratorTemplateSource = $("script#collaborator-template").html();
            if (collaboratorTemplateSource != undefined) {
                self.collaboratorTemplate = Handlebars.compile(collaboratorTemplateSource);
            }
        }

        // ---------------------- 
        
        this.construct                  = function() {
            self.initHandlebars();
            self.bindEvents();
        }

        self.construct();
	}
	
	return Project;
});


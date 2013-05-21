define(['TodolistQubutoWebSocket'], function(TodolistQubutoWebSocket) {
	function Todolist() {
		var self = this;
		this.websocket = null;
		this.routeAddTask = null;
		this.routeAddTag = null;
		this.routeRemoveTag = null;
		this.todoTemplate = null;
	
		/**
		 * Init the todolist pages.
		 */
		this.init                       = function(routeAddTask, routeAddTag, routeRemoveTag) {
			/*
			 * init the routes
			 */
			this.routeAddTask   = routeAddTask;
			this.routeAddTag    = routeAddTag;
			this.routeRemoveTag = routeRemoveTag;
			
			this.initWebsocket();
			
			/*
			 * Prepare the handlebars templates.
			 */
			this.prepareHandlebarsTemplates();
			
			/*
			 * Init tasks.
			 */
			this.initTasks();

            /*
             * Binds DOM events.
             */
            this.bindEvents();
		}
			
        /**
         * Binds the DOM events.
         */
        this.bindEvents                 = function() {
			$(document).on("click", "#btn-create-task", function() {
				self.newTask();
			});
			
			$(document).on("click", "#add-task", function() {
				self.addTask($(this));
			});

            $(document).on("click", "a.tag", function() {
                self.tagClick($(this));
            });
        }

        /**
         * Called when a click has been done on a tag.
         * @param $selector the jQuery selector on the clicked tag.
         */
        this.tagClick                   = function($selector) {
            if ($selector == undefined) {
                return;
            }

            var tag = $selector.data('tag');  
            var taskId = $selector.parents('.todo-entry').first().attr('id');

            if ($selector.hasClass('active')) {
                // Click to remove
                self.removeTagAjaxCall(taskId, tag);
            } else {
                // Click to add
                self.addTagAjaxCall(taskId, tag);
            }
        }
		
		/**
		 * Prepares the handlebars templates.
		 */
		this.prepareHandlebarsTemplates = function() {
			/*
			 * Prepare the Handlebars template.
			 */
			var todoTemplateSource = $(".todo-template").html();
			self.todoTemplate = Handlebars.compile(todoTemplateSource);
		}
		
		/**
		 * Init the tasks in the view.
		 */
		this.initTasks                  = function() {
			// these data has been inserted in the dom in the top of the page
			// by the scala view.
			for (var i = 0; i < tasksCount; i++) {
				if (i in tasks) {
					var html = self.todoTemplate(tasks[i]);
					$('#todo-container').append(html);
				}
			}
		}
		
		/**
		 * When an user want to create a new task.
		 */
		this.newTask                    = function() {
			// clear the inputs
			self.clearInputs();

			// show the popup
			$("div#new-task").modal({
				'containerCss': { 'min-width': '350px' },
				'overlayClose': true,
				'closeClass': 'close-modal',
				onOpen: function (dialog) {
					dialog.overlay.fadeIn(100, function () {
						dialog.data.fadeIn(50, function () {
							dialog.container.show();

                            // Give the focus to the first input
                            $('input#add-task-title').focus();
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
		 * When the user has clicked on the Add button to create the task.
		 * @param $selector the jQuery selector of the button.
		 */
		this.addTask                        = function($selector) {
			/*
			 * Retrieve values
			 */
			var title = $("#add-task-title").val();
			var content = $("#add-task-content").val();
			
			/*
			 * If values not null, make the ajax call.
			 */
			if (content != undefined && content.length > 0
			  && title != undefined && title.length > 0) {
				self.disableAddTask();
				self.addTaskAjaxCall(title, content);
			}
			// TODO else ?
		}

        this.sendAjaxCall               = function(route, postValues, doneCallback, failCallback) {
			$.ajax({
				type: 'POST',
				url: route,
				data: postValues
				})
				.done(function(data) {
					if (data != null) {
						var json = JSON.parse(data);
						if (json.error != 0) {
							alert("Error: " + json.message);
						}
					}

                    if (doneCallback != undefined) {
                        doneCallback(json);
                    }
				})
				.fail(function(jqxhr) {
                    if (failCallback != undefined) {
                        failCallback(json);
                    }
					if (jqxhr != null) {
						var json = JSON.parse(jqxhr.responseText);
						if (json.error == 1) { // NOT_AUTHENTICATED
							alert("You're not authenticated or your session has expired.");
							document.location.href = "/login";
						} else {
							if (json.message != undefined) {
								alert("An error occurred : " + json.message);
							} else { 
								alert("An unknown error occurred.");
							}
						}
					}
				});
        }
		
		/**
		 * The AJAX call to create a Task.
         * @param title the title of the tag
         * @param content the content of the tag
		 */
		this.addTaskAjaxCall            = function(title, content) {
			var route = self.routeAddTask;
			var values = {
				"content": content,
				"title": title
			}
            var doneCallback = function() {
                // re-enable inputs
                self.enableAddTask();
                // close the modal
                $.modal.close();
		    }
            var failCallback = function() {
                self.enableAddTask();
            }
            // sends the AJAX call.
            self.sendAjaxCall(route, values, doneCallback, failCallback);
		}
		
		/**
		 * The AJAX call to add a tag on a Task.
         * @param taskId id of the task on which we add a tag
         * @param tag the tag to add on the task
		 */
		this.addTagAjaxCall             = function(taskId, tag) {
			var route = self.routeAddTag;
			var values = {
                "taskId": taskId,
                "tag": tag 
			}
            var doneCallback = function() {
                alert('yaaay!'); //TODO
		    }
            var failCallback = function() {
                alert('ooh');   // TODO
            }

            // sends the AJAX call.
            self.sendAjaxCall(route, values, doneCallback, failCallback);
		}
		
		/**
		 * The AJAX call to remove a tag on a Task.
         * @param taskId id of the task on which we remove a tag
         * @param tag the tag to remove on the task
		 */
		this.removeTagAjaxCall             = function(taskId, tag) {
			var route = self.routeRemoveTag;
			var values = {
                "taskId": taskId,
                "tag": tag 
			}
            var doneCallback = function() {
                alert('yaaay!'); // TODO
		    }
            var failCallback = function() {
                alert('ooh');    // TODO
            }
            
            // sends the AJAX call.
            self.sendAjaxCall(route, values, doneCallback, failCallback);
		}
		
		this.disableAddTask             = function($selector) {
			$("#add-task-title").attr('disabled','disabled');
			$("#add-task-content").attr('disabled','disabled');
			$('#add-task').attr('disabled','disabled');
			setTimeout(function() {
				self.enableAddTask();
			}, 30000);
		}
		
		this.enableAddTask              = function() {
			$("#add-task-title").removeAttr('disabled');
			$("#add-task-content").removeAttr('disabled');
			$('#add-task').removeAttr('disabled');
		}
		
		this.initWebsocket              = function() {
			// websocketUri comes from the DOM.
			if (websocketUri != undefined) {
				this.websocket = new TodolistQubutoWebSocket(this);
				this.websocket.open(websocketUri);
			}
		}
		
		/**
		 * Clears the input of the "Add task" feature.
		 */
		this.clearInputs                = function() {
			$("#add-task-title").val('');
			$("#add-task-content").val('');
		}
	}
	
	return Todolist;
});

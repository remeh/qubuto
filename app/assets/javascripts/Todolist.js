define(['TodolistQubutoWebSocket'], function(TodolistQubutoWebSocket) {
	function Todolist() {
		var self = this;
		this.websocket          = null;
		this.routeAddTask       = null;
		this.routeDeleteTask    = null;
		this.routeAddTag        = null;
		this.routeRemoveTag     = null;
		this.todoTemplate       = null;
	
		/**
		 * Init the todolist pages.
		 */
		this.init                       = function(routeAddTask, routeDeleteTask, routeAddTag, routeRemoveTag) {
			/*
			 * init the routes
			 */
			this.routeAddTask       = routeAddTask;
			this.routeDeleteTask    = routeDeleteTask;
			this.routeAddTag        = routeAddTag;
			this.routeRemoveTag     = routeRemoveTag;
			
			this.initWebsocket();
			
			/*
			 * Prepare the handlebars templates.
			 */
			this.initHandlebars();
			
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

            $(document).on("click", "a.task-remove", function() {
                self.deleteTask($(this)); 
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

            var $span = $selector.children('span').first();

            var tag = $selector.data('tag');  
            var taskId = $selector.parents('.todo-entry').first().attr('id');

            if ($span.hasClass('tag-active')) {
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
		this.initHandlebars              = function() {
            Handlebars.registerHelper('contains', function(lvalue, rvalue, options) {
                if (arguments.length < 3) {
                    throw new Error("Handlebars Helper contains needs 3 parameters");
                }
                if (!(lvalue instanceof Array)) {
                    throw new Error("The first value must be an array.");
                }

                for (var i = 0; i < lvalue.length; i++) {
                    var tag = lvalue[i];
                    if (tag == rvalue) {
                        return options.fn(this);
                    }
                }
                return options.inverse(this);
            });
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
			for (var key in tasks) {
                self.insertTask(tasks[key]);
			}
		}

        /**
         * Inserts the given task (json) in the DOM.
         * @param task  the task (json) to add in the DOM.
         */
        this.insertTask                 = function(task) {
            var html = self.todoTemplate(task);
            $('#todo-container').append(html);
        }

        /**
         * Removes a Task from the DOM.
         * @param taskId  the task id of the Task to remove from the DOM.
         */
        this.removeTask                 = function(taskId) {
            $task = $('#' + taskId);
            if ($task != undefined) {
                $task.remove();
            }
        }

        /**
         * Actives a tag of a task in the DOM.
         * @param taskId    the task id of the task for which we want to active a tag.
         * @param tag       the tag to activate
         */
        this.activeTag                  = function(taskId, tag) {
            $task = $('#' + taskId);
            var tags = $task.find('span.tag');
            for (var i = 0; i < tags.length; i++) {
                $this = $(tags[i]);
                var $a = $this.parent();
                if ($a.data('tag') == tag) {
                    $this.addClass('tag-active');
                    $this.addClass('tag-active-' + tag);
                }
            }
        }

        /**
         * De-actives a tag of a task in the DOM.
         * @param taskId    the task id of the task for which we want to active a tag.
         * @param tag       the tag to activate
         */
        this.removeTag                  = function(taskId, tag) {
            $task = $('#' + taskId);
            var tags = $task.find('span.tag-active-' + tag);
            for (var i = 0; i < tags.length; i++) {
                $this = $(tags[i]);
                $this.removeClass('tag-active');
                $this.removeClass('tag-active-' + tag);
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
		
		/**
		 * When the user has clicked on the delete icon to delete the task.
		 * @param $selector the jQuery selector of the button.
		 */
		this.deleteTask                        = function($selector) {
            if (!confirm('Are you sure to delete this task ?'))
            {
                return;
            }

            var taskId = $selector.parent().attr('id');
            self.deleteTaskAjaxCall(taskId);
		}

        /**
         * Sends an AJAX call to the given route, with the given post values then call 
         * either the done callback or the fail callback.
         * @param route         the route to call using POST
         * @param postValues    the parameters
         * @param doneCallback  the function to call when everything went fine (could be undefined)
         * @param failCallback  the function to call when something went wrong (could be undefined)
         */
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
		 * The AJAX call to delete a Task.
         * @param taskId    the task id of the Task to delete
		 */
		this.deleteTaskAjaxCall         = function(taskId) {
			var route = self.routeDeleteTask;
			var values = {
				"taskId": taskId
			}
            // sends the AJAX call.
            self.sendAjaxCall(route, values);
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

            // sends the AJAX call.
            self.sendAjaxCall(route, values);
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
            
            // sends the AJAX call.
            self.sendAjaxCall(route, values);
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

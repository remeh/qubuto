define(['TodolistQubutoWebSocket'], function(TodolistQubutoWebSocket) {
	function Todolist() {
        var TIMEOUT = 10000;

        var TASK_TRANSITION = 100;

		var self = this;
		this.websocket          = null;
		this.routeAddTask       = null;
		this.routeDeleteTask    = null;
		this.routeCloseTask     = null;
		this.routeOpenTask      = null;
		this.routeAddTag        = null;
		this.routeRemoveTag     = null;
		this.todoTemplate       = null;
	
		/**
		 * Init the todolist pages.
		 */
		this.init                       = function(routeAddTask, routeDeleteTask, routeAddTag, routeRemoveTag, routeCloseTask, routeOpenTask) {
			/*
			 * init the routes
			 */
			this.routeAddTask       = routeAddTask;
			this.routeDeleteTask    = routeDeleteTask;
			this.routeCloseTask     = routeCloseTask;
			this.routeOpenTask      = routeOpenTask;
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

            $(document).on("click", "a.task-todo", function() {
                self.closeTask($(this));
            });

            $(document).on("click", "a.task-done", function() {
                self.openTask($(this));
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

            Handlebars.registerHelper('equals', function(lvalue, rvalue, options) {
                if (arguments.length < 3) {
                    throw new Error("Handlebars Helper contains needs 2 parameters");
                }
                if (lvalue == rvalue) {
                    return options.fn(this);
                }
                return options.inverse(this);
            });

			/*
			 * Prepare the Handlebars template.
			 */
			var todoTemplateSource = $("script#todo-template").html();
			self.todoTemplate = Handlebars.compile(todoTemplateSource);
		}
		
		/**
		 * Init the tasks in the view.
		 */
		this.initTasks                  = function() {
			// these data has been inserted in the dom in the top of the page
			// by the scala view.
			for (var key in tasks) {
                var task = tasks[key];
                if (task.state == 'TODO') {
                    self.insertTask(task);
                } else if (task.state == 'DONE') {
                    self.insertDoneTask(task);
                }
			}
		}

        /**
         * Inserts the given task (json) in the DOM.
         * @param task  the task (json) to add in the DOM.
         */
        this.insertTask                 = function(task) {
            var html = self.todoTemplate(task);
            self.positionTask(html, 'TODO', task.position);
        }

        /**
         * Inserts the given task (json) in the DOM.
         * @param task  the task (json) to add in the DOM.
         */
        this.insertDoneTask             = function(task) {
            var html = self.todoTemplate(task);
            self.positionTask(html, 'DONE', task.position);
        }
        
        /**
         * Positions the HTML content at the right position.
         * @param   html        the HTML content of the task.
         * @param   destState   the destination state. (possible values : 'TODO', 'DONE')
         * @param   position    the position value of the task.
         */
        this.positionTask               = function(html, destState, position) {
            console.log(position);
            var $container = $('#' + destState.toLowerCase() + '-container');
            if ($container.children().length == 0) {
                console.log("append");
                $container.append(html);
                return;
            }

            var $todos = $container.children('.task-' + destState);
            var $pos = null;
            for (var i = 0; i < $todos.length; i++) {
                var $todo = $($todos[i]);
                if ($todo.data('position') < position) {
                    $pos = $todo;
                }
            }

            if (!$pos) {
                $container.prepend(html);
            } else {
                $pos.after(html);
            }
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

            self.hideTaskLoader(taskId);
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

            self.hideTaskLoader(taskId);
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

            self.hideTaskLoader(taskId);
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
		this.addTask                    = function($selector) {
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
		this.deleteTask                 = function($selector) {
            if (!confirm('Are you sure to delete this task ?'))
            {
                return;
            }

            var taskId = $selector.parent().attr('id');
            self.deleteTaskAjaxCall(taskId);
		}

		/**
		 * When the user has clicked on the open task icon.
		 * @param $selector the jQuery selector of the button.
		 */
		this.openTask                   = function($selector) {
            var taskId = $selector.parents('.todo-entry').first().attr('id');
            self.openTaskAjaxCall(taskId);
		}
		
		/**
		 * When the user has clicked on the close task icon.
		 * @param $selector the jQuery selector of the button.
		 */
		this.closeTask                  = function($selector) {
            var taskId = $selector.parents('.todo-entry').first().attr('id');
            self.closeTaskAjaxCall(taskId);
		}

        /**
         * Moves the Task identified by the given taskId to "Done"
         * @param   taskId  the Task id
         */
        this.moveTaskToDone             = function(taskId) {
            $('#' + taskId).fadeOut(TASK_TRANSITION);
            setTimeout(function() {
                $task = $('#' + taskId);

                self.switchTaskToDone($task);

                // move in the 'done' container at the right position.
                var position = $('#' + taskId).data('position');
                self.positionTask($task, 'DONE', position);

                $task.fadeIn(TASK_TRANSITION);
                
                // hides the task loader.
                self.hideTaskLoader(taskId);
            }, TASK_TRANSITION);
        }

        /**
         * Moves the Task identified by the given taskId to "Todo"
         * @param   taskId  the Task id
         */
        this.moveTaskToTodo             = function(taskId) {
            $('#' + taskId).fadeOut(TASK_TRANSITION);
            setTimeout(function() {
                $task = $('#' + taskId);

                self.switchTaskToTodo($task);

                // move in the 'done' container at the right position
                var position = $('#' + taskId).data('position');
                self.positionTask($task, 'TODO', position);

                $task.fadeIn(TASK_TRANSITION);
                
                // hides the task loader.
                self.hideTaskLoader(taskId);
            }, TASK_TRANSITION);
        }

        /**
         * Switches the Task to done.
         * @param   $task   jQuery selector on the task to switch in the DOM.
         */
        this.switchTaskToDone           = function($task) {
            $task.removeClass('task-TODO');
            $task.addClass('task-DONE');

            $a = $task.find('.task-todo');                        
            $a.addClass('task-done');
            $a.removeClass('task-todo');
            $i = $a.children('i');
            $i.addClass('icon-check');
            $i.removeClass('icon-check-empty');
        }

        /**
         * Switches the Task to todo.
         * @param   $task   jQuery selector on the task to switch in the DOM.
         */
        this.switchTaskToTodo           = function($task) {
            $task.removeClass('task-DONE');
            $task.addClass('task-TODO');

            $a = $task.find('.task-done');                        
            $a.addClass('task-todo');
            $a.removeClass('task-done');
            $i = $a.children('i');
            $i.addClass('icon-check-empty');
            $i.removeClass('icon-check');
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
		 * The AJAX call to open a Task.
         * @param taskId    the task id of the Task to open
		 */
		this.openTaskAjaxCall         = function(taskId) {
			var route = self.routeOpenTask;
			var values = {
				"taskId": taskId
			}

            self.showTaskLoader(taskId);

            // sends the AJAX call.
            self.sendAjaxCall(route, values);
		}
		
		/**
		 * The AJAX call to close a Task.
         * @param taskId    the task id of the Task to close
		 */
		this.closeTaskAjaxCall         = function(taskId) {
			var route = self.routeCloseTask;
			var values = {
				"taskId": taskId
			}

            self.showTaskLoader(taskId);

            // sends the AJAX call.
            self.sendAjaxCall(route, values);
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

            self.showTaskLoader(taskId);

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

            self.showTaskLoader(taskId);

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

            self.showTaskLoader(taskId);
            
            // sends the AJAX call.
            self.sendAjaxCall(route, values);
		}
		
		this.disableAddTask             = function($selector) {
			$("#add-task-title").attr('disabled','disabled');
			$("#add-task-content").attr('disabled','disabled');
			$('#add-task').attr('disabled','disabled');
            $('#modal-loader').show();

			setTimeout(function() {
				self.enableAddTask();
			}, TIMEOUT);
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

        /**
         * Shows the Task loader for the given task. (a timeout forbid the loader to be displayed constantly)
         * @param   taskId  the id of the task
         */
        this.showTaskLoader             = function(taskId) {
            $loader = $('#loader-' + taskId);
            $loader.show();
            this.timeoutHide($loader);
        }

        /**
         * Hides the Task loader for the given task.
         * @param   taskId  the id of the task
         */
        this.hideTaskLoader             = function(taskId) {
            $('#loader-' + taskId).fadeOut(50);
        }

        /**
         * Hide a DOM after the given amount of time (could be null, default : 5000 ms)
         * @param   $selector   the DOM part to hide.
         * @param   timeout     after how many ms the DOM part is automatically hide.
         */
        this.timeoutHide                = function($selector, timeout) {
            var d = TIMEOUT;
            if (timeout != undefined) {
                d = timeout;
            }
            setTimeout(function() {
                $selector.fadeOut();
            },d); 
        }
	}
	
	return Todolist;
});

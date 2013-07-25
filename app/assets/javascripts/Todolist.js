define(['TodolistQubutoWebSocket'], function(TodolistQubutoWebSocket) {
	function Todolist(routeAddTask, routeDeleteTask, routeAddTag, routeRemoveTag, routeCloseTask, routeOpenTask, routeAddComment, routeDeleteComment) {
        var TIMEOUT = 10000;

        var LONG_DURATION   = 10000;
        var SHORT_DURATION  = 2000;

        var TASK_TRANSITION = 100;

		var self = this;

		this.websocket          = null;
		this.routeAddTask       = null;
		this.routeDeleteTask    = null;
		this.routeCloseTask     = null;
		this.routeOpenTask      = null;
		this.routeAddTag        = null;
		this.routeRemoveTag     = null;
		this.routeAddComment    = null;
		this.routeDeleteComment = null;

		this.todoTemplate       = null;
		this.commentTemplate    = null;

        /**
         * Username of the currently logged user.
         */
        this.username           = null;

        /**
         * On which tags the view is currently filtering.
         */
        this.filterTags         = [];
	
		/**
		 * Init the todolist pages.
		 */
		this.construct                  = function(routeAddTask, routeDeleteTask, routeAddTag, routeRemoveTag, routeCloseTask, routeOpenTask, routeAddComment, routeDeleteComment) {
			/*
			 * init the routes
			 */
			this.routeAddTask       = routeAddTask;
			this.routeDeleteTask    = routeDeleteTask;
			this.routeCloseTask     = routeCloseTask;
			this.routeOpenTask      = routeOpenTask;
			this.routeAddTag        = routeAddTag;
			this.routeRemoveTag     = routeRemoveTag;
			this.routeAddComment    = routeAddComment;
			this.routeDeleteComment = routeDeleteComment;

            this.username           = $('.username').data('username');
			
			this.initWebsocket();
			
			/*
			 * Prepare the handlebars templates.
			 */
			this.initHandlebars();

            this.initNotify();
			
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
				self.addTask();
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

            $(document).on("click", "a.filter-tag", function() {
                self.filterTagClick($(this));
            });

            $(document).on("click", "button.task-comments", function() {
                self.showComments($(this));
            });

            $(document).on("click", ".add-comment", function() {
                self.showFormComment($(this));
            });

            $(document).on("keydown", "input.comment", function(event) {
                self.keypressComment($(this), event);
            });

            $(document).on("click", ".comment-remove", function() {
                self.deleteComment($(this));
            });

            $(document).on("keyup", "#filter-content", function(event) {
                self.keypressFilterContent($(this), event);
            });

            $(document).on("click", "#remove-filter-content", function() {
                self.removeFilterContent($(this));
            });

            $(document).on("keydown", "#add-task-title", function(event) {
                if (event.keyCode == 13) {
                    self.addTask();
                    event.preventDefault();
                    return false;
                }
            });
        }

        /**
         * Configures the Notifyjs lib default behavior.
         */
        this.initNotify                 = function() {
            $.notify.defaults({
                style: "meh",
                elementPosition: 'top right',
                globalPosition: 'top right',
                autoHideDelay: LONG_DURATION,
                showAnimation: "fadeIn",
                hideAnimation: "fadeOut",
                showDuration: TASK_TRANSITION,
                arrowShow: false
                });
        }

        /**
         * Triggered when the user remove the "filter content"
         * @param $a            the jQuery selector of the a
         */
        this.removeFilterContent        = function($a) {
            $('#filter-content').val('');
            $('#remove-filter-content').hide();
            self.applyFilterContent('');
        }

        /**
         * Triggered when the user has typed something to filter on the content.
         * @param $input        the jQuery selector of the input
         * @param event         the received event
         */
        this.keypressFilterContent      = function($input, event) {
            var val = $input.val();
            if (val == 0) {
                $('#remove-filter-content').hide();
            } else {
                $('#remove-filter-content').show();
            }
            self.applyFilterContent($input.val().toLowerCase());
        }

        this.applyFilterContent         = function(content) {
            var $tasks          = $('.todo-entry:visible');
            var $hiddenTasks    = $('.todo-entry:hidden');
            
            // compute every task to hide
            var tasksToHide = [];
            for (var i = 0; i < $tasks.length; i++) {
                var $task = $($tasks[i]);
                if ($('#task-content-' + $task.attr('id')).text().toLowerCase().indexOf(content) == -1) {
                    tasksToHide.push($task);
                }
            }
            
            // compute every task to re-show
            var tasksToShow = [];
            for (var i = 0; i < $hiddenTasks.length; i++) {
                var $task = $($hiddenTasks[i]);
                if ($('#task-content-' + $task.attr('id')).text().toLowerCase().indexOf(content) != -1) {
                    tasksToShow.push($task);
                }
            }

            for (var i = 0; i < tasksToHide.length; i++) {
                tasksToHide[i].fadeOut(TASK_TRANSITION);
            }
            for (var i = 0; i < tasksToShow.length; i++) {
                tasksToShow[i].fadeIn(TASK_TRANSITION);
            }
        }

        /**
         * The AJAX call to delete a Comment.
         * @param commentId     the comment id of the Comment to delete
         */
        this.deleteCommentAjaxCall      = function(taskId, commentId) {
            var route = self.routeDeleteComment;
            var values = {
                "commentId": commentId
            }

            self.showTaskLoader(taskId);

            // sends the AJAX call.
            sendAjaxCall(route, values);
		}

        /**
         * Removes a Comment from the DOM.
         * @param taskId        the task id of the Task containing the comment.
         * @param comment       the json comment.
         * @param user          the user who removed the comment.
         */
        this.removeComment              = function(task, comment, user) {
            if (task == undefined || comment == undefined) {
                return;
            }

            var taskId = task.id;

            $comment = $('#' + comment.id);
            if ($comment != undefined) {
                $comment.remove();
            }

            if (!self.isUser(user)) {
                $('#task-comments-'+taskId).notify(
                        '<em>'+user+'</em> removed a comment', {
                        autoHideDelay: SHORT_DURATION 
                });
            }

            // increments the comments count
            $span = $('#task-comments-' + taskId).find('span');
            var val = parseInt($span.text(),10) - 1;
            if (val < 0) {
                val = 0;
            }
            $span.text(val);

            self.hideTaskLoader(taskId);
        }
		
        /**
         * When a clicked to delete a comment has been done.
         * @param   $selector   the a jQuery selector
         */
        this.deleteComment              = function($selector) {
            if (!confirm('Are you sure to delete this comment ?'))
            {
                return;
            }

            var taskId = $selector.parents('.todo-entry').first().attr('id');

            var commentId = $selector.parents('.comment').first().attr('id');
            self.deleteCommentAjaxCall(taskId, commentId);
        }

        /**
         * When a key has been pressed while typing in a Comment input.
         * @param   $selector   the input jQuery selector
         * @param   event       the keypress event.
         */
        this.keypressComment            = function($selector, event) {
             if (event.which == 13) {
                 self.addComment($selector);
                 event.preventDefault();
             }
        }

        /**
         * Adds a Comment to a Task.
         * @param   $selector   the jQuery selector of the button.
         */
        this.addComment                = function($selector) {
            var taskId = $selector.parents('.todo-entry').first().attr('id');

            if (taskId == undefined) {
                return;
            }

            // retrieves the content
            var content = $('#comment-' + taskId).val();

            var $commentsContainer = $('#comments-container-' + taskId);
            $commentsContainer.find('.form-comment').hide();
            $commentsContainer.find('.add-comment').fadeIn(TASK_TRANSITION);

            self.sendCommentAjaxCall(taskId, content);
        }

        /**
         * Inserts the comment in the given Task.
         * @param   taskId      the Task in which we want to insert the comment.
         * @param   comment     the json comment to append.
         */
        this.insertComment              = function(task, comment) {
            if (task == undefined || comment == undefined) {
                return;
            }

            var taskId = task.id;
            var html = self.commentTemplate(comment);
            $('#' + taskId).find('.add-comment').before(html);

            if (!self.isUser(comment.author)) {
                $('#task-comments-'+taskId).notify(
                        '<em>'+comment.author+'</em> added a comment', {
                        autoHideDelay: SHORT_DURATION 
                });
            }

            // increments the comments count
            $span = $('#task-comments-' + taskId).find('span');
            val = parseInt($span.text(),10) + 1;
            $span.text(val);
            
            // hides the task loader.
            self.hideTaskLoader(taskId);
        }

        /**
         * Sends the AJAX request to add the Comment.
         * @param   taskId      on which task the comment is added
         * @param   content     the content of the Comment to add.
         */
        this.sendCommentAjaxCall        = function(taskId, content)
        {
			var route = self.routeAddComment;
			var values = {
                "taskId": taskId,
                "content": content
			}

            self.showTaskLoader(taskId);
            
            // sends the AJAX call.
            sendAjaxCall(route, values);
        }

        /**
         * Display the edit to add a comment.
         * @param   $selector   the jQuery selector of the div.
         */
        this.showFormComment            = function($selector) {
            var taskId = $selector.parents('.todo-entry').first().attr('id');

            if (taskId == undefined) {
                return;
            }

            $selector.hide();
            $('#comment-' + taskId).val('');
            $('#form-comment-'+taskId).fadeIn();
        }

        /**
         * Display the comments for a Task.
         * @param   $selector   the jQuery selector of the button.
         */
        this.showComments               = function($selector) {
            var taskId = $selector.parents('.todo-entry').first().attr('id');

            if (taskId == undefined) {
                return;
            }

            $selector.fadeOut(TASK_TRANSITION);
            $commentsContainer = $('#comments-container-'+taskId);
            $commentsContainer.slideDown(TASK_TRANSITION);
        }
        
        /**
         * Called when an a user click on a tag in filters.
         * @param   $selector   the jQuery selector of the selected tag.
         */
        this.filterTagClick             = function($selector) {
            var tag = $selector.data('tag');
            var $span = $selector.children('span');
            if ($span.hasClass('tag-active')) {
                self.removeFilterTag($span);
            } else {
                self.addFilterTag($span);
            }
        }

        /**
         * Called when an a user click on a tag in filters to remove a filter on tags.
         * @param   $selector   the jQuery selector of the selected tag.
         */
        this.removeFilterTag            = function($selector) {
            var tag = $selector.parent().data('tag');
            $selector.removeClass('tag-active');
            $selector.removeClass('tag-active-'+tag);

            for (var i = 0; i < self.filterTags.length; i++) {
                if (self.filterTags[i] == tag) {
                    self.filterTags.splice(i, 1);
                    break;
                }
            }

            self.applyFilterTags();
        }

        /**
         * Called when an a user click on a tag in filters to add a filter on tags.
         * @param   $selector   the jQuery selector of the selected tag.
         */
        this.addFilterTag               = function($selector) {
            var tag = $selector.parent().data('tag');
            $selector.addClass('tag-active');
            $selector.addClass('tag-active-' + tag);
            self.filterTags.push(tag);

            self.applyFilterTags();
        }

        /**
         * Applies the filter on tags.
         */
        this.applyFilterTags            = function() {
            var $tasks          = $('.todo-entry:visible');
            var $hiddenTasks    = $('.todo-entry:hidden');
            
            // compute every task to hide
            var tasksToHide = [];
            for (var i = 0; i < $tasks.length; i++) {
                var $task = $($tasks[i]);
                for (var j = 0; j < self.filterTags.length; j++) {
                    var tag = self.filterTags[j];
                    if ($task.children().find('.tag-active-' + tag).length == 0) {
                        tasksToHide.push($task);
                        break;
                    }
                }
            }
            
            // compute every task to re-show
            var tasksToShow = [];
            for (var i = 0; i < $hiddenTasks.length; i++) {
                var $task = $($hiddenTasks[i]);
                var hidden = false;
                for (var j = 0; j < self.filterTags.length; j++) {
                    var tag = self.filterTags[j];
                    if ($task.children().find('.tag-active-' + tag).length == 0) {
                        hidden = true;
                    }
                }
                if (!hidden) {
                    tasksToShow.push($task);
                }
            }

            for (var i = 0; i < tasksToHide.length; i++) {
                tasksToHide[i].fadeOut(TASK_TRANSITION);
            }
            for (var i = 0; i < tasksToShow.length; i++) {
                tasksToShow[i].fadeIn(TASK_TRANSITION);
            }
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
            var commentTemplateSource = $("script#comment-template").html();
			self.commentTemplate = Handlebars.compile(commentTemplateSource);
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
                    self.insertTask(task, true);
                } else if (task.state == 'DONE') {
                    self.insertDoneTask(task);
                }
			}
		}

        /**
         * Tests whether the current user is the given one. Based on the username.
         * @param username      the username to compare to.
         * @return true if the current user is the same as the given one.
         */
        this.isUser                     = function(username) {
            if (username == undefined) {
                return false;
            }
            return self.username == username;
        }

        /**
         * Inserts the given task (json) in the DOM.
         * @param task      the task (json) to add in the DOM.
         * @param startup   if this insertion is for the loading of the page
         */
        this.insertTask                 = function(task, startup) {
            var html = self.todoTemplate(task);

            if (startup != true) {
                if (!self.isUser(task.author)) {
                    $.notify('<em>'+task.author+'</em> created the task : <strong>' + task.title + '</strong>');
                }
            }

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
            var $container = $('#' + destState.toLowerCase() + '-container');
            if ($container.children().length == 0) {
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
         * @param task      the json of the removed task.
         * @param user      the username of the user which has removed the task
         */
        this.removeTask                 = function(task, user) {
            $task = $('#' + task.id);
            if ($task != undefined) {
                $task.remove();
            }

            if (!self.isUser(user)) {
                $.notify('<em>'+user+'</em> deleted the task : <strong>' + task.title + '</strong>');
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
		 */
		this.addTask                    = function() {
			/*
			 * Retrieve values
			 */
			var title = $("#add-task-title").val();
			var content = $("#add-task-content").val();
			
			/*
			 * If values not null, make the ajax call.
			 */
			if (title != undefined && title.length > 0) {
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
        this.moveTaskToDone             = function(task, username) {
            if (task == undefined) {
                return;
            }
            var taskId = task.id;
            $('#' + taskId).fadeOut(TASK_TRANSITION);
            setTimeout(function() {
                $task = $('#' + taskId);

                self.switchTaskToDone($task);

                // move in the 'done' container at the right position.
                var position = $('#' + taskId).data('position');
                self.positionTask($task, 'DONE', position);

                $task.fadeIn(TASK_TRANSITION);

                // notifications
                if (!self.isUser(username)) {
                    $.notify('<em>'+username+'</em> closed the task : <strong>' + task.title + '</strong>');
                }
                
                // hides the task loader.
                self.hideTaskLoader(taskId);
            }, TASK_TRANSITION);
        }

        /**
         * Moves the Task identified by the given taskId to "Todo"
         * @param   taskId  the Task id
         */
        this.moveTaskToTodo             = function(task, username) {
            if (task == undefined) {
                return;
            }
            var taskId = task.id;
            $('#' + taskId).fadeOut(TASK_TRANSITION);
            setTimeout(function() {
                $task = $('#' + taskId);

                self.switchTaskToTodo($task);

                // move in the 'done' container at the right position
                var position = $('#' + taskId).data('position');
                self.positionTask($task, 'TODO', position);

                $task.fadeIn(TASK_TRANSITION);
                
                // notifications
                if (!self.isUser(username)) {
                    $.notify('<em>'+username+'</em> re-opened the task : <strong>' + task.title + '</strong>');
                }
                
                // hides the task loader.
                self.hideTaskLoader(taskId);
            }, TASK_TRANSITION);
        }

        /**
         * Switches the Task to done.
         * @param   $task   jQuery selector on the task to switch in the DOM.
         */
        this.switchTaskToDone           = function($task) {
            // Changes the type of the task.
            $task.removeClass('task-TODO');
            $task.addClass('task-DONE');

            // Changes the todo/done action and icons.
            $a = $task.find('.task-todo');                        
            $a.addClass('task-done');
            $a.removeClass('task-todo');
            $i = $a.children('i');
            $i.addClass('icon-check');
            $i.removeClass('icon-check-empty');

            // Hides the comments whether there were opened.
            $task.find('.comments-container').hide();
            $task.find('.task-comments').hide();
        }

        /**
         * Switches the Task to todo.
         * @param   $task   jQuery selector on the task to switch in the DOM.
         */
        this.switchTaskToTodo           = function($task) {
            // Change the type of the task.
            $task.removeClass('task-DONE');
            $task.addClass('task-TODO');

            // Changes the todo/done action and icons.
            $a = $task.find('.task-done');                        
            $a.addClass('task-todo');
            $a.removeClass('task-done');
            $i = $a.children('i');
            $i.addClass('icon-check-empty');
            $i.removeClass('icon-check');

            $task.find('.task-comments').fadeIn(TASK_TRANSITION);
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
            sendAjaxCall(route, values, doneCallback, failCallback);
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
            sendAjaxCall(route, values);
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
            sendAjaxCall(route, values);
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
            sendAjaxCall(route, values);
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
            sendAjaxCall(route, values);
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
            sendAjaxCall(route, values);
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

        // ---------------------- 
        
        self.construct(routeAddTask, routeDeleteTask, routeAddTag, routeRemoveTag, routeCloseTask, routeOpenTask, routeAddComment, routeDeleteComment);
	}
	
	return Todolist;
});

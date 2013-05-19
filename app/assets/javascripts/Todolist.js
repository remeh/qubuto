define(['TodolistQubutoWebSocket'], function(TodolistQubutoWebSocket) {
	function Todolist() {
		var self = this;
		this.websocket = null;
		this.routeAddTask = null;
		this.todoTemplate = null;
	
		/**
		 * Init the todolist pages.
		 */
		this.init = function(routeAddTask) {
			/*
			 * init the routes
			 */
			this.routeAddTask = routeAddTask;
			
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
			 * Bind events.
			 */
			$(document).on("click", "#btn-create-task", function() {
				self.newTask();
			});
			
			$(document).on("click", "#add-task", function() {
				self.addTask($(this));
			});
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
		this.initTasks = function() {
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
		this.newTask = function() {
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
		this.addTask = function($selector) {
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
		 * The AJAX call to create a Task.
		 * @param serializedForm the serialized form (for parameters values).
		 */
		this.addTaskAjaxCall = function(title, content) {
			var route = self.routeAddTask;
			var values = {
				"content": content,
				"title": title
			}
			
			$.ajax({
				type: "POST",
				url: route,
				data: values
				})
				.done(function(data) {
					if (data != null) {
						var json = JSON.parse(data);
						if (json.error != 0) {
							alert("Error: " + json.message);
						}
					}
					// re-enable inputs
					self.enableAddTask();
					// close the modal
					$.modal.close();
					
				})
				.fail(function(jqxhr) {
					self.enableAddTask();
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
		
		this.disableAddTask = function($selector) {
			$("#add-task-title").attr('disabled','disabled');
			$("#add-task-content").attr('disabled','disabled');
			$('#add-task').attr('disabled','disabled');
			setTimeout(function() {
				self.enableAddTask();
			}, 30000);
		}
		
		this.enableAddTask = function() {
			$("#add-task-title").removeAttr('disabled');
			$("#add-task-content").removeAttr('disabled');
			$('#add-task').removeAttr('disabled');
		}
		
		this.initWebsocket = function() {
			// websocketUri comes from the DOM.
			if (websocketUri != undefined) {
				this.websocket = new TodolistQubutoWebSocket(this);
				this.websocket.open(websocketUri);
			}
		}
		
		/**
		 * Clears the input of the "Add task" feature.
		 */
		this.clearInputs = function() {
			$("#add-task-title").val('');
			$("#add-task-content").val('');
		}
	}
	
	return Todolist;
});
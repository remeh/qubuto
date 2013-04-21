define(['TodolistQubutoWebSocket'], function(TodolistQubutoWebSocket) {
	function Todolist() {
		var self = this;
		this.websocket = null;
	
		/**
		 * Init the todolist pages.
		 */
		this.init = function(/* routes */) {
			/*
			 * init the routes
			 */
			
			this.initWebsocket();
			
			$(document).on("click", "#btn-create-task", function() {
				self.newTask();
			});
			
			$(document).on("click", "#add-task", function() {
				self.addTask($(this));
			});
		}
		
		/**
		 * When an user want to create a new task.
		 */
		this.newTask = function() {
			// show the popup
			$("div#new-task").modal({
				'containerCss': { 'min-width': '350px' },
				'overlayClose': true,
				'closeClass': 'close-modal'
			});
		}
		
		/**
		 * When the user has clicked on the Add button to create the task.
		 * @param $selector the jQuery selector of the button.
		 */
		this.addTask = function($selector) {
			var title = $("#add-task-title").val();
			var content = $("#add-task-content").val();
			if (content != undefined && content.length > 0
			  && title != undefined && title.length > 0) {
				self.disableAddTask();
				// TODO call to add the task
			}
			// TODO else ?
		}
		
		this.disableAddTask = function($selector) {
			$("#add-task-title").attr('disabled','disabled');
			$("#add-task-content").attr('disabled','disabled');
			$('#add-task').attr('disabled','disabled');
			setTimeout(function() {
				$("#add-task-title").removeAttr('disabled');
				$("#add-task-content").removeAttr('disabled');
				$('#add-task').removeAttr('disabled');
			}, 5000);
		}
		
		this.initWebsocket = function() {
			// websocketUri comes from the DOM.
			if (websocketUri != undefined) {
				this.websocket = new TodolistQubutoWebSocket(this);
				this.websocket.open(websocketUri);
			}
		}
	}
	
	return Todolist;
});
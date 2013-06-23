define([], function() {
	function ListProject() {
		var self = this;

        this.projectTemplate            = null;

        this.bindEvents                 = function() {
            // Nothing to bind.
        }

		this.initHandlebars             = function() {
			/*
			 * Prepare the Handlebars template.
			 */
			var projectTemplateSource = $("script#project-template").html();
			self.projectTemplate = Handlebars.compile(projectTemplateSource);
        }

        this.showProjects               = function() {
            // Note: projects and sharedProjects come from the DOM.
            self.renderAndInsertProjects(projects, $('div.your-projects-container'));
            self.renderAndInsertProjects(sharedProjects, $('div.shared-projects-container'));

            if (projects.length == 0) {
                // TODO
            }
            if (sharedProjects.length == 0) {
                var span = $('<span>').addClass('gray').addClass('project-title').text('You\'re not contributing to any project.');
                $('div.shared-projects-container').append(span);
            }
        }

        this.renderAndInsertProjects    = function(projects, $selector) {
            for (var i = 0; i < projects.length; i++) {
                var html = self.projectTemplate(projects[i]);
                $selector.append(html);
            }
        }

        // ---------------------- 
        
        this.construct                  = function() {
            self.initHandlebars();
            self.bindEvents();
            // show the projects
            self.showProjects();
        }

        self.construct();
	}
	
	return ListProject;
});



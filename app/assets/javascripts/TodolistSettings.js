define([], function() {
	function TodolistSettings() {
		var self = this;

        this.bindEvents                 = function() {
            $(document).on("keyup", "input.tag", function(event) {
                self.updateTag($(this));
            });
        }

        this.updateTag                  = function($input) {
            var id = $input.attr('id');
            $('#preview-'+id).text($input.val());
        }

        // ---------------------- 
        
        this.construct                  = function() {
            self.bindEvents();
        }

        self.construct();
	}
	
	return TodolistSettings;
});



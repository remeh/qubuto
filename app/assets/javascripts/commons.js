/**
 * Sends an AJAX call to the given route, with the given post values then call 
 * either the done callback or the fail callback.
 * @param route         the route to call using POST
 * @param postValues    the parameters
 * @param doneCallback  the function to call when everything went fine (could be undefined)
 * @param failCallback  the function to call when something went wrong (could be undefined)
 */
function sendAjaxCall(route, postValues, doneCallback, failCallback) {
    $.ajax({
        type: 'POST',
        url: route,
        data: postValues,
        dataType: "json"
        })
        .done(function(data) {
            if (data.error != 0) {
                alert("Error: " + json.message);
            }
            if (doneCallback != undefined) {
                doneCallback(data);
            }
        })
        .fail(function(jqxhr) {
            if (jqxhr != null) {
                json = undefined;
                json = JSON.parse(jqxhr.responseText);

                if (failCallback != undefined) {
                    failCallback(json);
                }

                if (json.error == 1) { // NOT_AUTHENTICATED
                    alert("You're not authenticated or your session has expired.");
                    document.location.href = "/login";
                } else {
                    // Do something only if there is no custom fail callback
                    if (failCallback == undefined) {
                        if (json.message != undefined) {
                            alert("A problem occurred : " + json.message);
                        } else { 
                            alert("An unknown error occurred.");
                        }
                    }
                }
            }
        });
}

// ---------------------- 

$(function() {
	$(document).on("click", 'a[href="#"]', function(e) {
	     return false;
	     e.preventDefault();
	});
});


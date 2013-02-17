import java.util.concurrent.TimeUnit;

import com.mehteor.qubuto.session.SessionCleanJob;

import controllers.Application;

import play.libs.Akka;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Http.RequestHeader;

import play.*;
import play.api.libs.concurrent.Execution;
import scala.concurrent.duration.Duration;

public class Global extends GlobalSettings {
	
    @Override
    public void onStart(play.Application app) {
        Logger.info("Application starting...");
        // Launch the session job
    	scheduleSessionTask();
    }  

    @Override
    public void onStop(play.Application app) {
        Logger.info("Application shutdown...");
    }  

    @Override
    public Result onHandlerNotFound(RequestHeader header) {
    	return Results.notFound(Application.renderNotFound());
    }
    
    // ---------------------
    
	public void scheduleSessionTask() {
		Akka.system().scheduler().schedule(
					Duration.create(0, TimeUnit.MILLISECONDS),
					Duration.create(60, TimeUnit.SECONDS),
					new SessionCleanJob(),
					Execution.defaultContext()
				); 
	}
}

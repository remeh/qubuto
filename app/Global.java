import java.util.concurrent.TimeUnit;

import com.mehteor.qubuto.session.SessionCleanJob;

import play.libs.Akka;

import play.*;
import play.api.libs.concurrent.Execution;
import scala.concurrent.duration.Duration;

public class Global extends GlobalSettings {
	
    @Override
    public void onStart(Application app) {
        // Launch the session job
    	scheduleSessionTask();
    }  

    @Override
    public void onStop(Application app) {
        Logger.info("Application shutdown...");
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

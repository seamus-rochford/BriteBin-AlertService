package com.trandonsystems.britebin;

import java.io.File;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.services.AlertServices;
import com.trandonsystems.britebin.services.JavaMailServices;
import com.trandonsystems.britebin.services.SmsServices;


public class BriteBinAlertService {

	static Logger log = Logger.getLogger(BriteBinAlertService.class);
	
	// Set the scheduling parameters
	private static int intervalDelay= 1 * 60 * 1000; // Repeat every 5 minutes 
		
	public static void main(String[] args) {

        log.info("BriteBinAlertService started ... ");
        
        String workingDir = System.getProperty("user.dir");
		log.info("Working Directory = " + workingDir);
		
		String logoFileName = System.getProperty("user.dir") + "/logo.png";
		File logoImage = new File(logoFileName);
		
		if(!(logoImage.exists() && !logoImage.isDirectory())) { 
		    log.error("logo image missing from working directory: " + workingDir);
		}
		
        try {        	 
			JavaMailServices.initializeEmailer();
			SmsServices.initializeSms();
 
			AlertServices alertServices = new AlertServices();

			while (true) {
            	log.info("Processing waiting alerts ...");

    			alertServices.processWaitingAlerts();

    			// Wait set period before attempting to process any more alerts
            	Thread.sleep(intervalDelay);
            }
 
        } catch (Exception ex) {
            log.error("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }		

        log.info(" ... BriteBinAlertService Terminated ");		
	}

}

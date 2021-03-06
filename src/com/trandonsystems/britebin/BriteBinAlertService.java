package com.trandonsystems.britebin;

import java.io.File;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.services.AlertServices;
import com.trandonsystems.britebin.services.GuestServices;
import com.trandonsystems.britebin.services.JavaMailServices;
import com.trandonsystems.britebin.services.PushNotificationServices;
import com.trandonsystems.britebin.services.SmsServices;
import com.trandonsystems.britebin.services.UnitServices;


public class BriteBinAlertService {

	static Logger log = Logger.getLogger(BriteBinAlertService.class);
	
	// Set the scheduling parameters
	private static int intervalDelay= 60 * 1000; // Repeat every 1 minutes 
		
	public static void main(String[] args) {

        log.info("BriteBinAlertService started ... ");
        
        String workingDir = System.getProperty("user.dir");
		log.info("Working Directory = " + workingDir);
		
		String logoFileName = System.getProperty("user.dir") + "/logo.png";
		File logoImage = new File(logoFileName);
		
		if(!(logoImage.exists() && !logoImage.isDirectory())) { 
		    log.error("logo image (logo.png) missing from working directory: " + workingDir);
		}
		
        try {
        	
        	// Check if any units are not reporting - this is scheduled to check every 6 hours
        	// The following call appear to block the rest of this service from running
        	// Instead I call the UnitService.checkUnits every time I go around the loop
//        	UnitServices.scheduleCheckUnits();
        	
			JavaMailServices.initializeEmailer();
			SmsServices.initializeSms();
			PushNotificationServices.initializePushNotification();
 
			AlertServices alertServices = new AlertServices();

			int checkUnitsReportingCount = 0;
			while (true) {
            	
            	log.info("Processing waiting alerts ...");

    			alertServices.processWaitingAlerts();

    			checkUnitsReportingCount++;
    			if (checkUnitsReportingCount == 60) {
    				// Only check if units are reporting once every 60 iterations
    				UnitServices.checkUnits();
    				checkUnitsReportingCount = 0;
    			}
    			
    			// duplicate GuestReadings
//    			GuestServices.duplicateGuestReadings();
    			
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

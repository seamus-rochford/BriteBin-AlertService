package com.trandonsystems.britebin;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.services.AlertServices;
import com.trandonsystems.britebin.services.JavaMailServices;


public class BriteBinAlertService {

	static Logger log = Logger.getLogger(BriteBinAlertService.class);
	static AlertServices alertServices = new AlertServices();
	
	// Set the scheduling parameters
	private static int intervalDelay= 5 * 60 * 100; // Repeat every 5 minutes 
		
	public static void main(String[] args) {

        log.info("BriteBinAlertService started ... ");
		
        try {        	 
            log.info("Server is listening on port ");
			JavaMailServices.initializeEmailer();
 
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

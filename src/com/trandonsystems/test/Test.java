package com.trandonsystems.test;

import com.trandonsystems.britebin.services.AlertServices;
import com.trandonsystems.britebin.services.JavaMailServices;
import org.apache.log4j.Logger;

public class Test {

	public static void main(String[] args) {
		
		final Logger log = Logger.getLogger(Test.class);
		
		String envName= System.getenv("ENV_NAME");
		log.info("ENV_NAME: " + envName);

		log.info("Test Emails started ... ");
		
        try {        	 
            log.info("Initialize emailer ");
			JavaMailServices.initializeEmailer();
 
//			log.info("Send email");
//			JavaMailServices.sendMail("serochfo@gmail.com", "Test BriteBin Emailer", false, "Test BriteBin Emailer body");
			
			AlertServices alertServices = new AlertServices();
			
			log.info("process waiting alerts");
			alertServices.processWaitingAlerts();
			
        } catch (Exception ex) {
            log.error("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }		

        log.info(" ... Test Emails Terminated ");		
	}

}

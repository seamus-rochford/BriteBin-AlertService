package com.trandonsystems.britebin.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.UnitDAL;
import com.trandonsystems.britebin.model.UnitReading;

public class UnitServices {

	static Logger log = Logger.getLogger(UnitServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	
	public static void checkUnits() {
		
		try {
			log.info("UnitServices.checkUnits()");

			List<UnitReading> unitReadings = UnitDAL.getLatestReadings();
			
			for (UnitReading unitReading : unitReadings) {
				
				// Get the number of hours since last reading
				Duration duration = Duration.between(unitReading.readingDateTime, Instant.now());
				long hoursSinceLastReading = duration.toHours();
				
				// Check device Type
				if (unitReading.unit.deviceType.id == 1) {
					// Tekelek sensor - it reports every 24 hours - report if no reading after 73 Hours i.e. 3 missed readings
					
					if (hoursSinceLastReading > 73) {
						// Check if alert in past 24 hours - only report this once a day
						if (UnitDAL.getNoReportingAlertCount24Hours(unitReading.unit.id) == 0) {
							log.debug("Tekelek Unit: " + unitReading.unit.id + " not reporting since " + new Date());
							UnitDAL.saveAlert(unitReading.unit.id, unitReading.id, "Tekelek unit not reporting in past 73 hours");
						}
					}

				} else if (unitReading.unit.deviceType.id == 2) {
					// BriteBin Sensor - it reports multiple times per hour - report if no readings after 24 hour
				
					if (hoursSinceLastReading > 24) {
						// Check if alert flagged in past 24 hours - only report this once a day
						if (UnitDAL.getNoReportingAlertCount24Hours(unitReading.unit.id) == 0) {
							log.debug("briteBin Unit: " + unitReading.unit.id + " not reporting since " + new Date());
							UnitDAL.saveAlert(unitReading.unit.id, unitReading.id, "BriteBin unit not reporting in past 24 hours");
						}
					}
				}
			}
		} catch (Exception ex) {
			log.error("UnitServices.checkUnits Exception: " + ex.getMessage());
			log.error(ex.getStackTrace());
		}
	}
	
//	public static void scheduleCheckUnits() {
//	    TimerTask repeatedTask = new TimerTask() {
//	        public void run() {
//	            log.info("Schedule check if units reporting " + new Date());
//	            checkUnits();
//	        }
//	    };
//	    Timer timer = new Timer("Timer");
//	    
//	    long delay = 1000L;
////	    long period = 1000L * 60L;  // Check every 1 minute
//	    long period = 1000L * 60L * 60L;  // Check every 1 hour
////	    long period = 1000L * 60L * 60L * 6L;  // Check every 6 hour
//	    timer.scheduleAtFixedRate(repeatedTask, delay, period);
//	    log.info("Scheduler configured");
//	}
}

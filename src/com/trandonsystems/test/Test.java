package com.trandonsystems.test;

import com.trandonsystems.britebin.services.AlertServices;
import com.trandonsystems.britebin.services.JavaMailServices;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

public class Test {

	public static Logger log = Logger.getLogger(Test.class);
	
    public static final boolean SORT_BY_REGION = false;

    private static Map<String, String> getAllZoneIdsAndItsOffSet() {

        Map<String, String> result = new HashMap<>();

        LocalDateTime localDateTime = LocalDateTime.now();

        for (String zoneId : ZoneId.getAvailableZoneIds()) {

            ZoneId id = ZoneId.of(zoneId);

            // LocalDateTime -> ZonedDateTime
            ZonedDateTime zonedDateTime = localDateTime.atZone(id);

            // ZonedDateTime -> ZoneOffset
            ZoneOffset zoneOffset = zonedDateTime.getOffset();

            //replace Z to +00:00
            String offset = zoneOffset.getId().replaceAll("Z", "+00:00");

            result.put(id.toString(), offset);

        }

        return result;

    }
    
    private static void getTimeZones() {
        Map<String, String> sortedMap = new LinkedHashMap<>();

        Map<String, String> allZoneIdsAndItsOffSet = getAllZoneIdsAndItsOffSet();

        //sort map by key
        if (SORT_BY_REGION) {
            allZoneIdsAndItsOffSet.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));
        } else {
            // sort by value, descending order
            allZoneIdsAndItsOffSet.entrySet().stream()
                    .sorted(Map.Entry.<String, String>comparingByValue().reversed())
                    .forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));
        }

        // print map
        sortedMap.forEach((k, v) ->
        {
            String out = String.format("%35s (UTC%s) %n", k, v);
            System.out.printf(out);
        });

        System.out.println("\nTotal Zone IDs " + sortedMap.size());
        System.out.println("\n\n");
	}
    
    private static void testTimeZones() {
		Instant instant = Instant.now();
		System.out.println("UTC Time: " + instant);
		
		Locale locale = new Locale("hr", "HR");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", locale).withZone(ZoneId.systemDefault());
		System.out.println("   >>> " + formatter.format(instant));
		
		ZoneId dublin = ZoneId.of("Europe/Dublin");
		System.out.println("\nDublin: " + dublin);
		locale = new Locale("en", "IE");
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", locale).withZone(dublin);
		System.out.println("   >>> " + formatter.format(instant));
		
		ZoneId zagreb = ZoneId.of("Europe/Zagreb");
		System.out.println("\nZagreb: " + zagreb.toString());
		locale = new Locale("hr", "HR");
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", locale).withZone(zagreb);
		System.out.println("   >>> " + formatter.format(instant));
		
		ZoneId paris = ZoneId.of("Europe/Paris");
		System.out.println("\nParis: " + paris);
		locale = new Locale("fr", "FR");
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", locale).withZone(paris);
		System.out.println("   >>> " + formatter.format(instant));
		
		ZoneId oslo = ZoneId.of("Europe/Oslo");
		System.out.println("\nOslo: " + oslo);
		locale = new Locale("no", "NO");
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", locale).withZone(oslo);
		System.out.println("   >>> " + formatter.format(instant));
		
		ZoneId newYork = ZoneId.of("America/New_York");
		System.out.println("\nNew York: " + newYork);
		locale = new Locale("en", "US");
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", locale).withZone(newYork);
		System.out.println("   >>> " + formatter.format(instant));
    }
    
	public static String stripPhoneNo(String phoneNo) {
		
		// Remove all except digits
		String newPhoneNumber = phoneNo.replaceAll("[^0-9]", "");
		
		// Remove leading zeros
		newPhoneNumber = newPhoneNumber.replaceAll("^0+(?=.)", "");
		
		return newPhoneNumber;
	}
	
	public static void main(String[] args) {
		
		String envName= System.getenv("ENV_NAME");
		log.info("ENV_NAME: " + envName);

//		getTimeZones();
//		
//		testTimeZones();
		
		String phoneNo = "+00353.87.2646379";
		phoneNo = stripPhoneNo(phoneNo);
		System.out.println(phoneNo);
		
		phoneNo = "+00353.00.2640379";
		phoneNo = stripPhoneNo(phoneNo);
		System.out.println(phoneNo);
		
		log.info("Test Emails started ... ");
		log.info("Working Directory = " + System.getProperty("user.dir"));
//		
//		String fileName = "logo.png";
//		boolean fileExists = new File(fileName).isFile();
//		log.info(fileName + " - Exists: " + fileExists);
//		
//		// Check if file exist
//		
//        try {        	 
//            log.info("Initialize emailer ");
//			JavaMailServices.initializeEmailer();
// 
////			log.info("Send email");
//			JavaMailServices.sendMail("serochfo@gmail.com", "Test BriteBin Emailer", true, "<H1>Hello</H1><img src=\"cid:image\">");
//			
//			AlertServices alertServices = new AlertServices();
//			
//			log.info("process waiting alerts");
//			alertServices.processWaitingAlerts();
//			
//        } catch (Exception ex) {
//            log.error("Server exception: " + ex.getMessage());
//            ex.printStackTrace();
//        }		

        log.info("\n ... Test Emails Terminated ");		
	}

}

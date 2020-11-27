package com.trandonsystems.britebin.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.GuestDAL;
import com.trandonsystems.britebin.model.GuestDuplicate;

public class GuestServices {

	static Logger log = Logger.getLogger(GuestServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static void duplicateGuestReadings() {
		log.info("GuestServices.duplicateGuestReadings()");
		
		try {
			
			log.info("getDuplicate Units");
			List<GuestDuplicate> guestDuplicates = GuestDAL.getGuestDuplicates();
			
			for (int i = 0; i < guestDuplicates.size(); i++) {
				log.info("Duplicate Unit: GuestUnitId: " + guestDuplicates.get(i).guestUnitId + "    dupUnitId: " + guestDuplicates.get(i).dupUnitId);
				int rowCount = GuestDAL.duplicateReadings(guestDuplicates.get(i).guestUnitId, guestDuplicates.get(i).dupUnitId);
				log.info("Duplicated Readings: " + rowCount);
			}
			
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}
}

package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.GuestDuplicate;

public class GuestDAL {

	static Logger log = Logger.getLogger(GuestDAL.class);

	public static List<GuestDuplicate> getGuestDuplicates() throws SQLException {
		
		log.info("AlertDAL.getGuestDuplicates()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<GuestDuplicate> guestDuplicates = new ArrayList<GuestDuplicate>();

		String spCall = "{ call getGuestDuplicates(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				GuestDuplicate guestDuplicate = new GuestDuplicate();

				guestDuplicate.guestUnitId = rs.getInt("guestUnitId");
				guestDuplicate.dupUnitId = rs.getInt("dupUnitId");
				
				guestDuplicates.add(guestDuplicate);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return guestDuplicates;
	}

	public static int duplicateReadings (int guestUnitId, int dupUnitId) throws SQLException {
		log.info("AlertDAL.getGuestDuplicates()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call DuplicateReadings(?) }";
		log.info("SP Call: " + spCall);

		int rowsAffected = 0;
		
		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {
		
			spStmt.setInt(1, guestUnitId);
			spStmt.setInt(1, dupUnitId);
			
			rowsAffected = spStmt.executeUpdate();

			return rowsAffected;
			
		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}

}

package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.model.DeviceType;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.UnitReading;


public class UnitDAL {

	static Logger log = Logger.getLogger(UnitDAL.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	static final int ALERT_UNIT_NOT_REPORTING = 20;

	private static UnitReading setUnitReadingValues(ResultSet rs) throws SQLException {
		
		// Only take values I may need
		UnitReading unitReading = new UnitReading();
		unitReading.id = rs.getInt("unit_readings.id");
		
		// We only need the id of the unit
		Unit unit = new Unit();
		unit.id  = rs.getInt("units.id");
		unit.serialNo = rs.getString("serialNo");

		DeviceType deviceType = new DeviceType();
		deviceType.id = rs.getInt("ref_device_type.id");
		deviceType.name = rs.getString("ref_device_type.name");
		unit.deviceType = deviceType;
		
		unitReading.unit = unit;
		
		unitReading.serialNo = rs.getString("serialNo");

		// Convert database timestamp(UTC date) to local time instant
		Timestamp readingDateTime = rs.getTimestamp("readingDateTime");
		if (readingDateTime == null) {
			unitReading.readingDateTime = null;
		}
		else {
			java.time.Instant readingDateTimenInstant = readingDateTime.toInstant();
			unitReading.readingDateTime = readingDateTimenInstant;
		}
		
		unitReading.source = rs.getString("unit_readings.source");
		
		return unitReading;
	}
	
		
	public static List<UnitReading> getLatestReadings(int userFilterId) throws SQLException {

		log.info("UnitDAL.getLatestReadings(userFilterId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<UnitReading> unitReadings = new ArrayList<UnitReading>();

		String spCall = "{ call GetLatestReadings(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, userFilterId);
			spStmt.setInt(2, 0); // Active Only
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				UnitReading unitReading = setUnitReadingValues(rs);

				unitReadings.add(unitReading);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return unitReadings;
	}

	
	public static int getNoReportingAlertCount24Hours(int unitId) throws SQLException {

		log.info("UnitDAL.getAlertCount24Hours(unitId, alertType)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call alertCount24Hours(?, ?) }";
		log.info("SP Call: " + spCall);

		int alertCount = 0;
		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, unitId);
			spStmt.setInt(2, ALERT_UNIT_NOT_REPORTING); 
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				alertCount = rs.getInt("alertCount");

			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return alertCount;
	}
	
	
	public static void saveAlert(int unitId, long unitReadingId, String comment) throws SQLException {

		log.info("UnitDAL.saveAlert(unitId, unitReadingId, comment)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call SaveAlertAlarm(?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, ALERT_UNIT_NOT_REPORTING);
			spStmt.setInt(2, unitId);
			spStmt.setLong(3, unitReadingId);
			spStmt.setString(4, comment);
			
			spStmt.executeUpdate();

		} catch (SQLException ex) {
			log.error("UnitDAL.saveAlert(unitId, unitReadingId, comment) - ERROR: " + ex.getMessage());
			throw ex;
		}

	}
	
		
}

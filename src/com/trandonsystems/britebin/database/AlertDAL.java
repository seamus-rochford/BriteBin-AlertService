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

import com.trandonsystems.britebin.model.Alert;
import com.trandonsystems.britebin.model.AlertDefn;
import com.trandonsystems.britebin.model.AlertType;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.ContentType;
import com.trandonsystems.britebin.model.Country;
import com.trandonsystems.britebin.model.DeviceType;
import com.trandonsystems.britebin.model.EmailDefn;
import com.trandonsystems.britebin.model.Locale;
import com.trandonsystems.britebin.model.Role;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.User;
import com.trandonsystems.britebin.model.UserStatus;

public class AlertDAL {

	static Logger log = Logger.getLogger(AlertDAL.class);

	
	private static Alert setAlertValues(ResultSet rs) throws SQLException  {

		Alert alert = new Alert();
		
		alert.id = rs.getInt("alerts.id");
		
		AlertType alertType = new AlertType();
		alertType.id = rs.getInt("ref_alert_type.id");
		alertType.name = rs.getString("ref_alert_type.name");
		alert.alertType = alertType;
		
		// Get the unit
		Unit unit = new Unit();
		unit.id = rs.getInt("units.id");
		
		User owner = new User();
		owner.id = rs.getInt("units.ownerId");
		unit.owner = owner;
		
		unit.serialNo = rs.getString("units.serialNo");
		
		DeviceType deviceType = new DeviceType();
		deviceType.id = rs.getInt("ref_device_type.id");
		deviceType.name = rs.getString("ref_device_type.name");
		unit.deviceType = deviceType;
		
		unit.location = rs.getString("units.location");
		if (unit.location == null) {
			unit.location = "";
		}
		unit.latitude = rs.getDouble("units.latitude");
		unit.longitude = rs.getDouble("units.longitude");
		
		BinType binType = new BinType();
		binType.id = rs.getInt("ref_bin_type.id");
		binType.name = rs.getString("ref_bin_type.name");
		binType.emptyLevel = rs.getInt("ref_bin_type.emptyLevel");
		binType.fullLevel = rs.getInt("ref_bin_type.fullLevel");
		unit.binType = binType;
		
		ContentType contentType = new ContentType();
		contentType.id = rs.getInt("ref_content_type.id");
		contentType.name = rs.getString("ref_content_type.name");	
		unit.contentType = contentType;
		
		unit.useBinTypeLevel = (rs.getInt("useBinTypeLevel") == 1);
		unit.emptyLevel = rs.getInt("units.emptyLevel");
		unit.fullLevel = rs.getInt("units.fullLevel");

		// Convert database timestamp(UTC date) to local time instant
		Timestamp lastActivity = rs.getTimestamp("units.lastActivity");
		if (lastActivity == null) {
			unit.lastActivity = null;
		}
		else {
			java.time.Instant lastActivityInstant = lastActivity.toInstant();
			unit.lastActivity = lastActivityInstant;
		}

		// Convert database timestamp(UTC date) to local time instant
		Timestamp insertDate = rs.getTimestamp("units.insertDate");
		if (insertDate == null) {
			unit.insertDate = null;
		}
		else {
			java.time.Instant insertDateInstant = insertDate.toInstant();
			unit.insertDate = insertDateInstant;
		}
		unit.insertBy = rs.getInt("units.insertBy");
		
		// Convert database timestamp(UTC date) to local time instant
		Timestamp modifiedDate = rs.getTimestamp("units.modifiedDate");
		if (modifiedDate == null) {
			unit.modifiedDate = null;
		}
		else {
			java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
			unit.modifiedDate = modifiedDateInstant;
		}
		unit.modifiedBy = rs.getInt("units.modifiedBy");
		
		alert.unit = unit;

		// Get the Alert definition
		AlertDefn alertDefn = new AlertDefn();
		alertDefn.id = rs.getInt("alert_defn.id");
		alertDefn.customerId = rs.getInt("alert_defn.customerId");
		
		alertDefn.notifyByEmail = rs.getInt("alert_defn.email") == 1;
		alertDefn.notifyBySms = (rs.getInt("alert_defn.sms") == 1);
		alertDefn.notifyByWhatsApp = (rs.getInt("alert_defn.whatsapp") == 1);
		alertDefn.notifyByPushNotification = (rs.getInt("alert_defn.push") == 1);		
		
		alert.alertDefn = alertDefn;

		// Convert database timestamp(UTC date) to local time instant
		Timestamp alertDate = rs.getTimestamp("alerts.alertDateTime");
		if (alertDate == null) {
			alert.alertDateTime = null;
		}
		else {
			java.time.Instant alertDateInstant = alertDate.toInstant();
			alert.alertDateTime = alertDateInstant;
		}
		
		User user = new User();
		user.id = rs.getInt("users.id");
		user.email = rs.getString("users.email");
		user.password = rs.getString("users.password");
		
		Role role = new Role();
		role.id = rs.getInt("ref_roles.id");
		role.name = rs.getString("ref_roles.name");
		user.role = role;
		
		user.parent = new  User();
		user.parent.id = rs.getInt("users.parentId");
		
		UserStatus status = new UserStatus();
		status.id = rs.getInt("ref_status.id");
		status.name = rs.getString("ref_status.name");
		user.status = status;
		
		Locale locale = new Locale();
		locale.abbr = rs.getString("ref_locale.abbr");
		locale.name = rs.getString("ref_locale.name");
		user.locale = locale;
		
		user.name = rs.getString("users.name");
		user.addr1 = rs.getString("users.addr1");
		user.addr2 = rs.getString("users.addr2");
		user.city = rs.getString("users.city");
		user.county = rs.getString("users.county");
		user.postcode = rs.getString("users.postcode");
		
		Country country = new Country();
		country.id = rs.getInt("ref_country.id");
		country.name = rs.getString("ref_country.name");
		country.abbr = rs.getString("ref_country.abbr");
		user.country = country;
		
		user.mobile = rs.getString("users.mobile");
		user.homeTel = rs.getString("users.homeTel");
		user.workTel = rs.getString("users.workTel");
		
		user.binLevelAlert = rs.getInt("users.binLevelAlert");
		
		// Convert database timestamp(UTC date) to local time instant
		Timestamp lastLoggedIn = rs.getTimestamp("users.lastLoggedIn");
		if (lastLoggedIn == null) {
			user.lastLoggedIn = null;
		}
		else {
			java.time.Instant lastLoggedInInstant = lastLoggedIn.toInstant();
			user.lastLoggedIn = lastLoggedInInstant;
		}
		
		// Convert database timestamp(UTC date) to local time instant
		Timestamp userLastActivity = rs.getTimestamp("users.lastActivity");
		if (userLastActivity == null) {
			user.lastActivity = null;
		}
		else {
			java.time.Instant lastActivityInstant = userLastActivity.toInstant();
			user.lastActivity = lastActivityInstant;
		}
		
		// Convert database timestamp(UTC date) to local time instant
		Timestamp userInsertDate = rs.getTimestamp("users.insertDate");
		if (userInsertDate == null) {
			user.insertDate = null;
		}
		else {
			java.time.Instant insertDateInstant = userInsertDate.toInstant();
			user.insertDate = insertDateInstant;
		}
		user.insertBy = rs.getInt("users.insertBy");
		
		// Convert database timestamp(UTC date) to local time instant
		Timestamp userModifiedDate = rs.getTimestamp("users.modifiedDate");
		if (userModifiedDate == null) {
			user.modifiedDate = null;
		}
		else {
			java.time.Instant modifiedDateInstant = userModifiedDate.toInstant();
			user.modifiedDate = modifiedDateInstant;
		}
		user.modifiedBy = rs.getInt("users.modifiedBy");
		
		alert.user = user;
		
		int damageId = rs.getInt("alerts.damageId");
		
		if (damageId != 0) {
			alert.damage = DamageDAL.getDamage(damageId);
		} else {
			alert.damage = null;
		}
		
		return alert;
	}
	
	
	public static List<Alert> getWaitingAlertsBinFull() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlertsBinFull()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsTriggeredBinFull() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(rs);
				
				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Alerts: " + alerts.size());
		return alerts;
	}
		
	
	public static List<Alert> getWaitingAlertsBatteryUVLO() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlertsBatteryUVLO()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsTriggeredBatteryUVLO() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(rs);
				
				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Alerts: " + alerts.size());
		return alerts;
	}
		
	
	public static List<Alert> getWaitingAlertsBinEmptiedLastPeriod() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlertsBinEmptiedLastPeriod()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsTriggeredBinEmptiedLastPeriod() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(rs);
				
				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Alerts: " + alerts.size());
		return alerts;
	}
		
	
	public static List<Alert> getWaitingAlertsBatteryOverTempLO() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlertsBatteryOverTempLO()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsTriggeredBatteryOverTempLO() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(rs);
				
				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Alerts: " + alerts.size());
		return alerts;
	}
		
	
	public static List<Alert> getWaitingAlertsBinLocked() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlertsBinLocked()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsTriggeredBinLocked() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(rs);
				
				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Alerts: " + alerts.size());
		return alerts;
	}
		
	
	public static List<Alert> getWaitingAlertsBinTilted() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlertsBinTilted()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsTriggeredBinTilted() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(rs);
				
				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Alerts: " + alerts.size());
		return alerts;
	}
		
	
	public static List<Alert> getWaitingAlertsServiceDoorOpen() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlertsServiceDoorOpen()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsTriggeredServiceDoorOpen() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(rs);
				
				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Alerts: " + alerts.size());
		return alerts;
	}
		
	
	public static List<Alert> getWaitingAlertsFlapStuckOpen() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlertsFlapStuckOpen()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsTriggeredFlapStuckOpen() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(rs);
				
				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Alerts: " + alerts.size());
		return alerts;
	}
		
	
	public static List<Alert> getWaitingAlertsDamage() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlertsDamage()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsTriggeredDamage() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Alert alert = setAlertValues(rs);
				
				// Check Damage object to see who is to be sent email
				if (alert.damage.damageStatus.id == 2) {
					// Assigned damage email - get assigned user (note it will be the first entry in the history)
					log.debug("AssignedTo damage alert for user: " + alert.damage.damageHistory.get(0).assignedToUserId);
					alert.user = UserDAL.getUser(alert.damage.damageHistory.get(0).assignedToUserId);
				}
				
				alerts.add(alert);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Alerts: " + alerts.size());
		return alerts;
	}
		
	
	public static List<Alert> getWaitingAlerts() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlerts()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		try {
			alerts.addAll(getWaitingAlertsBinFull());
			alerts.addAll(getWaitingAlertsBatteryUVLO());
			alerts.addAll(getWaitingAlertsBinEmptiedLastPeriod());
			alerts.addAll(getWaitingAlertsBatteryOverTempLO());
			alerts.addAll(getWaitingAlertsBinLocked());
			alerts.addAll(getWaitingAlertsBinTilted());
			alerts.addAll(getWaitingAlertsServiceDoorOpen());
			alerts.addAll(getWaitingAlertsFlapStuckOpen());
			alerts.addAll(getWaitingAlertsDamage());
			
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return alerts;
	}
	
	
	public static List<EmailDefn> getAlertEmailDefns() throws Exception{
		log.info("AlertDAL.getAlertEmails()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<EmailDefn> emailDefns = new ArrayList<EmailDefn>();

		String spCall = "{ call GetAlertEmails() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				EmailDefn emailDefn = new EmailDefn();
				
				emailDefn.alertType = rs.getInt("alertType");
				emailDefn.subject = rs.getString("subject");
				emailDefn.htmlBody = (rs.getInt("htmlBody") == 1);
				emailDefn.body = rs.getString("body");
				
				emailDefns.add(emailDefn);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Email definitions: " + emailDefns.size());
		return emailDefns;		
	}
	
	public static void markAlertAsProcessed(int alertId) throws SQLException {
		log.info("AlertDAL.markAlertAsProcessed()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call MarkAlertAsProcessed(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, alertId);
			spStmt.executeUpdate();

		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}	
	}	
	
	public static void markAlertAsFailed(int alertId, String reason) {
		log.info("AlertDAL.markAlertAsProcessed()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call MarkAlertAsFailed(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, alertId);
			spStmt.setString(2, reason);
			spStmt.executeUpdate();

		} catch (SQLException ex) {
			log.error("ERROR - markAlertAsFailed: " + ex.getMessage());
		}	
	}	

}

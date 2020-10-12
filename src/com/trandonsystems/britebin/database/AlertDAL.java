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
import com.trandonsystems.britebin.model.Email;
import com.trandonsystems.britebin.model.EmailDefn;
import com.trandonsystems.britebin.model.PushNotification;
import com.trandonsystems.britebin.model.PushNotificationDefn;
import com.trandonsystems.britebin.model.Unit;
import com.trandonsystems.britebin.model.User;
import com.trandonsystems.britebin.model.sms.Sms;
import com.trandonsystems.britebin.model.sms.SmsDefn;
import com.trandonsystems.britebin.model.sms.SmsResponse;
import com.trandonsystems.britebin.model.sms.SmsResponseMessage;


public class AlertDAL {

	static Logger log = Logger.getLogger(AlertDAL.class);


	private static Alert setAlertValues(ResultSet rs) throws SQLException  {

		Alert alert = new Alert();
		
		String localeAbbr = rs.getString("users.locale");
		
		alert.id = rs.getInt("alerts.id");
		
		alert.alertType = LookupDAL.getAlertType(localeAbbr, rs.getInt("alerts.alertType"));
		
		// Get the unit
		Unit unit = new Unit();
		unit.id = rs.getInt("units.id");
		
		User owner = new User();
		owner.id = rs.getInt("units.ownerId");
		unit.owner = owner;
		
		unit.serialNo = rs.getString("units.serialNo");
		
		unit.deviceType = LookupDAL.getDeviceType(localeAbbr, rs.getInt("units.deviceType"));
		
		unit.location = rs.getString("units.location");
		if (unit.location == null) {
			unit.location = "";
		}
		unit.latitude = rs.getDouble("units.latitude");
		unit.longitude = rs.getDouble("units.longitude");
		
		unit.binType = LookupDAL.getBinType(localeAbbr, rs.getInt("units.binType"));
		unit.contentType = LookupDAL.getContentType(localeAbbr, rs.getInt("units.contentType"));
		
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
		
		user.role = LookupDAL.getRole(localeAbbr, rs.getInt("users.role"));
		
		user.parent = new  User();
		user.parent.id = rs.getInt("users.parentId");
		
		user.status = LookupDAL.getUserStatus(localeAbbr, rs.getInt("users.status"));
		user.locale = LookupDAL.getLocale(localeAbbr, rs.getString("users.locale"));
		
		user.name = rs.getString("users.name");
		user.addr1 = rs.getString("users.addr1");
		user.addr2 = rs.getString("users.addr2");
		user.city = rs.getString("users.city");
		user.county = rs.getString("users.county");
		user.postcode = rs.getString("users.postcode");
		
		user.country = LookupDAL.getCountry(localeAbbr, rs.getInt("users.country"));;
		
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
		
		user.gcmToken = rs.getString("users.gcmToken");
		
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
					// Assigned damage email - get assigned user (note it will be the first entry in the history - history is in descending order  i.e. the latest entry first)
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
		
	
	public static List<Alert> getWaitingAlertsNoReadings() throws SQLException {
		
		log.info("AlertDAL.getWaitingAlertsNoReadings()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Alert> alerts = new ArrayList<Alert>();

		String spCall = "{ call GetAlertsTriggeredNoReadings() }";
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
			alerts.addAll(getWaitingAlertsNoReadings());
			
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

		String spCall = "{ call GetAlertEmailDefns() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				EmailDefn emailDefn = new EmailDefn();
				
				emailDefn.alertType = rs.getInt("alertType");
				emailDefn.locale = rs.getString("locale");
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

	
	public static List<SmsDefn> getAlertSmsDefns() throws Exception{
		log.info("AlertDAL.getAlertSmsDefns()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<SmsDefn> smsDefns = new ArrayList<SmsDefn>();

		String spCall = "{ call GetAlertSmsDefns() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				SmsDefn smsDefn = new SmsDefn();
				
				smsDefn.alertType = rs.getInt("alertType");
				smsDefn.locale = rs.getString("locale");
				smsDefn.message = rs.getString("message");
				
				smsDefns.add(smsDefn);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No. SMS definitions: " + smsDefns.size());
		
		return smsDefns;		
	}
	
	
	public static List<PushNotificationDefn> getPushNotificationDefns() throws Exception{
		log.info("AlertDAL.getPushNotificationDefns()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<PushNotificationDefn> pushNotificationDefns = new ArrayList<PushNotificationDefn>();

		String spCall = "{ call GetAlertPushDefns() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				PushNotificationDefn pushNotificationDefn = new PushNotificationDefn();
				
				pushNotificationDefn.alertType = rs.getInt("alertType");
				pushNotificationDefn.locale = rs.getString("locale");
				pushNotificationDefn.title = rs.getString("title");
				pushNotificationDefn.body = rs.getString("body");
				
				pushNotificationDefns.add(pushNotificationDefn);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No. PushNotification definitions: " + pushNotificationDefns.size());
		
		return pushNotificationDefns;		
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
	
//	public static void markAlertAsFailed(int alertId, int contactType, String contactDetails, String reason) {
//		log.info("AlertDAL.markAlertAsFailed()");
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
//		} catch (Exception ex) {
//			log.error("ERROR: " + ex.getMessage());
//		}
//
//		String spCall = "{ call MarkAlertAsFailed(?, ?, ?, ?) }";
//		log.info("SP Call: " + spCall);
//
//		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
//				CallableStatement spStmt = conn.prepareCall(spCall)) {
//
//			spStmt.setInt(1, alertId);
//			spStmt.setInt(2,  contactType);
//			spStmt.setString(3, contactDetails);
//			spStmt.setString(4, reason);
//			spStmt.executeUpdate();
//
//		} catch (SQLException ex) {
//			log.error("ERROR - markAlertAsFailed: " + ex.getMessage());
//		}	
//	}	
//

	public static int generateEmail(int alertId, String email, String subject, boolean htmlBody, String body) throws SQLException {
		log.info("AlertDAL.generateEmail()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call InsertEmail(?, ?, ?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);

		int id = 0;
		
		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, id);
			spStmt.setInt(2, alertId);
			spStmt.setString(3, email);
			spStmt.setString(4, subject);
			spStmt.setInt(5, htmlBody ? 1 : 0);
			spStmt.setString(6, body);
			spStmt.executeUpdate();
			
			id = spStmt.getInt(1);
			
			return id;

		} catch (SQLException ex) {
			log.error("ERROR - generateEmail: " + ex.getMessage());
			throw ex;
		}			
	}

	public static void markEmailAlertAsFailed(int id, String comment) {
		log.info("AlertDAL.markEmailAlertAsFailed()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call MarkEmailFailed(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, id);
			spStmt.setString(2, comment);
			spStmt.executeUpdate();

		} catch (SQLException ex) {
			log.error("ERROR - markEmailAlertAsFailed: " + ex.getMessage());
		}			
	}
	
		
	public static void markEmailAlertAsSent(int id) {
		log.info("AlertDAL.markEmailAlertAsSent()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call MarkEmailAsSent(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, id);
			spStmt.executeUpdate();

		} catch (SQLException ex) {
			log.error("ERROR - markEmailAlertAsSent: " + ex.getMessage());
		}			
	}
	
	
	public static List<Email> getWaitingEmails() throws SQLException {
		
		log.info("AlertDAL.getWaitingEmail()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Email> emailList = new ArrayList<Email>();

		String spCall = "{ call getWaitingEmail() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Email email = new Email();
				
				email.id = rs.getInt("id");
				email.alertId = rs.getInt("alertId");
				email.emailAddr = rs.getString("email");
				email.subject = rs.getString("subject");
				email.htmlBody = (rs.getInt("htmlBody") == 1);
				email.body = rs.getString("body");
				
				emailList.add(email);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No emails: " + emailList.size());
		return emailList;
	}
		
	
	
	public static int generateSms(int alertId, String phoneNo, String message) throws SQLException {
		log.info("AlertDAL.generateSms()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call InsertSmsMessage(?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);

		int id = 0;
		
		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, id);
			spStmt.setInt(2,  alertId);
			spStmt.setString(3, phoneNo);
			spStmt.setString(4, message);
			spStmt.executeUpdate();
			
			id = spStmt.getInt(1);
			
			return id;

		} catch (SQLException ex) {
			log.error("ERROR - generateSms: " + ex.getMessage());
			throw ex;
		}			
	}

	public static void saveSmsResponseMessage(Connection conn, int smsId, SmsResponseMessage smsResponseMessage) throws SQLException {
		String spCall = "{ call saveSmsResponseMessage(?, ?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);
		
		try (CallableStatement spStmt = conn.prepareCall(spCall)) {

			log.info("smsId: " + smsId);
			log.info("messageId: " + smsResponseMessage.messageId);
			log.info("destination: " + smsResponseMessage.destination.phoneNumber);
			log.info("status.code: " + smsResponseMessage.status.code);
			log.info("status.description: " + smsResponseMessage.status.description);
			spStmt.setInt(1, smsId);
			spStmt.setString(2, smsResponseMessage.messageId);
			spStmt.setString(3, smsResponseMessage.destination.phoneNumber);
			spStmt.setInt(4, smsResponseMessage.status.code);
			spStmt.setString(5, smsResponseMessage.status.description);
			spStmt.executeUpdate();

			return;

		} catch (SQLException ex) {
			log.error("ERROR - saveSmsResponseMessage: " + ex.getMessage());
			throw ex;
		}			
		
	}
	
	public static void saveSmsResponse(int smsId, int httpResponseCode, SmsResponse smsResponse) throws SQLException {
		
		log.info("AlertDAL.saveSmsResponse()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call SaveSMSResponse(?, ?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);
		
		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, smsId);
			spStmt.setInt(2, httpResponseCode);
			spStmt.setInt(3, smsResponse.resultCode);
			spStmt.setString(4, smsResponse.resultDescription);
			spStmt.setInt(5, smsResponse.smsCount);
			spStmt.executeUpdate();
			log.debug("SMS Response saved");

			for(int i = 0; i < smsResponse.messages.size(); i++) {
				saveSmsResponseMessage(conn, smsId, smsResponse.messages.get(i));
			}
			
			log.debug("SMS Response Messages saved");
			return;

		} catch (SQLException ex) {
			log.error("ERROR - saveSmsResponse: " + ex.getMessage());
			throw ex;
		}			
	}	

	public static void markSmsAlertAsFailed(int id, String comment) {
		log.info("AlertDAL.markSmsAlertAsFailed()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call MarkSmsFailed(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, id);
			spStmt.setString(2, comment);
			spStmt.executeUpdate();

		} catch (SQLException ex) {
			log.error("ERROR - markSmsAlertAsFailed: " + ex.getMessage());
		}			
	}
	
	
	public static List<Sms> getWaitingSms() throws SQLException {
		
		log.info("AlertDAL.getWaitingSms()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<Sms> smsList = new ArrayList<Sms>();

		String spCall = "{ call getWaitingSms() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				Sms sms = new Sms();
				
				sms.alertId = rs.getInt("alertId");
				sms.id = rs.getInt("id");
				sms.phoneNo = rs.getString("phoneNo");
				sms.message = rs.getString("message");
				
				smsList.add(sms);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No sms: " + smsList.size());
		return smsList;
	}
		
	public static int generatePushNotification(int alertId, String gcmToken, String title, String body) throws SQLException {
		log.info("AlertDAL.generatePushNotification()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call InsertPushNotification(?, ?, ?, ?, ?) }";
		log.info("SP Call: " + spCall);

		int id = 0;
		
		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, id);
			spStmt.setInt(2,  alertId);
			spStmt.setString(3, gcmToken);
			spStmt.setString(4, title);
			spStmt.setString(5, body);
			spStmt.executeUpdate();
			
			id = spStmt.getInt(1);
			
			return id;

		} catch (SQLException ex) {
			log.error("ERROR - generateSms: " + ex.getMessage());
			throw ex;
		}			
	}

	public static List<PushNotification> getWaitingPushNotifications() throws SQLException {
		
		log.info("AlertDAL.getWaitingPushNotifications()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<PushNotification> pushNotifications = new ArrayList<PushNotification>();

		String spCall = "{ call getWaitingPushNotifications() }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				PushNotification pushNotification = new PushNotification();
				
				pushNotification.alertId = rs.getInt("alertId");
				pushNotification.id = rs.getInt("id");
				pushNotification.gcmToken = rs.getString("gcmToken");
				pushNotification.title = rs.getString("title");
				pushNotification.body = rs.getString("body");
				
				pushNotifications.add(pushNotification);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		log.debug("No Push  Notifications: " + pushNotifications.size());
		return pushNotifications;
	}
	
	public static void markPushNotificationAlertAsFailed(int id, String comment) {
		log.info("AlertDAL.markPushNotificationAlertAsFailed()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call MarkPushNotificationFailed(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, id);
			spStmt.setString(2, comment);
			spStmt.executeUpdate();

		} catch (SQLException ex) {
			log.error("ERROR - markPushNotificationAlertAsFailed: " + ex.getMessage());
		}			
	}
	
	public static void savePushNotificationResponse(int pushNotificationId, int httpResponseCode, String httpResponseBody) throws SQLException {
		
		log.info("AlertDAL.savePushNotificationResponse()");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String spCall = "{ call SavePushNotificationResponse(?, ?, ?) }";
		log.info("SP Call: " + spCall);
		
		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, pushNotificationId);
			spStmt.setInt(2, httpResponseCode);
			spStmt.setString(3, httpResponseBody);
			spStmt.executeUpdate();
			log.debug("Push Notification Response saved");

			return;

		} catch (SQLException ex) {
			log.error("ERROR - savePushNotificationResponse: " + ex.getMessage());
			throw ex;
		}			
	}	

	

}

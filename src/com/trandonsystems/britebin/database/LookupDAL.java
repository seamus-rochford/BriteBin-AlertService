package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.AlertType;
import com.trandonsystems.britebin.model.BinType;
import com.trandonsystems.britebin.model.ContentType;
import com.trandonsystems.britebin.model.Country;
import com.trandonsystems.britebin.model.DeviceType;
import com.trandonsystems.britebin.model.Locale;
import com.trandonsystems.britebin.model.Role;
import com.trandonsystems.britebin.model.UserStatus;

public class LookupDAL {

	static Logger log = Logger.getLogger(LookupDAL.class);

	public static AlertType getAlertType(String locale, int alertTypeId) {
		
		log.info("LookupDAL.getAlertType(" + locale + ")");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		AlertType alertType = new AlertType();

		String spCall = "{ call GetAlertType(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			spStmt.setInt(2,  alertTypeId);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				alertType.id = rs.getInt("id");
				alertType.name = rs.getString("name");
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return alertType;
	}

	public static BinType getBinType(String locale, int binTypeId) {
		
		log.info("LookupDAL.getBinType(" + locale + ")");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		BinType binType = new BinType();

		String spCall = "{ call GetBinType(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			spStmt.setInt(2,  binTypeId);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				binType.id = rs.getInt("id");
				binType.name = rs.getString("name");
				binType.emptyLevel = rs.getInt("emptyLevel");
				binType.fullLevel = rs.getInt("fullLevel");
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return binType;
	}

	public static ContentType getContentType(String locale, int contentTypeId) {
		
		log.info("LookupDAL.getContentType");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		ContentType contentType = new ContentType();

		String spCall = "{ call GetContentType(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			spStmt.setInt(2, contentTypeId);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				contentType.id = rs.getInt("id");
				contentType.name = rs.getString("name");
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return contentType;
	}

	public static Country getCountry(String locale, int countryId) {
		
		log.info("LookupDAL.getCountry");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		Country country = new Country();

		String spCall = "{ call GetCountry(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			spStmt.setInt(2, countryId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				country.id = rs.getInt("id");
				country.name = rs.getString("name");
				country.abbr = rs.getString("abbr");
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return country;
	}

	public static DeviceType getDeviceType(String locale, int deviceTypeId) {
		
		log.info("LookupDAL.getDeviceType");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		DeviceType deviceType = new DeviceType();

		String spCall = "{ call GetDeviceType(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			spStmt.setInt(2, deviceTypeId);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				deviceType.id = rs.getInt("id");
				deviceType.name = rs.getString("name");
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return deviceType;
	}

	public static Locale getLocale(String translateLocale, String localeAbbr) {
		
		log.info("LookupDAL.getLocale");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		Locale locale = new Locale();

		String spCall = "{ call GetLocale(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, translateLocale);
			spStmt.setString(2, localeAbbr);
			ResultSet rs = spStmt.executeQuery();
			
			if (rs.next()) {
				locale.abbr = rs.getString("abbr");
				locale.name = rs.getString("Name");
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return locale;
	}

	public static Role getRole(String locale, int roleId) {
		
		log.info("LookupDAL.getRole");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		Role role = new Role();

		String spCall = "{ call GetRole(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			spStmt.setInt(2, roleId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				role.id = rs.getInt("id");
				role.name = rs.getString("Name");
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return role;
	}

	public static UserStatus getUserStatus(String locale, int userStatusId) {
		
		log.info("LookupDAL.getUserStatus");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		UserStatus userStatus = new UserStatus();

		String spCall = "{ call GetStatusName(?, ?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, locale);
			spStmt.setInt(2, userStatusId);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				userStatus.id = rs.getInt("id");
				userStatus.name = rs.getString("Name");

			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		return userStatus;
	}

}

package com.trandonsystems.britebin.database;

import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Timestamp;


import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.Country;
import com.trandonsystems.britebin.model.Locale;
import com.trandonsystems.britebin.model.Role;
import com.trandonsystems.britebin.model.UserStatus;
import com.trandonsystems.britebin.model.User;

public class UserDAL {

	static Logger log = Logger.getLogger(UserDAL.class);

	public UserDAL() {
		log.trace("Constructor");
	}

	public static User getUser(int id) {

		log.info("UserDAL.get(id)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetUserById(?, ?) }";
		log.info("SP Call: " + spCall);
		
		User user = null;
		
		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, 1);  // default to admin user so I can get any user
			spStmt.setInt(2, id);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				user = new User();
				
				user.id = id;
				user.email = rs.getString("email");
				user.password = rs.getString("password");
				
				Role role = new Role();
				role.id = rs.getInt("role");
				role.name = rs.getString("ref_roles.name");
				user.role = role;
				
				user.parent = new  User();
				user.parent.id = rs.getInt("parentId");
				user.parent.name = rs.getString("parentUser.name");
				
				UserStatus status = new UserStatus();
				status.id = rs.getInt("status");
				status.name = rs.getString("ref_status.name");
				user.status = status;
				
				Locale locale = new Locale();
				locale.abbr = rs.getString("locale");
				locale.name = rs.getString("ref_locale.name");
				user.locale = locale;
				
				user.name = rs.getString("name");
				user.addr1 = rs.getString("addr1");
				user.addr2 = rs.getString("addr2");
				user.city = rs.getString("city");
				user.county = rs.getString("county");
				user.postcode = rs.getString("postcode");
				
				Country country = new Country();
				country.id = rs.getInt("country");
				country.name = rs.getString("ref_country.name");
				country.abbr = rs.getString("ref_country.abbr");
				user.country = country;
				
				user.mobile = rs.getString("mobile");
				user.homeTel = rs.getString("homeTel");
				user.workTel = rs.getString("workTel");
				
				user.binLevelAlert = rs.getInt("binLevelAlert");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastLoggedIn = rs.getTimestamp("lastLoggedIn");
				if (lastLoggedIn == null) {
					user.lastLoggedIn = null;
				}
				else {
					java.time.Instant lastLoggedInInstant = lastLoggedIn.toInstant();
					user.lastLoggedIn = lastLoggedInInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp lastActivity = rs.getTimestamp("lastActivity");
				if (lastActivity == null) {
					user.lastActivity = null;
				}
				else {
					java.time.Instant lastActivityInstant = lastActivity.toInstant();
					user.lastActivity = lastActivityInstant;
				}
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					user.insertDate = null;
				}
				else {
					java.time.Instant insertDateInstant = insertDate.toInstant();
					user.insertDate = insertDateInstant;
				}
				user.insertBy = rs.getInt("insertBy");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				if (modifiedDate == null) {
					user.modifiedDate = null;
				}
				else {
					java.time.Instant modifiedDateInstant = modifiedDate.toInstant();
					user.modifiedDate = modifiedDateInstant;
				}
				user.modifiedBy = rs.getInt("modifiedBy");
				
				user.gcmToken = rs.getString("gcmToken");

			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}

		return user;
	}

}

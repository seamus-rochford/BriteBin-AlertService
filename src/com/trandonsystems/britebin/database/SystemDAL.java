package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;


public class SystemDAL {

	static Logger log = Logger.getLogger(SystemDAL.class);

	public static String getSysConfigValue(String name) throws SQLException {
		
		log.info("SystemDAL.getSysConfigValue(" + name + ")");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		String configValue = "";

		String spCall = "{ call getSysConfigValue(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setString(1, name);
			ResultSet rs = spStmt.executeQuery();

			if (rs.next()) {
				configValue = rs.getString("value");
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return configValue;
	}
	
	
}

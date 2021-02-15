package com.trandonsystems.britebin.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.trandonsystems.britebin.model.Damage;
import com.trandonsystems.britebin.model.DamageHistory;
import com.trandonsystems.britebin.model.DamageStatus;
import com.trandonsystems.britebin.model.DamageType;
import com.trandonsystems.britebin.model.Unit;


public class DamageDAL {

	static Logger log = Logger.getLogger(DamageDAL.class);


	public DamageDAL() {
		log.trace("Constructor");
	}
	
	public static Damage getDamage(int damageId) throws SQLException {

		log.info("DamageDAL.getDamage(id)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			log.error("ERROR: Can't create instance of driver" + ex.getMessage());
		}

		String spCall = "{ call GetDamage(?) }";
		log.info("SP Call: " + spCall);
		
		Damage damage = new Damage();
		
		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, damageId);
			ResultSet rs = spStmt.executeQuery();

			damage.id = damageId;
			if (rs.next()) {
				
				damage.damageType = new DamageType();
				damage.damageType.id = rs.getInt("ref_damage_type.id");
				damage.damageType.name = rs.getString("ref_damage_type.name");
				
				damage.damageStatus = new DamageStatus();
				damage.damageStatus.id = rs.getInt("ref_damage_status.id");
				damage.damageStatus.name = rs.getString("ref_damage_status.name");
				
				// The unit definition is in teh alert - do not need to get teh unit here
				damage.unit = new Unit();
				damage.unit.id =  rs.getInt("damage.unitId");
				
				// Convert database timestamp(UTC date) to local time instant
				Timestamp insertDate = rs.getTimestamp("insertDate");
				if (insertDate == null) {
					damage.insertDate = null;
				}
				else {
					Instant insertDateInstant = insertDate.toInstant();
					damage.insertDate = insertDateInstant;
				}
				
				// Get damage history
				damage.damageHistory = getDamageHistory(damageId);

			}

		} catch (SQLException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

		return damage;
	}

	public static List<DamageHistory> getDamageHistory(int damageId) throws SQLException {
		
		log.info("UnitDAL.getUnits(userFilterId)");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			log.error("ERROR: " + ex.getMessage());
		}

		List<DamageHistory> damageHistoryList = new ArrayList<DamageHistory>();

		String spCall = "{ call GetDamageHistory(?) }";
		log.info("SP Call: " + spCall);

		try (Connection conn = DriverManager.getConnection(UtilDAL.connUrl, UtilDAL.username, UtilDAL.password);
				CallableStatement spStmt = conn.prepareCall(spCall)) {

			spStmt.setInt(1, damageId);
			ResultSet rs = spStmt.executeQuery();

			while (rs.next()) {
				DamageHistory damageHistory = new DamageHistory();

				damageHistory.id = rs.getInt("id");
				damageHistory.damageId = damageId;
				damageHistory.damageStatus = new DamageStatus();
				damageHistory.damageStatus.id = rs.getInt("ref_damage_status.id");
				damageHistory.damageStatus.name = rs.getString("ref_damage_status.name");
				damageHistory.comment = rs.getString("damage_history.comment");
				damageHistory.actionUserId = rs.getInt("damage_history.actionUserId");

				// Convert database timestamp(UTC date) to local time instant
				Timestamp actionDate = rs.getTimestamp("actionDate");
				if (actionDate == null) {
					damageHistory.actionDate = null;
				}
				else {
					Instant actionDateInstant = actionDate.toInstant();
					damageHistory.actionDate = actionDateInstant;
				}

				damageHistory.assignedToUserId = rs.getInt("damage_history.assignedTo");

				damageHistoryList.add(damageHistory);
			}
		} catch (SQLException ex) {
			log.error("ERROR: " + ex.getMessage());
			throw ex;
		}

		return damageHistoryList;
	}

}


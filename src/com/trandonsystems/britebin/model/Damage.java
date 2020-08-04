package com.trandonsystems.britebin.model;

import java.time.Instant;
import java.util.List;

public class Damage {

	public int id;
	public DamageType damageType;
	public Unit unit;
	public DamageStatus damageStatus;
	public Instant insertDate;
	
	public List<DamageHistory> damageHistory;
	
}

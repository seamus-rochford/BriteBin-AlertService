package com.trandonsystems.britebin.model;

import java.time.Instant;

public class DamageHistory {

	public int id;
	public int damageId;
	public DamageStatus damageStatus;
	public int actionUserId;
	public Instant actionDate;
	public String comment;
	public int assignedToUserId;
	public byte[] image;
	public String filename;
	
}

package com.trandonsystems.britebin.model;

import java.time.Instant;

public class Alert {

	public int id;
	public AlertType alertType;
	public Unit unit;
	public AlertDefn alertDefn;
	public Instant alertDateTime;
	public User user;
	
}

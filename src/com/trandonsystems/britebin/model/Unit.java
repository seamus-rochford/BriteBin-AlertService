package com.trandonsystems.britebin.model;

import java.time.Instant;

public class Unit {

	public int id;
	public User owner;
	public String serialNo;
	public DeviceType deviceType;
	public String location;
	public double latitude;
	public double longitude;
	public BinType binType;
	public ContentType contentType;
	public boolean useBinTypeLevel;
	public int emptyLevel;
	public int fullLevel;
	public Instant lastActivity;

	public Instant insertDate;
	public int insertBy;
	public Instant modifiedDate;
	public int modifiedBy;
		
	public int reading40percent;
	public int reading100percent;
}

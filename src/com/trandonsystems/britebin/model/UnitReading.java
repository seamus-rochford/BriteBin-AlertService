package com.trandonsystems.britebin.model;

import java.time.Instant;

public class UnitReading {

	public long id;
	
	public Unit unit;
	
	public String serialNo;
	
	// data
	public int msgType;
	
	public int binLevel;		// unsigned
	public int binLevelBC;		// unsigned
	public boolean compactionDone;  // if incoming binLevel = 0, then no compaction done so binLevel is binLevelBC
	
	public int binLevelPercent;
	public int binLevelBCPercent;
	
	public int binLevelStatus;
	
	public int noFlapOpenings;	// unsigned
	public int batteryVoltageReading;	// unsigned
	public double batteryVoltage;
	public int temperature;		// *** signed ***
	public int noCompactions;	// unsigned
	
	// Signal strengths
	public int nbIoTSignalStrength;
	
	//flags
	public boolean batteryUVLO;
	public boolean binEmptiedLastPeriod;
	public boolean batteryOverTempLO;
	public boolean binLocked;
	public boolean binFull;
	public boolean binTilted;
	public boolean serviceDoorOpen;
	public boolean flapStuckOpen;

	public double rssi;		// Received Signal Strength Indicator
	public int src;			// Sonic Result Code - used by tekelek
	public double snr;
	public int ber;
	public double rsrq;	// Reference Signal Received Quality
	public int rsrp; 	// Reference Signal Received Power
	
	public Instant readingDateTime;
	public Instant insertDateTime;
	
	public String source;
	
	// These are message type 5 values
	public String firmware;
	public long timeDiff;     // time difference between unit and server
	
	// flags
	public boolean binJustOn;
	public boolean regularPeriodicReporting;
	public boolean nbiotSimIssue;
		

}

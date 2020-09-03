package com.trandonsystems.britebin.model.sms;

import java.util.List;

public class SmsResponse {

	public int resultCode;
	public String resultDescription;
	public List<SmsResponseMessage> messages;
	public int smsCount;
	
}

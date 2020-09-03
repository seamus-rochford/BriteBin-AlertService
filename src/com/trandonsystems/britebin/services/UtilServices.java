package com.trandonsystems.britebin.services;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.log4j.Logger;

public class UtilServices {

	static Logger log = Logger.getLogger(SmsServices.class);

	public static String getIpAddress() throws Exception {
	
		try(final DatagramSocket socket = new DatagramSocket()){
			  
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			
			return socket.getLocalAddress().getHostAddress();
			  
		} catch (Exception ex) {
			log.error("Could NOT get the machine IP Address");
			throw ex;
		}
	}
	
	public static String getBase64(String inStr) {
		
		byte[] bytesEncoded = inStr.getBytes(StandardCharsets.UTF_8);
		
		String base64 = new String(Base64.getEncoder().encodeToString(bytesEncoded));
		
		return base64;
	}

	
	public static String stripPhoneNo(String phoneNo) {
		
		// Remove all except digits
		String newPhoneNumber = phoneNo.replaceAll("[^0-9]", "");
		
		// Remove leading zeros
		newPhoneNumber = newPhoneNumber.replaceAll("^0+(?=.)", "");
		
		return newPhoneNumber;
	}
	
}

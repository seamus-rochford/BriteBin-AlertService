package com.trandonsystems.britebin.services;

import java.io.IOException;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.trandonsystems.britebin.database.AlertDAL;
import com.trandonsystems.britebin.database.SystemDAL;
import com.trandonsystems.britebin.model.sms.Sms;
import com.trandonsystems.britebin.model.sms.SmsResponse;

public class SmsServices {

	static String SMS_URL = "http://multi.mobile-gw.com:9000/v1/omni/message";
	static String SMS_USERNAME = "demo7777n";
	static String SMS_PASSWORD = "?7&mNbq6";
	static String SMS_SENDER = "SMSInfo";
	static String SMS_DLR_URL = "http://161.35.32.177:8080/BriteBin/api/sms/dlr";
	
	static Logger log = Logger.getLogger(SmsServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public static boolean initializeSms() throws Exception {
		try {
		
			String dbSmsUrl = SystemDAL.getSysConfigValue("SMS-URL");
			if (dbSmsUrl != null) {
				SMS_URL = dbSmsUrl;
			}
			String dbSmsUsername = SystemDAL.getSysConfigValue("SMS-USERNAME");
			if (dbSmsUsername != null) {
				SMS_USERNAME = dbSmsUsername;
			}
			String dbSmsPwd = SystemDAL.getSysConfigValue("SMS-PASSWORD");
			if (dbSmsPwd != null) {
				SMS_PASSWORD = dbSmsPwd;
			}
			String dbSmsDlrUrl = SystemDAL.getSysConfigValue("SMS-DLR-URL");
			if (dbSmsDlrUrl != null) {
				SMS_DLR_URL = dbSmsDlrUrl;
			}
			
		} catch(SQLException ex) {
			log.error("ERROR: Failed to initialize SMS Sender: " + ex.getMessage());
			log.error(ex.getStackTrace());
			throw ex;
		}
		
		return true;
	}
	
		
	public static boolean sendSMS(String phoneNumber, String msg, int smsId) throws Exception {
		
		String userPwd = SMS_USERNAME + ":" + SMS_PASSWORD;
		String basicAuthorization = "Basic " + UtilServices.getBase64(userPwd);

    	PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    	CloseableHttpClient httpClient = HttpClients.custom()
    			.setConnectionManager(connManager)
    			.build();		
    	
    	log.debug("SMS-URL: " + SMS_URL);
    	log.debug("Authentication: " + basicAuthorization);
    	HttpPost httpPost = new HttpPost(SMS_URL);
    	httpPost.addHeader(HttpHeaders.AUTHORIZATION, basicAuthorization);
    	httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    	httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());

    	// Build Request Body
    	JsonArray channels = Json.createArrayBuilder()
    			.add("SMS")
    			.build();
    	
    	phoneNumber = UtilServices.stripPhoneNo(phoneNumber);
    	
    	JsonObject phoneNo = Json.createObjectBuilder()
    			.add("phoneNumber", phoneNumber)
    			.build();
    	JsonArray destinations = Json.createArrayBuilder()
    			.add(phoneNo)
    			.build();
    	
    	JsonObject smsSender = Json.createObjectBuilder()
    			.add("sender", SMS_SENDER)
    			.add("text", msg)
    			.build();
    	
    	JsonObject reqBody = Json.createObjectBuilder()
    			.add("channels", channels)
    			.add("destinations", destinations)
    			.add("transactionId", String.valueOf(smsId))
    			.add("dlr", true)
    			.add("dlrUrl", SMS_DLR_URL)
    			.add("tag", "BriteBin alert")
    			.add("sms", smsSender)
    			.build();
    	
    	log.debug("Request Body: " + gson.toJson(reqBody.toString()));
    	
    	httpPost.setEntity(new StringEntity(reqBody.toString(), ContentType.APPLICATION_JSON));
    	
    	try {
    		HttpResponse response = httpClient.execute(httpPost);
    		
    		int httpResponseCode = response.getStatusLine().getStatusCode();
    		log.debug("\nResponse Code: " + httpResponseCode);
    		
    		String responseStr = EntityUtils.toString(response.getEntity());
    		log.debug("\nResponse Body: " + responseStr);
    		
    		try {
    			SmsResponse smsRespone =  gson.fromJson(responseStr, SmsResponse.class);
    			log.debug("\nResponse: " + smsRespone.toString());
    		
    			AlertDAL.saveSmsResponse(smsId, httpResponseCode, smsRespone);
    		} catch (JsonSyntaxException ex) {
    			log.error("Invalid response body sent from SMS provider: " + responseStr);
    		}
    		
    		return true;
    	} catch (ClientProtocolException ex) {
    		log.error("HTTP Client Protocol Exception: " + ex.getMessage());
    		throw new Exception("HTTP Client Protocol Exception", ex);
    	} catch (IOException exIO) {
    		log.error("HTTP IO Exception: " + exIO.getMessage());
    		throw new Exception("HTTP IO Exception", exIO);
    	} catch (SQLException sqlEx) {
    		log.error("SQL Exception: " + sqlEx.getMessage());
    		throw new Exception("SQL Exception", sqlEx);
    	}
	}
	
	public static boolean sendSMS(Sms sms) throws Exception {
		return sendSMS(sms.phoneNo, sms.message, sms.id);
	}
	
	
}

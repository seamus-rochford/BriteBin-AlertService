package com.trandonsystems.britebin.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.trandonsystems.britebin.database.AlertDAL;
import com.trandonsystems.britebin.database.SystemDAL;


public class PushNotificationServices {
	
	static Logger log = Logger.getLogger(SmsServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String FCM_AUTH_KEY = "AAAAD9UvohM:APA91bGppLoHeyMTywSHcVFVevBoX7inAshjECAH723w-rvRCKKBwGHFwa88AgxmqtvZBTylrhhweMYVFxsG1kX0avmtrv9Io8xoVP16gdECWL_K2FxVOLj3-CL6A10NfvEFvTPdX67A";
    public static String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";

	public static boolean initializePushNotification() throws Exception {
		try {
		
			String dbFcmAuthKey = SystemDAL.getSysConfigValue("FCM-AUTH-KEY");
			if (dbFcmAuthKey != null) {
				FCM_AUTH_KEY = dbFcmAuthKey;
			}
			String dbFcmApiUrl = SystemDAL.getSysConfigValue("FCM-API-URL");
			if (dbFcmApiUrl != null) {
				FCM_API_URL = dbFcmApiUrl;
			}
			
		} catch(SQLException ex) {
			log.error("ERROR: Failed to initialize FCM for Push notifications: " + ex.getMessage());
			log.error(ex.getStackTrace());
			throw ex;
		}
		
		return true;
	}
	    
    public static boolean sendPushNotification(String gcmToken, String title, String body, int pushNotificationId) throws IOException {

        URL url = new URL(FCM_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + FCM_AUTH_KEY);
        conn.setRequestProperty("Content-Type", "application/json");

        JsonObject json = new JsonObject();

        json.addProperty("to", gcmToken.trim());
        JsonObject message = new JsonObject();
        message.addProperty("title", title); // Notification title
        message.addProperty("body", body); // Notification body
        json.add("notification", message);
        
        log.debug("Json Object: " + gson.toJson(json));
        
        try {
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            log.debug("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                log.debug(output);
            }

            log.debug("GCM Notification is sent successfully");
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean pushNotificationHttp(String deviceToken, String title, String body, int pushNotificationId) throws Exception {
    	try {
    		HttpClient client = HttpClientBuilder.create().build();
    		HttpPost post = new HttpPost(FCM_API_URL);
    		log.debug("FCM-API-URL: " + FCM_API_URL);
    		
    		post.setHeader("Content-type", "application/json");
    		post.setHeader("Authorization", "key=" + FCM_AUTH_KEY);
    		log.debug("Authorization: " + FCM_AUTH_KEY);
    		
    		JsonObject message = new JsonObject();
    		message.addProperty("to", deviceToken);
    		message.addProperty("priority", "high");

    		JsonObject notification = new JsonObject();
    		notification.addProperty("title", title);
    		notification.addProperty("body", body);

    		message.add("notification", notification);
    		
    		log.debug("Message: " + message);

    		post.setEntity(new StringEntity(message.toString(), "UTF-8"));
    		HttpResponse response = client.execute(post);
    		
    		int httpResponseCode = response.getStatusLine().getStatusCode();
    		log.debug("\nResponse Code: " + httpResponseCode);
    		
    		String httpResponseBody = EntityUtils.toString(response.getEntity());
    		log.debug("\nResponse Body: " + httpResponseBody);
    		
    		log.debug(response);
    		
    		AlertDAL.savePushNotificationResponse(pushNotificationId, httpResponseCode, httpResponseBody);    		
    		
    		return true;
    	} catch (Exception ex) {
    		log.error("Error sending push notification: " + ex.getMessage());
    		throw ex;
    	}
    }
}

package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.AlertDAL;
import com.trandonsystems.britebin.model.Alert;
import com.trandonsystems.britebin.model.Email;
import com.trandonsystems.britebin.model.EmailDefn;
import com.trandonsystems.britebin.model.PushNotification;
import com.trandonsystems.britebin.model.PushNotificationDefn;
import com.trandonsystems.britebin.model.sms.Sms;
import com.trandonsystems.britebin.model.sms.SmsDefn;

public class AlertServices {

	static Logger log = Logger.getLogger(AlertServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	static List<EmailDefn> emailDefns;
	static List<SmsDefn> smsDefns;
	static List<PushNotificationDefn> pushNotificationDefns;

	public AlertServices() throws Exception {
		log.info("Constructor ...");
		try {
			// Load the email subjects and bodies
			emailDefns = AlertDAL.getAlertEmailDefns();
		} catch (Exception ex) {
			log.error("AlertServices.Constructor() - getAlertEmailDefns() ERROR: " + ex.getMessage());
		}
		
		try {
			// Load the SMS text definitions
			smsDefns = AlertDAL.getAlertSmsDefns();
		} catch (Exception ex) {
			log.error("AlertServices.Constructor() - getAlertSmsDefns() ERROR: " + ex.getMessage());
		}
		
		try {
			// Load the PushNotification definitions
			pushNotificationDefns = AlertDAL.getPushNotificationDefns();
		} catch (Exception ex) {
			log.error("AlertServices.Constructor() - getPushNotificationDefns() ERROR: " + ex.getMessage());
		}
		
		
	}
	
	private EmailDefn getEmailDefn(int alertType, String locale) throws Exception {
		
		// Attempt to get the correct locale email definition
		for (int i = 0; i < emailDefns.size(); i++) {
			if(emailDefns.get(i).alertType == alertType && emailDefns.get(i).locale.contentEquals(locale)) {
				return emailDefns.get(i);
			}
		}
		
		// If correct Local Email Definition not found, try and get the English Version 
		locale = "en-IE";
		for (int i = 0; i < emailDefns.size(); i++) {
			if(emailDefns.get(i).alertType == alertType && emailDefns.get(i).locale.contentEquals(locale)) {
				return emailDefns.get(i);
			}
		}
		
		// If no email definition found raise an error
		throw new Exception("No Email Definition Template found for alertType: " + alertType + " and locale: " + locale);
	}
	
	private SmsDefn getSmsDefn(int alertType, String locale) throws Exception {
		
		// Attempt to get the correct locale message definition
		for (int i = 0; i < smsDefns.size(); i++) {
			if(smsDefns.get(i).alertType == alertType && smsDefns.get(i).locale.contentEquals(locale)) {
				return smsDefns.get(i);
			}
		}
		
		// If correct Local Email Definition not found, try and get the English Version 
		locale = "en-IE";
		for (int i = 0; i < smsDefns.size(); i++) {
			if(smsDefns.get(i).alertType == alertType && smsDefns.get(i).locale.contentEquals(locale)) {
				return smsDefns.get(i);
			}
		}

		// if no sms definition found raise an error
		throw new Exception("No SMS Definition Template found for alertType: " + alertType + " and locale: " + locale);
	}
	
	private PushNotificationDefn getPushNotificationDefn(int alertType, String locale) throws Exception {
		
		// Attempt to get the correct locale message definition
		for (int i = 0; i < pushNotificationDefns.size(); i++) {
			if(pushNotificationDefns.get(i).alertType == alertType && pushNotificationDefns.get(i).locale.contentEquals(locale)) {
				return pushNotificationDefns.get(i);
			}
		}
		
		// If correct Local Email Definition not found, try and get the English Version 
		locale = "en-IE";
		for (int i = 0; i < pushNotificationDefns.size(); i++) {
			if(pushNotificationDefns.get(i).alertType == alertType && pushNotificationDefns.get(i).locale.contentEquals(locale)) {
				return pushNotificationDefns.get(i);
			}
		}

		// if no sms definition found raise an error
		throw new Exception("No Push Notification Definition Template found for alertType: " + alertType + " and locale: " + locale);
	}
	
	private String substitudeFields(String body, Alert alert) {
		
		// We need to get the date in the correct timezone
		String timeZoneName = "Europe/Dublin";
		Locale locale = new Locale("en", "IE");
		String datePattern = "yyyy-MM-dd HH:mm:ss";
		
		switch (alert.user.locale.abbr) {
		case "hr-HR":
			timeZoneName = "Europe/Zagreb";
			locale = new Locale("hr", "HR");
			datePattern = "yyyy-MM-dd HH:mm:ss";
			break;
		case "fr-FR":
			timeZoneName = "Europe/Paris";
			locale = new Locale("fr", "FR");
			datePattern = "yyyy-MM-dd HH:mm:ss";
			break;
		case "no-No":
			timeZoneName = "Europe/Oslo";
			locale = new Locale("no", "NO");
			datePattern = "yyyy-MM-dd HH:mm:ss";
			break;
		default:
			timeZoneName = "Europe/Dublin";
			locale = new Locale("en", "IE");
			datePattern = "yyyy-MM-dd HH:mm:ss";
		}
		
		ZoneId zoneId = ZoneId.of(timeZoneName);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern, locale).withZone(zoneId);
		String alertDateTimeStr = formatter.format(alert.alertDateTime);
		
		log.debug("Substitude fields");
		log.debug("AlertType: " + alert.alertType.name);
		log.debug("alertDateTime: " + alertDateTimeStr);
		log.debug("serialNo: " + alert.unit.serialNo);
		log.debug("location: " + alert.unit.location);
		log.debug("binType: " + alert.unit.binType.name);
		log.debug("deviceType: " + alert.unit.deviceType.name);
		log.debug("contentType: " + alert.unit.contentType.name);
		
		log.debug("Text before substitution: " + body);
		
		String newBody = body.replaceAll("@@alertType@@", alert.alertType.name);
		newBody = newBody.replaceAll("@@alertDateTime@@", alertDateTimeStr);
		newBody = newBody.replaceAll("@@serialNo@@", alert.unit.serialNo);
		newBody = newBody.replaceAll("@@location@@", alert.unit.location);
		newBody = newBody.replaceAll("@@binType@@", alert.unit.binType.name);
		newBody = newBody.replaceAll("@@deviceType@@", alert.unit.deviceType.name);
		newBody = newBody.replaceAll("@@contentType@@", alert.unit.contentType.name);
		
//		newBody = newBody.replaceAll("@@binFillLevel@@", alert.unitReading.percentFull);

		// Plug damage report fields also
		if (alert.damage != null) {
			log.debug("damageStatus: " +  alert.damage.damageStatus.name);
			log.debug("damageType: " +  alert.damage.damageType.name);
			log.debug("damageMsg: " +  alert.damage.damageHistory.get(0).comment);
			
			newBody = newBody.replaceAll("@@damageStatus@@", alert.damage.damageStatus.name);
			newBody = newBody.replaceAll("@@damageType@@", alert.damage.damageType.name);
			newBody = newBody.replaceAll("@@damageMsg@@", alert.damage.damageHistory.get(0).comment);
			// Get entire history
			String history = "";
			for (int i = 1; i < alert.damage.damageHistory.size(); i++) {
				history += alert.damage.damageHistory.get(i).comment;
			}
			log.debug("damageHistory: " +  history);
			
			newBody = newBody.replaceAll("@@damageHistory@@", history);
		}
		
		log.debug("Text after substitution: " + newBody);
		return newBody;
	}
	
	public void generateEmail(Alert alert) {
		
		try {
			log.debug("Email");
			EmailDefn emailDefn = getEmailDefn(alert.alertType.id, alert.user.locale.abbr);
			
			log.debug("Get subject");
			String subject = emailDefn.subject;
			
			log.debug("Get email body");
			String emailBody = substitudeFields(emailDefn.body, alert);
			
			AlertDAL.generateEmail(alert.id, alert.user.email, subject, emailDefn.htmlBody, emailBody);
			
			log.info("Email generated: alertId: " + alert.id + "    Email: " + alert.user.email + "    Msg: " + emailBody);
			
		} catch (Exception ex) {
			String errorMsg = "ERROR: failed to send email for alertId: " + alert.id + " - Email: " + alert.user.email + "  error: " + ex.getMessage();
			log.error(errorMsg);
		}
	}
	
	
	public void generateSms(Alert alert) {
		
		try {

			log.debug("generateSms");
			SmsDefn smsDefn = getSmsDefn(alert.alertType.id, alert.user.locale.abbr);
	
			log.debug("Get sms message");
			String smsMessage = substitudeFields(smsDefn.message, alert);
			
			String phoneNo = UtilServices.stripPhoneNo(alert.user.mobile);
			
			if (phoneNo == null || phoneNo.length() < 7) {
				throw new Exception("Invalid phone number: " + phoneNo + " for alertId: " + alert.id);
			}
			AlertDAL.generateSms(alert.id, phoneNo, smsMessage);
						
			log.info("SMS generated: alertId: " + alert.id + "   phoneNo: " + phoneNo + "    Msg: " + smsMessage);
			
		} catch (Exception ex) {
			String errorMsg = "ERROR: failed to generate SMS for alertId: " + alert.id + " - SMS Number: " + alert.user.mobile + "  error: " + ex.getMessage();
			log.error(errorMsg);
		}
	}
	
	
	public void generateWhatsApp(Alert alert) {
		// Not implemented
		try {

		} catch (Exception ex) {
			String errorMsg = "ERROR: failed to send WhatsApp for alertId: " + alert.id + " - Mobile Number: " + alert.user.mobile + "  error: " + ex.getMessage();
			log.error(errorMsg);
		}	
	}
	
	
	public void generatePush(Alert alert) {
		// Not implemented
		try {

			log.debug("generatePushNotification");
			PushNotificationDefn pushNotificationDefn = getPushNotificationDefn(alert.alertType.id, alert.user.locale.abbr);
	
			log.debug("Get push notification title/body");
			String title = substitudeFields(pushNotificationDefn.title, alert);
			String body = substitudeFields(pushNotificationDefn.body, alert);
			
			String gcmToken = alert.user.gcmToken;
			if (gcmToken == null) {
				throw new Exception("Invalid gcmToken: " + gcmToken + "  for alertId: " + alert.id);
			}
			
			AlertDAL.generatePushNotification(alert.id, gcmToken, title, body);
			
			log.info("Push Notification generated: alertId: " + alert.id + "    title: " + title + "    body: " + body + "   gcmToken: " + gcmToken);
			
		} catch (Exception ex) {
			String errorMsg = "ERROR: failed to send Push Notification for alertId: " + alert.id  + "   gcmToken: " + alert.user.gcmToken + "   error: " + ex.getMessage();
			log.error(errorMsg);
		}	
	}
	
	
	private void processAlert(Alert alert) {
		
		try {
			log.info("processAlert: " + alert.id);
			
			if (alert.alertDefn.notifyByEmail) {
				log.info("Notify by email");
				generateEmail(alert);
			}
			if (alert.alertDefn.notifyBySms) {
				log.info("Notify by SMS");
				generateSms(alert);
			}
			if (alert.alertDefn.notifyByWhatsApp) {
				log.info("Notify by WhatsApp");
				generateWhatsApp(alert);
			}
			if (alert.alertDefn.notifyByPushNotification) {
				log.info("Notify by Push Notification");
				generatePush(alert);
			}

			// Mark alert as processed
			AlertDAL.markAlertAsProcessed(alert.id);
			
		} catch(Exception ex) {
			String errorMsg = "ERROR (processAlert); " + ex.getMessage(); 
			log.error(errorMsg);
		}
	}
	
	
	public void processWaitingAlerts() {
		log.info("AlertServices.processWaitingAlerts() - started");
		
		try {
			List<Alert> waitingAlerts = AlertDAL.getWaitingAlerts();
			log.info("No. of Alerts to Process: " + waitingAlerts.size());
			
			for (int i = 0; i < waitingAlerts.size(); i++) {
				processAlert(waitingAlerts.get(i));
			}
			
			sendEmails();
			sendSms();
//			sendWhatsApp();
			sendPushNotifications();
			
		} catch (Exception ex) {
			log.error("processWaitingAlerts: ERROR: " + ex.getMessage());
		}
		log.info("AlertServices.processWaitingAlerts() - complete");
	}
	
	
	public void sendEmails() {
		
		log.info("Send waiting Emails");
		try {
			
			// Get list of unsent Mails
			List<Email> emailList = AlertDAL.getWaitingEmails();
			
			int emailCount =  0;
			for(int i = 0; i < emailList.size(); i++) {
				Email email = emailList.get(i);
				try {
					JavaMailServices.sendMail(email.emailAddr, email.subject, email.htmlBody, email.body);
					AlertDAL.markEmailAlertAsSent(email.id);
					emailCount++;
				} catch(Exception ex) {
					AlertDAL.markEmailAlertAsFailed(email.id, ex.getMessage());
				}
			}
			log.info("All waiting Emails (" + emailList.size() + ") Processed - " + emailCount +  " successfully sent");
		} catch (Exception ex) {
			log.error("processWaitingAlerts: ERROR: " + ex.getMessage());
		}			
		
	}
	
	
	public void sendSms() {
		
		Sms sms = new Sms();;
		try {
			
			// Get list of unsent sms
			List<Sms> smsList = AlertDAL.getWaitingSms();
			
			int smsCount = 0;
			for(int i = 0; i < smsList.size(); i++) {
				sms = smsList.get(i);
				try {
					SmsServices.sendSMS(sms);
					smsCount++;
				} catch (Exception ex) {
					log.error("sendSms failed: " + ex.getMessage());
					AlertDAL.markSmsAlertAsFailed(sms.id, ex.getMessage());
				}
			}
			log.info("All waiting SMS's (" +  smsList.size() + ") Processed - " + smsCount +  " successfully sent");
			
		} catch (SQLException ex) {
			log.error("sendSms: ERROR: " + ex.getMessage());
		}		
	}
	
	
	public void sendWhatsApp() {
		
	}	

	public void sendPushNotifications() {
		PushNotification pushNotification = new PushNotification();;
		try {
			
			// Get list of unsent push notifications
			List<PushNotification> pushNotifications = AlertDAL.getWaitingPushNotifications();
			
			int pushCount = 0;
			for(int i = 0; i < pushNotifications.size(); i++) {
				pushNotification = pushNotifications.get(i);
		    	log.debug("pushNotification: " + gson.toJson(pushNotification));
				try {
					PushNotificationServices.pushNotificationHttp(pushNotification.gcmToken, pushNotification.title, pushNotification.body, pushNotification.id);
					pushCount++;
				} catch (Exception ex) {
					log.error("Push notification failed - gcmToken: " + pushNotification.gcmToken + " - " + ex.getMessage());
					AlertDAL.markPushNotificationAlertAsFailed(pushNotification.id, ex.getMessage());
				}
			}
			log.info("All waiting Push Notifications (" +  pushNotifications.size() + ") Processed - " + pushCount +  " successfully sent");
			
		} catch (SQLException ex) {
			log.error("sendPushNotifications: ERROR: " + ex.getMessage());
		}			
	}	

}

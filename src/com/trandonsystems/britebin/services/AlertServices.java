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
import com.trandonsystems.britebin.model.EmailDefn;
import com.trandonsystems.britebin.model.sms.Sms;
import com.trandonsystems.britebin.model.sms.SmsDefn;

public class AlertServices {

	static Logger log = Logger.getLogger(AlertServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	static List<EmailDefn> emailDefns;
	static List<SmsDefn> smsDefns;

	public AlertServices() throws Exception {
		log.info("Constructor ...");
		try {
			// Load the email subjects and bodies
			emailDefns = AlertDAL.getAlertEmailDefns();
		} catch (Exception ex) {
			log.error("AlertServices.Constructor() - getAlertEmailDefns() ERROR: " + ex.getMessage());
		}
		
		try {
			// Load the email subjects and bodies
			smsDefns = AlertDAL.getAlertSmsDefns();
		} catch (Exception ex) {
			log.error("AlertServices.Constructor() - getAlertSmsDefns() ERROR: " + ex.getMessage());
		}
		
		
	}
	
	private EmailDefn getEmailDefn(int alertType, String locale) {
		
		EmailDefn emailDefn = new EmailDefn();
		
		for (int i = 0; i < emailDefns.size(); i++) {
			if(emailDefns.get(i).alertType == alertType && emailDefns.get(i).locale.contentEquals(locale)) {
				return emailDefns.get(i);
			}
		}
		
		return emailDefn;
	}
	
	private SmsDefn getSmsDefn(int alertType, String locale) {
		
		SmsDefn smsDefn = new SmsDefn();
		
		for (int i = 0; i < smsDefns.size(); i++) {
			if(smsDefns.get(i).alertType == alertType && smsDefns.get(i).locale.contentEquals(locale)) {
				return smsDefns.get(i);
			}
		}
		
		return smsDefn;
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
		
		log.debug("Email body" + body);
		
		String newBody = body.replaceAll("@@alertType@@", alert.alertType.name);
		newBody = newBody.replaceAll("@@alertDateTime@@", alertDateTimeStr);
		newBody = newBody.replaceAll("@@serialNo@@", alert.unit.serialNo);
		newBody = newBody.replaceAll("@@location@@", alert.unit.location);
		newBody = newBody.replaceAll("@@binType@@", alert.unit.binType.name);
		newBody = newBody.replaceAll("@@deviceType@@", alert.unit.deviceType.name);
		newBody = newBody.replaceAll("@@contentType@@", alert.unit.contentType.name);
		
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
		
		log.debug("New Body: " + newBody);
		return newBody;
	}
	
	public void email(Alert alert) throws Exception {
		
		try {
			log.debug("Email");
			EmailDefn emailDefn = getEmailDefn(alert.alertType.id, alert.user.locale.abbr);
			
			log.debug("Get subject");
			String subject = emailDefn.subject;
			
			log.debug("Get email body");
			String emailBody = substitudeFields(emailDefn.body, alert);
			
			JavaMailServices.sendMail(alert.user.email, subject, emailDefn.htmlBody, emailBody);
			
			log.info("Email sent: " + alert.user.email + "    Msg: " + emailBody);
			
		} catch (SQLException ex) {
			String errorMsg = "ERROR: failed to send email for alertId: " + alert.id + " - Email: " + alert.user.email + "  error: " + ex.getMessage();
			log.error(errorMsg);
//			AlertDAL.markEmailAsFailed(alert.id, 1, alert.user.email, errorMsg);
			throw ex;
		}
	}
	
	
	public void generateSms(Alert alert) throws Exception {
		
		try {

			log.debug("generateSms");
			SmsDefn smsDefn = getSmsDefn(alert.alertType.id, alert.user.locale.abbr);
	
			log.debug("Get sms message");
			String smsMessage = substitudeFields(smsDefn.message, alert);
			
			String phoneNo = UtilServices.stripPhoneNo(alert.user.mobile);
			
			AlertDAL.generateSms(alert.id, phoneNo, smsMessage);
						
			log.info("Email sent: " + alert.user.email + "    Msg: " + smsMessage);
			
		} catch (Exception ex) {
			String errorMsg = "ERROR: failed to generate SMS for alertId: " + alert.id + " - SMS Number: " + alert.user.mobile + "  error: " + ex.getMessage();
			log.error(errorMsg);
//			AlertDAL.markSmsAlertAsFailed(alert.id, 2, alert.user.email, errorMsg);
			throw ex;
		}
	}
	
	
	public void whatsApp(Alert alert) throws Exception {
		// Not implemented
		try {

		} catch (Exception ex) {
			String errorMsg = "ERROR: failed to send WhatsApp for alertId: " + alert.id + " - Mobile Number: " + alert.user.mobile + "  error: " + ex.getMessage();
			log.error(errorMsg);
//			AlertDAL.markWhatsAppAsFailed(alert.id, 3, alert.user.email, errorMsg);
			throw ex;
		}	
	}
	
	
	public void push(Alert alert) throws Exception {
		// Not implemented
		try {

		} catch (Exception ex) {
			String errorMsg = "ERROR: failed to send Push Notification for alertId: " + alert.id + " - Mobile Number: " + alert.user.mobile + "  error: " + ex.getMessage();
			log.error(errorMsg);
//			AlertDAL.markPushAsFailed(alert.id, 4, alert.user.email, errorMsg);
			throw ex;
		}	
	}
	
	
	private void processAlert(Alert alert) {
		
		try {
			log.info("processAlert: " + alert.id);
			
			if (alert.alertDefn.notifyByEmail) {
				log.info("Notify by email");
				email(alert);
			}
			if (alert.alertDefn.notifyBySms) {
				log.info("Notify by SMS");
				generateSms(alert);
			}
			if (alert.alertDefn.notifyByWhatsApp) {
				log.info("Notify by WhatsApp");
				whatsApp(alert);
			}
			if (alert.alertDefn.notifyByPushNotification) {
				log.info("Notify by Push Notification");
				push(alert);
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
			
//			senEmails();
			sendSms();
//			sendWhatsApp();
//			sendPushNotifications();
			
		} catch (Exception ex) {
			log.error("processWaitingAlerts: ERROR: " + ex.getMessage());
		}
		log.info("AlertServices.processWaitingAlerts() - complete");
	}
	
	
	public void sendEmails() {
		
	}
	
	
	public void sendSms() {
		try {
			
			// Get list of unsent sms
			List<Sms> smsList = AlertDAL.getWaitingSms();
			
			for(int i = 0; i < smsList.size(); i++) {
				SmsServices.sendSMS(smsList.get(i));
			}
			
		} catch (SQLException ex) {
			log.error("processWaitingAlerts: ERROR: " + ex.getMessage());
		}		
	}
	
	
	public void sendWhatsApp() {
		
	}	

	public void sendPushNotifications() {
		
	}	

}

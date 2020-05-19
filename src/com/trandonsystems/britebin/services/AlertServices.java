package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.AlertDAL;
import com.trandonsystems.britebin.model.Alert;
import com.trandonsystems.britebin.model.EmailDefn;

public class AlertServices {

	static Logger log = Logger.getLogger(AlertServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	static List<EmailDefn> emailDefns;

	public AlertServices() {
		log.info("Constructor ...");
		// Load the email subjects and bodies
		emailDefns = AlertDAL.getAlertEmailDefns();
	}
	
	private EmailDefn getEmailDefn(int alertType) {
		
		EmailDefn emailDefn = new EmailDefn();
		
		for (int i = 0; i < emailDefns.size(); i++) {
			if(emailDefns.get(i).alertType == alertType) {
				return emailDefns.get(i);
			}
		}
		
		return emailDefn;
	}
	
	private String substitudeFields(String body, Alert alert) {
		String newBody = body.replaceAll("@@alertType@@", alert.alertType.name);
		newBody = newBody.replaceAll("@@alertDateTime@@", alert.alertDateTime.toString());
		newBody = newBody.replaceAll("@@serialNo@@", alert.unit.serialNo);
		newBody = newBody.replaceAll("@@location@@", alert.unit.location);
		newBody = newBody.replaceAll("@@binType@@", alert.unit.binType.name);
		newBody = newBody.replaceAll("@@deviceType@@", alert.unit.deviceType.name);
		newBody = newBody.replaceAll("@@contentType@@", alert.unit.contentType.name);
		
		return newBody;
	}
	
	public void email(Alert alert) throws Exception {
		
		try {
			EmailDefn emailDefn = getEmailDefn(alert.alertType.id);
			
			String subject = emailDefn.subject;
			
			String emailBody = substitudeFields(emailDefn.body, alert);
			
			JavaMailServices.sendMail(alert.user.email, subject, emailDefn.htmlBody, emailBody);
						
		} catch (SQLException ex) {
			log.error("ERROR: failed to send email for alertId: " + alert.id + " - Eamil: " + alert.user.email + "  error: " + ex.getMessage());
			throw ex;
		}
	}
	
	
	public void sms(Alert alert) {
		// Not implemented
	}
	
	
	public void whatsApp(Alert alert) {
		// Not implemented
	}
	
	
	public void push(Alert alert) {
		// Not implemented
	}
	
	
	private void processAlert(Alert alert) {
		
		try {
			if (alert.alertDefn.notifyByEmail) {
				email(alert);
			}
			if (alert.alertDefn.notifyBySms) {
				sms(alert);
			}
			if (alert.alertDefn.notifyByWhatsApp) {
				whatsApp(alert);
			}
			if (alert.alertDefn.notifyByPushNotification) {
				push(alert);
			}
			
			// Mark alert as processed
			AlertDAL.markAlertAsProcessed(alert.id);
			
		} catch(Exception ex) {
			String errorMsg = "ERROR (processAlert); " + ex.getMessage(); 
			log.error(errorMsg);
			AlertDAL.markAlertAsFailed(alert.id, errorMsg);
		}
		

	}
	
	public void processWaitingAlerts() {
		log.info("AlertServices.processWaitingAlerts() - started");
		
		try {
			List<Alert> waitingAlerts = AlertDAL.getWaitingAlerts();
			log.info("Total No. Alerts to process: " + waitingAlerts.size());
			
			for (int i = 0; i < waitingAlerts.size(); i++) {
				processAlert(waitingAlerts.get(i));
			}
			
		} catch (Exception ex) {
			log.error("processWaitingAlerts: ERROR: " + ex.getMessage());
		}
		log.info("AlertServices.processWaitingAlerts() - complete");
	}
	
	
}

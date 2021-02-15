package com.trandonsystems.britebin.services;

import java.sql.SQLException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trandonsystems.britebin.database.SystemDAL;

public class JavaMailServices {

	static String SMTP_HOST = "smtp.gmail.com";
	static String SMTP_PORT = "587";
	static String SMTP_ACCOUNT_EMAIL = "someone@gmail.com";
	static String SMTP_PASSWORD = "password";
	
	static Logger log = Logger.getLogger(JavaMailServices.class);
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static boolean initializeEmailer() throws Exception {
		try {
		
			SMTP_HOST = SystemDAL.getSysConfigValue("SMTP-HOST");
			SMTP_PORT = SystemDAL.getSysConfigValue("SMTP-PORT");
			SMTP_ACCOUNT_EMAIL = SystemDAL.getSysConfigValue("SMTP-ACCOUNT-EMAIL");
			SMTP_PASSWORD = SystemDAL.getSysConfigValue("SMTP-PASSWORD");
			
		} catch(SQLException ex) {
			log.error("ERROR: failed to initialize Emailer: " + ex.getMessage());
			log.error(ex.getStackTrace());
			throw ex;		
		}
		
		return true;
	}
	
	public static void sendMail(String recepient, String subject, boolean htmlBody, String body) throws Exception {
		try {
//			Properties properties = new Properties();
//			
//			properties.put("mail.smtp.auth", "true");
//			properties.put("mail.smtp.starttls.enabled", "true");
//			properties.put("mail.smtp.starttls.required", "true");
//			properties.put("mail.smtp.host", SMTP_HOST);
//			properties.put("mail.smtp.port", SMTP_PORT);

			String myAccountEmail = SMTP_ACCOUNT_EMAIL;
			String password = SMTP_PASSWORD;
			
			log.debug("sendMail - start");
			Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", SMTP_HOST);
			log.debug("sendMail - mail.smtp.host set: " + SMTP_HOST);
			
			properties.put("mail.smtp.port", SMTP_PORT);
			log.debug("sendMail - mail.smtp.port set: " + SMTP_PORT);
			if (!password.equals("")) {
				properties.put("mail.smtp.auth", "true");
				log.debug("sendMail - mail.smtp.auth set: " + "true");
				
				properties.put("mail.smtp.socketFactory.port", SMTP_PORT);
			} else {
				properties.put("mail.smtp.auth", "false");
				log.debug("sendMail - mail.smtp.auth set: " + "false");
			}
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			log.debug("sendMail - mail.smtp.socketFactory.class set: " + "javax.net.ssl.SSLSocketFactory");

			Session session = Session.getInstance(properties, null);
			if (!password.equals("")) {
				session = Session.getInstance(properties, new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(myAccountEmail, password);
					}
				});
				log.debug("sendMail - Authentication Session created");
			} else {
				log.debug("sendMail - Session created without authentication");
			}
			
			session.setDebug(true);

			log.debug("sendMail - prepareMessage");
			
			Message message = prepareMessage(session, myAccountEmail, recepient, subject, htmlBody, body);
			log.debug("sendMail - prepareMessage complete");
			
			Transport.send(message);
			
			log.info("Message sent successfully - " + recepient);

		} catch (Exception ex) {
			log.error("ERROR in sendMail: " + ex.getMessage());
			throw ex;
		}
	}

	private static Message prepareMessage(Session session, String myAccountEmail, String recepient, String subject, boolean htmlBody, String body) throws Exception {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(myAccountEmail));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
			message.setSubject(subject);
			
			if (htmlBody) {
//				message.setContent(body, "text/html");
				
				// This Message has 2 parts - the body and the embed image
		         MimeMultipart multipart = new MimeMultipart("related");

		         // first part (the html)
		         BodyPart messageBodyPart = new MimeBodyPart();
		         // The body will have <img src=\"cid:image\"> inside it
		         String htmlText = body;
		         messageBodyPart.setContent(htmlText, "text/html");
		         // add it
		         multipart.addBodyPart(messageBodyPart);

		         // second part (the image)
		         messageBodyPart = new MimeBodyPart();
		         DataSource fds = new FileDataSource("logo.png");

		         messageBodyPart.setDataHandler(new DataHandler(fds));
		         messageBodyPart.setHeader("Content-ID", "<image>");

		         // add image to the multipart
		         multipart.addBodyPart(messageBodyPart);

		         // put everything together
		         message.setContent(multipart);				
			} else {
				// Plain text body
				message.setText(body);
			}
			
			return message;
		} catch (Exception ex) {
			log.error("ERROR in prepareMessage:" + ex.getMessage());
			throw ex;
		}
	}

	public static void sendMail(String[] recepients, String subject, String msg) throws Exception {
		
		try {
			Properties properties = new Properties();
			
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enabled", "true");
			properties.put("mail.smtp.starttls.required", "true");
			properties.put("mail.smtp.host", SMTP_HOST);
			properties.put("mail.smtp.port", SMTP_PORT);
			
			String myAccountEmail = SMTP_ACCOUNT_EMAIL;
			String password = SMTP_PASSWORD;
			
			Session session = Session.getInstance(properties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(myAccountEmail, password);
				}
			});
			
			Message message = prepareMessage(session, myAccountEmail, recepients, subject, msg);
			
			Transport.send(message);
			
			log.info("Messages sent successfully");

		} catch (Exception ex) {
			log.error("ERROR in sendMail: " + ex.getMessage());
			throw ex;
		}
	}

	private static Message prepareMessage(Session session, String myAccountEmail, String[] recepients, String subject, String msg) throws Exception {

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(myAccountEmail));
			for (int i = 0; i < recepients.length; i++) {
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepients[i]));
			}
			message.setSubject(subject);
			message.setText(msg);
			
			return message;
		} catch (Exception ex) {
			log.error("ERROR in prepareMessage: " + ex.getMessage());
			throw ex;
		}
	}

}

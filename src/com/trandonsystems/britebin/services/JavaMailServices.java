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

import com.trandonsystems.britebin.database.SystemDAL;

public class JavaMailServices {

	static String SMTP_HOST = "smtp.gmail.com";
	static String SMTP_PORT = "587";
	static String SMTP_ACCOUNT_EMAIL = "someone@gmail.com";
	static String SMTP_PASSWORD = "password";
	
	static Logger log = Logger.getLogger(JavaMailServices.class);
	
	public static boolean initializeEmailer() throws Exception {
		try {
		
			SMTP_HOST = SystemDAL.getSysConfigValue("SMTP-HOST");
			SMTP_PORT = SystemDAL.getSysConfigValue("SMTP-PORT");
			SMTP_ACCOUNT_EMAIL = SystemDAL.getSysConfigValue("SMTP-ACCOUNT-EMAIL");
			SMTP_PASSWORD = SystemDAL.getSysConfigValue("SMTP-PASSWORD");
			
		} catch(SQLException ex) {
			log.error("ERROR: failed to initialize Emailer: " + ex.getMessage());
			log.error(ex.getStackTrace());
			throw ex;		}
		
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

			Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", SMTP_HOST);
			
			properties.put("mail.smtp.port", SMTP_PORT);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

			String myAccountEmail = SMTP_ACCOUNT_EMAIL;
			String password = SMTP_PASSWORD;
			
			Session session = Session.getInstance(properties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(myAccountEmail, password);
				}
			});
			
			Message message = prepareMessage(session, myAccountEmail, recepient, subject, htmlBody, body);
			
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

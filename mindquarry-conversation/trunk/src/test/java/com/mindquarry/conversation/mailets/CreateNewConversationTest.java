/*
 * Copyright (C) 2006, Mindquarry GmbH 
 */
package com.mindquarry.conversation.mailets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

/**
 * @author <a hef="mailto:alexander(dot)saar(at)mindquarry(dot)com</a>
 */
public class CreateNewConversationTest extends TestCase {
	private static final String P_FILE_NAME = "src/test/resources/mail.properties";

	public void testSendMail() throws IOException, MessagingException {
		File pFile = new File(P_FILE_NAME);
		InputStream pis = new FileInputStream(pFile);

		Properties props = new Properties();
		props.load(pis);

		Address from = new InternetAddress("alexander.saar@mindquarry.com", "Alexander Saar");
		Address to = new InternetAddress("Cyclr-dev@conversation.mindquarry.com");
		Address to2 = new InternetAddress("Cyclr-support@conversation.mindquarry.com");

		Session session = Session.getDefaultInstance(props, null);
		MimeMessage msg = new MimeMessage(session);

		msg.setFrom(from);
		msg.addRecipient(Message.RecipientType.TO, to);
		msg.addRecipient(Message.RecipientType.TO, to2);

		msg.setSubject("new conversation");
		msg.setText("a question to the developers...");

		Transport.send(msg);
	}
}

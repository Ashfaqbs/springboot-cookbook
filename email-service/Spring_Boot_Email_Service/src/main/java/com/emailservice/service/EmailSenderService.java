package com.emailservice.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSenderService {

	
	//Autowiring mail sender interface
	@Autowired
	private JavaMailSender sender;
	
	//creating a method to send mails. NOTE: attachment means path of the file
	public void sendMailWithAttachMent(String to, String body, String Subject,String attachment) throws MessagingException
	{
		
		//using the mime message
		MimeMessage message = sender.createMimeMessage();
		
		//using the mimemessage helper
		MimeMessageHelper helper = new MimeMessageHelper(message,true);
		
	
		helper.setFrom("ash001x@gmail.com");
		helper.setTo(to);
		helper.setText(body);
		helper.setSubject(Subject);
		
		
		//attachment related
		FileSystemResource resource = new FileSystemResource(new File(attachment));
		helper.addAttachment(resource.getFilename(),resource);
		
		sender.send(message);
		System.out.println("Message Send");
		
		
		
		
	}
	
	
	
	
}

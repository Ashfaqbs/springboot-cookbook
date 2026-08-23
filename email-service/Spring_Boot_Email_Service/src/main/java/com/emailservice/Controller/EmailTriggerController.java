package com.emailservice.Controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailservice.service.EmailSenderService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/email")
public class EmailTriggerController {
	
	
	@Autowired
	private EmailSenderService service;
	
	@GetMapping("/1")
	public void Dummy()
	{
	
try {
	this.service.sendMailWithAttachMent("ashfaq786786.wwe@gmail.com","This is a body "
			+ "attaching a dummy mail with some files"
			+" "
			,"Mail testing is the Subject","C:\\Users\\Ashfaq PC\\Downloads\\file.txt");
	System.out.println("Success");
} catch (MessagingException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	System.out.println("error");
}
		
		
	}
	
	@GetMapping("/2/{content}")
	public void Custom(@PathVariable("content")String Content)
	{
		
		try {
			this.service.sendMailWithAttachMent("ashfaq786786.wwe@gmail.com",Content
					,"Mail testing is the Subject","C:\\Users\\Ashfaq PC\\Downloads\\file.txt");
			System.out.println("Success");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error");
		}
		
		
	}
	
	@GetMapping("/3/{to}/{content}")
	public void superCustom(@PathVariable("to") String to , @PathVariable("content") String content) throws IOException
	{
		
		
	    String Content = content;

	    // The path of the folder where the file will be saved
	    String folderPath = "C:\\Users\\Ashfaq PC\\Downloads";

	    // The name of the file
	    String fileName = "file1.txt";

	    // Create a file object with the full path of the file
	    File file = new File(folderPath, fileName);

	
	      // Create a FileWriter object
	      FileWriter fw = new FileWriter(file);

	      // Wrap the FileWriter object in a BufferedWriter
	      BufferedWriter bw = new BufferedWriter(fw);

	      // Write the string to the file
	      bw.write(content);

	      // Close the BufferedWriter
	      bw.close();
		
		
		
		try {
			this.service.sendMailWithAttachMent(to,content
					,"Mail testing is the Subject","C:\\Users\\Ashfaq PC\\Downloads\\file1.txt");
			System.out.println("Success");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error");
		}
		
		
	}
}

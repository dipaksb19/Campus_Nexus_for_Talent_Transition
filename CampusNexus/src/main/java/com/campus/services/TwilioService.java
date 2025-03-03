package com.campus.services;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.campus.config.TwilioConfig;
import com.campus.entity.JobPosting;
import com.campus.entity.Student;
import com.campus.repository.StudentRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioService {

	@Autowired
	private TwilioConfig twilioConfig;

	@Autowired
	private StudentRepository studentRepository;

	@PostConstruct  // it will act as a init method
	public void initTwilio() {
		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
	}



	//Notifications

	// function to get all students whom i have to send message 
	public void jobNotificationSMS(JobPosting job) {

		String message = "Dear Candidate,\r\n"
				+ "We have an exciting new job opportunity for you :\r\n"
				+ "Company Name : " + job.getCompanyName() + "\r\n"
				+ "Job Role : " + job.getJob_title()+ " \r\n"
				+ "For more details and to apply, please visit  Your Login.\r\n"
				+ "Best regards,  \r\n"
				+ "Placement Team  \r\n"
				+ "[Campus_Nexus]";

		List<Student> list = studentRepository.findAll();
		for(Student student : list) {
			try {
				PhoneNumber to = new PhoneNumber("+91"+Long.toString(student.getMobile()));
				PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
				Message.creator(to, from, message).create();
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("Failed to send message to the student : " + student);
			}

		}

	}




}

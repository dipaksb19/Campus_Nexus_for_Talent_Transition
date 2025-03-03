package com.campus.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.campus.entity.JobPosting;
import com.campus.entity.Student;
import com.campus.entity.User;
import com.campus.repository.StudentRepository;

@Service
public class MailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private StudentRepository studentRepository;

	// Coordinator Account created mail
	public void userAccountCreatedMail(User user) {
		String subject = "Congratulations on Successfully Creating Your Account !";
		String text = "Dear "+ user.getName() +",\r\n"
				+ "You have successfully created your Cordinator account, and \n"
				+ "You are one step away to be the part of our community \n"
				+ "Currently your account is under process \n"
				+ "please wait until our Admin (Training and Placement Officer) \n"
				+ "to verrify you as a coordinator. \n"
				+ "Once you get verification status mail as (Verified) \n"
				+ "You will be able to get access to your account\n"
				+ "Best regards,  \r\n"
				+ "Placement Team  \r\n"
				+ "[Campus_Nexus]";

		SimpleMailMessage message = new SimpleMailMessage();

		try {
			message.setTo(user.getEmail());
			message.setSubject(subject);
			message.setText(text);
			mailSender.send(message);

		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Fail to send mail to coordinator : " + user);
		}
	}


	// Coordinator Account Verified mail
	public void userVerificationMail(User user) {
		String subject = "Verification Status (Verified)";
		String text = "Dear "+ user.getName() +",\r\n"
				+ "You are successfully Verified by the TPO as Cordinator\n"
				+ "Your Details : \n"
				+ "Id : " + user.getId() + "\r\n"
				+ "Name : " + user.getName() + "\r\n"
				+ "Role : " + user.getRole() + "\r\n"
				+ "You can log in to your Account by using your credentials\n"
				+ "Thank You\n"
				+ "Best regards,  \r\n"
				+ "Placement Team  \r\n"
				+ "[Campus_Nexus]";

		SimpleMailMessage message = new SimpleMailMessage();

		try {
			message.setTo(user.getEmail());
			message.setSubject(subject);
			message.setText(text);
			mailSender.send(message);

		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Fail to send verification mail to coordinator : " + user);
		}
	}



	// Student account creation mail
	public void studentAccountCreatedMail(Student student) {
		String subject = "Congratulations on Successfully Creating Your Account !";
		String text = "Dear Candidate,\r\n"
				+ "You have successfully created your account, and \n"
				+ "we are excited to have you as part of our community. \n"
				+ "Our platform is designed to support you every step \n"
				+ "of the way in your journey towards securing the best \n"
				+ "placement opportunities.\n"
				+ "Please login to your account and complete your profile \n"
				+ "Best regards,  \r\n"
				+ "Placement Team  \r\n"
				+ "[Campus_Nexus]";

		SimpleMailMessage message = new SimpleMailMessage();

		try {
			message.setTo(student.getEmail());
			message.setSubject(subject);
			message.setText(text);
			mailSender.send(message);
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Failed to send Mail to the student : " + student);
		}

	}



	// Job Posting Mail Sending
	public void jobNotificationMail(JobPosting job) {
		String subject = "Campus Nexus New Job Posting Alert";
		String text = "Dear Candidate,\r\n"
				+ "We have an exciting new job opportunity for you :\r\n"
				+ "Company Name : " + job.getCompanyName() + "\r\n"
				+ "Job Role : " + job.getJob_title()+ " \r\n"
				+ "For more details and to apply, please visit  Your Login.\r\n"
				+ "Best regards,  \r\n"
				+ "Placement Team  \r\n"
				+ "[Campus_Nexus]";

		List<Student> list = studentRepository.findAll();
		SimpleMailMessage message = new SimpleMailMessage();

		for(Student student : list) {
			try {
				message.setTo(student.getEmail());
				message.setSubject(subject);
				message.setText(text);
				mailSender.send(message);
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("Failed to send Mail to the student : " + student);
			}

		}

	}



	// Job Updated Notification (Made changes in criteria, Location, Package)
	// Job Posting Mail Sending
	public void jobUpdatedNotificationMail(JobPosting job) {
		String subject = "Campus Nexus Posting Updated Alert";
		String text = "Dear Candidate,\r\n"
				+ "We have made some changes in below job opportunity :\r\n"
				+ "Job ID : " + job.getId() + "\r\n"
				+ "Company Name : " + job.getCompanyName() + "\r\n"
				+ "Job Role : " + job.getJob_title()+ " \r\n"
				+ "For more details and to apply, please visit  Your Login.\r\n"
				+ "Best regards,  \r\n"
				+ "Placement Team  \r\n"
				+ "[Campus_Nexus]";

		List<Student> list = studentRepository.findAll();
		SimpleMailMessage message = new SimpleMailMessage();

		for(Student student : list) {
			try {
				message.setTo(student.getEmail());
				message.setSubject(subject);
				message.setText(text);
				mailSender.send(message);
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("Failed to send Mail to the student : " + student);
			}

		}

	}


}

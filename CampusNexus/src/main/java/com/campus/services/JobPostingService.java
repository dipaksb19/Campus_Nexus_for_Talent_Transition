package com.campus.services;

import com.campus.config.TwilioConfig;
import com.campus.entity.JobPosting;
import com.campus.entity.Student;
import com.campus.entity.User;
import com.campus.model.JobPostingRequest;
import com.campus.model.JobPostingResponse;
import com.campus.repository.JobPostingRepository;
import com.campus.repository.StudentRepository;
import com.campus.repository.UserRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobPostingService {

	@Autowired
	private JobPostingRepository jobPostingRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private TwilioConfig twilioConfig;
	
	@Autowired
	private JavaMailSender mailSender;
	
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
		
	
	
	// Create a new job posting
	public JobPostingResponse createJobPosting(JobPostingRequest request, Long postedById) {
		User user = userRepository.findById(postedById)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		JobPosting jobPosting = new JobPosting();
		jobPosting.setJob_title(request.getJob_title());
		jobPosting.setJob_description(request.getJob_description());
		jobPosting.setCompanyName(request.getCompanyName());
		jobPosting.setStartDate(request.getStartDate());
		jobPosting.setEndDate(request.getEndDate());
		jobPosting.setJob_location(request.getJob_location());
		jobPosting.setEligibilityCriteria(request.getEligibilityCriteria());
		jobPosting.setCompany_url(request.getCompany_url());
		jobPosting.setCtc(request.getCtc());
		jobPosting.setActive(LocalDateTime.now().isBefore(request.getEndDate())); // Auto-calculate status
		jobPosting.setPostedBy(user);
		JobPosting savedJob = jobPostingRepository.save(jobPosting);

		//jobNotificationSMS(savedJob);
		jobNotificationMail(savedJob);

		return mapToResponse(savedJob);
	}
	


	// Get all active job postings
	public List<JobPostingResponse> getAllActiveJobs() {
		List<JobPosting> activeJobs = jobPostingRepository.findByIsActiveTrue();
		return activeJobs.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	// Get all job postings by a specific user
	public List<JobPostingResponse> getJobsPostedByUser(Long userId) {
		List<JobPosting> jobs = jobPostingRepository.findByPostedById(userId);
		return jobs.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	// Mark a job as inactive
	public void markJobAsInactive(Long jobId) {
		JobPosting jobPosting = jobPostingRepository.findById(jobId)
				.orElseThrow(() -> new IllegalArgumentException("Job not found"));

		jobPosting.setActive(false);
		jobPostingRepository.save(jobPosting);
	}

	// Utility method to map entity to response DTO
	private JobPostingResponse mapToResponse(JobPosting job) {
		JobPostingResponse response = new JobPostingResponse();
		response.setId(job.getId());
		response.setJob_title(job.getJob_title());
		response.setJob_description(job.getJob_description());
		response.setCompanyName(job.getCompanyName());
		response.setStartDate(job.getStartDate());
		response.setEndDate(job.getEndDate());
		response.setJob_location(job.getJob_location());
		response.setEligibilityCriteria(job.getEligibilityCriteria());
		response.setCompany_url(job.getCompany_url());
		response.setCtc(job.getCtc());
		response.setActive(job.isActive());
		response.setPostedByName(job.getPostedBy().getName());
		return response;
	}

	public List<JobPostingResponse> getAllJobs() {
		List<JobPosting> jobs = jobPostingRepository.findAll();
		return jobs.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	public JobPostingResponse getJobById(Long jobId) {
		JobPosting job = jobPostingRepository.findById(jobId)
				.orElseThrow(() -> new IllegalArgumentException("Job not found"));
		return mapToResponse(job);
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
	

	public JobPostingResponse updateJob(Long jobId, Long userId, JobPostingRequest request) {
		JobPosting job = jobPostingRepository.findById(jobId)
				.orElseThrow(() -> new IllegalArgumentException("Job not found"));

		job.setJob_title(request.getJob_title());
		job.setJob_description(request.getJob_description());
		job.setCompanyName(request.getCompanyName());
		job.setStartDate(request.getStartDate());
		job.setEndDate(request.getEndDate());
		job.setJob_location(request.getJob_location());
		job.setEligibilityCriteria(request.getEligibilityCriteria());
		job.setCompany_url(request.getCompany_url());
		job.setCtc(request.getCtc());
		job.setActive(request.isActive());
		job.setPostedBy(userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")));
		JobPosting updatedJob = jobPostingRepository.save(job);
		
		jobUpdatedNotificationMail(updatedJob);
		
		return mapToResponse(updatedJob);
		
		
	}
}

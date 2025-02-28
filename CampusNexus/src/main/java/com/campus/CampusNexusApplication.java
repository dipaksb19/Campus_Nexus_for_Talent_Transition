package com.campus;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.campus.config.TwilioConfig;
import com.twilio.Twilio;

@SpringBootApplication
public class CampusNexusApplication {
	
	@Autowired
	private TwilioConfig twilioConfig;
	
	@PostConstruct  // it will act as a init method
	public void initTwilio() {
		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
	}

	public static void main(String[] args) {
		SpringApplication.run(CampusNexusApplication.class, args);
		System.out.println("---Application Started---");
	}

}

package com.campus.services;

import com.campus.entity.User;
import com.campus.enums.UserRoles;
import com.campus.model.LoginRequest;
import com.campus.model.UserResponse;
import com.campus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public ResponseEntity<?> userAuthentication(LoginRequest loginRequest) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(loginRequest.getEmail()));
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
        User user = optionalUser.get();
        boolean isPasswordMatch = user.getPassword().equals(loginRequest.getPassword());
        if(!isPasswordMatch) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid password");
        }
        boolean isVarified = user.isVarified();
        if(!isVarified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not verified");
        }
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isVarified()));
    }


    // Ensure there is always one default admin
    @PostConstruct
    public void createDefaultAdmin() {
        if (userRepository.findByRole(UserRoles.ADMIN).isEmpty()) {
            User admin = new User();
            admin.setId(null);
            admin.setName("Admin");
            admin.setEmail("admin@campus.com");
            admin.setPassword("admin@123"); // This should be hashed in production
            admin.setRole(UserRoles.ADMIN);
            admin.setVarified(true); // Admins are always verified
            userRepository.save(admin);
        	
        }
    }

    
    
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
    
    
    
    
    
    // Register a coordinator
    public UserResponse registerCoordinator(User coordinator) {
        coordinator.setRole(UserRoles.COORDINATOR);
        coordinator.setVarified(false); // Default to not verified
        User user = userRepository.save(coordinator);
        userAccountCreatedMail(user);
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isVarified());
    }
    
    
    
 // Coordinator Account created mail
    public void userVerificationMail(User user) {
    	String subject = "Verification Status is (Verified)";
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
    

    // Verify coordinator
    public UserResponse verifyCoordinator(Long id) {
        Optional<User> optionalCoordinator = userRepository.findById(id);
        User user = new User();
        if (optionalCoordinator.isPresent()) {
            User coordinator = optionalCoordinator.get();
            if (coordinator.getRole() == UserRoles.COORDINATOR) {
                coordinator.setVarified(true);
                user = userRepository.save(coordinator);
                userVerificationMail(user);
                return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isVarified());
            } else {
                throw new IllegalArgumentException("User is not a coordinator");
            }
        }
        throw new IllegalArgumentException("Coordinator not found");
    }

    // Update user (admin or coordinator)
    public UserResponse updateUser(Long id, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            User userRes = userRepository.save(user);
            return new UserResponse(userRes.getId(), userRes.getName(), userRes.getEmail(), userRes.getRole(), userRes.isVarified());
        }
        throw new IllegalArgumentException("User not found");
    }

    // Delete coordinator
    public void deleteCoordinator(Long id) {
        Optional<User> optionalCoordinator = userRepository.findById(id);
        if (optionalCoordinator.isPresent() && optionalCoordinator.get().getRole() == UserRoles.COORDINATOR) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Coordinator not found or is not a coordinator");
        }
    }

    // Get all coordinators
    public List<UserResponse> getAllCoordinators() {
        List<User> coordinators = userRepository.findByRole(UserRoles.COORDINATOR);
        return coordinators.stream().map(coordinator -> new UserResponse(coordinator.getId(), coordinator.getName(), coordinator.getEmail(), coordinator.getRole(), coordinator.isVarified())).toList();
    }
}

package com.example.demo.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpService 
{

	 private Map<String, String> otpStorage = new HashMap<>();
	 
	 @Autowired
	 private JavaMailSender javaMailSender;

	    public String generateOtp() {
	        Random rand = new Random();
	        int otp = rand.nextInt(9999);
	        return String.format("%04d", otp);
	    }

	    public void saveOtp(String email, String otp) {
	        otpStorage.put(email, otp);
	    }

	    public boolean verifyOtp(String email, String otp) {
	        String storedOtp = otpStorage.get(email);
	        return storedOtp != null && storedOtp.equals(otp);
	    }
	    
	    public String sendOtp(String email, String otp) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom("your-email@example.com");
	        message.setTo(email);
	        message.setSubject("Your OTP Code");
	        message.setText("Your OTP code is: " + otp);

	        try {
	            javaMailSender.send(message);
	            return "Success";
	        } catch (Exception e) {
	            return "Error";
	        }
	    }
	    
	    public void saveOtpForVerification(String email, String otp) {
	        otpStorage.put(email, otp); 
	    }

	    public String getOtpFromMemoryOrDb(String email) {
	        return otpStorage.get(email);  
	    }
	    
}

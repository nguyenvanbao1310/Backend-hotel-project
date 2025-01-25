package com.example.demo.Controller;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.User;
import com.example.demo.Security.PasswordEncoder;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.IUserService;
import com.example.demo.Service.OtpService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private IUserService iUserSv;
	
	@Autowired
	private OtpService otpService;
	
	@Autowired
	private EmailService emailService;
	
	
	@GetMapping("/")
	public String test()
	{
		return "hello world";
	}
	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user)
	{
		
		if (iUserSv.existsByEmail(user.getEmail())) {
	        return new ResponseEntity<>("Email đã tồn tại!", HttpStatus.BAD_REQUEST);
	    }
		
	    String otp = otpService.generateOtp();
	    
	    String emailResponse = otpService.sendOtp(user.getEmail(), otp);
	    
	    if (emailResponse.equals("Success")) {
            otpService.saveOtpForVerification(user.getEmail(), otp);

            return new ResponseEntity<>("OTP đã được gửi qua email. Vui lòng nhập mã OTP để hoàn tất đăng ký.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Lỗi khi gửi OTP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@PostMapping("/verifyOtp")
	public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
	    String email = request.get("email");
	    String otp = request.get("otp");

	    String storedOtp = otpService.getOtpFromMemoryOrDb(email);
	    
	    if (storedOtp != null && storedOtp.equals(otp)) {
	        User user = new User();
	        user.setEmail(email);
	        user.setUserName(request.get("userName"));
	        user.setPhone(request.get("phone"));
	        user.setPassWord(request.get("passWord"));
	        iUserSv.addUser(user);

	        return new ResponseEntity<>("Đăng ký thành công!", HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>("Mã OTP không chính xác.", HttpStatus.BAD_REQUEST);
	    }
	}
	
	@PostMapping("/forgot-password")
    public ResponseEntity<?> sendOtpForPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email không được để trống");
        }

        Optional<User> userOptional = iUserSv.findByEmail(email);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email không tồn tại");
        }

        String otp = String.format("%04d", new Random().nextInt(9999));

        otpService.saveOtp(email, otp);

        boolean isEmailSent = emailService.sendEmail(email, "Mã OTP khôi phục mật khẩu", "Mã OTP của bạn là: " + otp);
        if (!isEmailSent) {
            return new ResponseEntity<>("Không thể gửi email. Vui lòng thử lại sau.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok("Mã OTP đã được gửi tới email của bạn");
    }
	
	@PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Email, OTP và mật khẩu mới không được để trống");
        }

        String storedOtp = otpService.getOtpFromMemoryOrDb(email);

        if (storedOtp != null && storedOtp.equals(otp)) {
            Optional<User> userOptional = iUserSv.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
    			String hashedPassword = PasswordEncoder.hashPassword(user.getPassWord());
                user.setPassWord(hashedPassword);
                iUserSv.updateUser(user);

                return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
            }
        } else {
            return new ResponseEntity<>("Mã OTP không chính xác hoặc đã hết hạn.", HttpStatus.BAD_REQUEST);
        }
    }
}
        


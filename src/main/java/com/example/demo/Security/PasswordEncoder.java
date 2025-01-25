package com.example.demo.Security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder
{
	 public static String hashPassword(String password) 
	 {
	        try {
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

	            StringBuilder hexString = new StringBuilder();
	            for (byte b : encodedHash) {
	                String hex = Integer.toHexString(0xff & b);
	                if (hex.length() == 1) hexString.append('0');
	                hexString.append(hex);
	            }
	            return hexString.toString();
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException("Error while hashing password", e);
	        }
	 }
	 // Hàm kiểm tra mật khẩu
	 public static boolean checkPassword(String plainPassword, String hashedPassword) {
	        String hashedInputPassword = hashPassword(plainPassword);
	        return hashedInputPassword.equals(hashedPassword);
	    }
}

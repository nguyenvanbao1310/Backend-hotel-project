package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import com.example.demo.Model.User;

public interface IUserService {
	
	public User addUser(User user);

   	public User updateUser(User user);
   	
   	public boolean existsByEmail(String email);

    public boolean deleteUser(String id);
   
    Optional<User> findByEmail(String email);

    Optional<User> getUserById(String id);

    List<User> getAllUsers();

	String generateUserId();

}

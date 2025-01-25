package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Security.PasswordEncoder;

@Service
public class UserServiceImpl implements IUserService 
{
	@Autowired
	private UserRepository userRp;
	
	
	public boolean existsByEmail(String email) 
	{
	        return userRp.existsByEmail(email);
	}
	 
	@Override
    public Optional<User> findByEmail(String email) {
        return userRp.findByEmail(email);
    }

	@Override
	public User addUser(User user) 
	{
		
		if(user!=null)
		{
			String newUserId = generateUserId();
	        user.setId(newUserId);
			String hashedPassword = PasswordEncoder.hashPassword(user.getPassWord());
	        user.setPassWord(hashedPassword);
			return userRp.save(user);
		}
		return null;
	}
	
	@Override
	public String generateUserId()
	{
		int lastUserId = userRp.findLastUserId();
        int newUserId = lastUserId + 1;
        return String.format("G%03d", newUserId);
	}

	@Override
	public User updateUser(User user) 
	{
		Optional<User> existingUserOpt = userRp.findById(user.getId());

	    if (existingUserOpt.isPresent()) {
	        User existingUser = existingUserOpt.get();

	        existingUser.setUserName(user.getUserName() != null ? user.getUserName() : existingUser.getUserName());
	        existingUser.setEmail(user.getEmail() != null ? user.getEmail() : existingUser.getEmail());
	        existingUser.setPhone(user.getPhone() != null ? user.getPhone() : existingUser.getPhone());

	        return userRp.save(existingUser);
	    } else {
	        return null;
	    }
	}

	@Override
	public boolean deleteUser(String id) {
		return false;
	}

	@Override
	public Optional<User> getUserById(String id) {
		return Optional.empty();
	}

	@Override
	public List<User> getAllUsers() {
		return null;
	}

}

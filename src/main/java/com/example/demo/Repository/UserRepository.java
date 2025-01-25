package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.User;

@Repository
public interface UserRepository extends JpaRepository<User,String>{
	
	@Query("SELECT MAX(CAST(SUBSTRING(u.id, 2) AS INTEGER)) FROM User u")
	int findLastUserId();
	
    boolean existsByEmail(String email);
    
    Optional<User> findByEmail(String email);
}

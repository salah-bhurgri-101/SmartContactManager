package com.spring.dao;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spring.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


//	 @Query("select u from User u where u.email = :email")
//	 public User getUserByUserName(@Param("email") String email);
	
	public User findByEmail(String email);
	 
//	public Page<User> findAll(Pageable pageable);

	@Query("select u from User u where u.role = :role")
	public Page<User> findByUser(String role  , Pageable pageable);
}

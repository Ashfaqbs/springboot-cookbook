package com.ashfaq.sample.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ashfaq.sample.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}

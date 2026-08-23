/**
 * Code developed by Ashfaq (© [Year])
 * GitHub: https://github.com/DarkSharkAsh
 */

package com.dev.Jasypt.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.Jasypt.model.*;


public interface CycleDAO extends JpaRepository<Cycle, Integer> {

	
	List<Cycle> findByCyclebrand(String brand);

}

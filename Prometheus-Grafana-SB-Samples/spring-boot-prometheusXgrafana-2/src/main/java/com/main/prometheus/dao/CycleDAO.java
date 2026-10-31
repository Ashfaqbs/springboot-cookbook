/**
 * Code developed by Ashfaq (© [Year])
 * GitHub: https://github.com/DarkSharkAsh
 */

package com.main.prometheus.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.prometheus.model.Cycle;

public interface CycleDAO extends JpaRepository<Cycle, Integer> {

	
	List<Cycle> findByCyclebrand(String brand);

}

/**
 * Code developed by Ashfaq (© [Year])
 * GitHub: https://github.com/DarkSharkAsh
 */

package com.main.prometheus.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PrometheusCycle")
public class Cycle {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	String cycle_name;

	@Column(name = "cycle_brand")
	String cyclebrand;
//cycle_brand this was the feild but we were unable to create the custom finder methods so we made the column name and changes the feild name and made the method name as 
	// List<Cycle> findByCyclebrand(String brand); in DAO 

	double cycle_Cost;

	public Cycle() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Cycle(int id, String cycle_name, String cycle_brand, double cycle_Cost) {
		super();
		this.id = id;
		this.cycle_name = cycle_name;
		this.cyclebrand = cycle_brand;
		this.cycle_Cost = cycle_Cost;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCycle_name() {
		return cycle_name;
	}

	public void setCycle_name(String cycle_name) {
		this.cycle_name = cycle_name;
	}

	public String getCycle_brand() {
		return cyclebrand;
	}

	public void setCycle_brand(String cycle_brand) {
		this.cyclebrand = cycle_brand;
	}

	public double getCycle_Cost() {
		return cycle_Cost;
	}

	public void setCycle_Cost(double cycle_Cost) {
		this.cycle_Cost = cycle_Cost;
	}

	@Override
	public String toString() {
		return "Cycle [id=" + id + ", cycle_name=" + cycle_name + ", cycle_brand=" + cyclebrand + ", cycle_Cost="
				+ cycle_Cost + "]";
	}

}

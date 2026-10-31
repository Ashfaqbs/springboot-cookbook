package com.ashfaq.example.lifecyclemethods.eg1;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {

	@Id
	private Long id;
	private String name;
	private Double price;

}

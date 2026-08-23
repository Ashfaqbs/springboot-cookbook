/**
 * Code developed by Ashfaq (© [Year])
 * GitHub: https://github.com/DarkSharkAsh
 */

package com.main.springboot.batch.config;

import org.springframework.batch.item.ItemProcessor;

import com.main.springboot.batch.model.Product;

public class CustomItemProcessor implements ItemProcessor<Product, Product> {

	@Override
	public Product process(Product item) throws Exception {

		try {
			int discountPer = Integer.parseInt(item.getDiscount().trim());

			double originalPrice = Double.parseDouble(item.getPrice().trim());

			double discount = (discountPer / 100) * originalPrice;

			double finalPrice = originalPrice - discount;

			item.setDiscountedPrice(String.valueOf(finalPrice));

		} catch (Exception exception) {
			// TODO: handle exception
			exception.printStackTrace();
		}

		return item;
	}

}

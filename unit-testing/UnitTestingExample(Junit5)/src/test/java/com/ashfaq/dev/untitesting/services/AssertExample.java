package com.ashfaq.dev.untitesting.services;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssertExample {
//assertion means validating actual result with expected result
	
	//test case
	@Test
	public void addTest() {
		System.out.println("Test for add method");
		int sum = CalculatorServices.add(12, 45);
		int expected = 57;
		Assertions.assertEquals(expected, sum);//this is used for equality check 
		Assertions.assertNotEquals(expected, sum);//this is used for non equality check
		Assertions.assertTrue(sum > 0);//this is used for true check
		Assertions.assertFalse(sum > 0);//this is used for false check
		Assertions.assertSame(expected, sum);//this is used for same check i.e to check if both the "Objects" are same not content here 
		Assertions.assertNotSame(expected, sum);//this is used for not same check
		Assertions.assertNull(sum);//this is used for null check
		Assertions.assertNotNull(sum);//this is used for not null check
		Assertions.fail();//this is used for fail check
		Assertions.assertThrows(ArithmeticException.class, () -> CalculatorServices.add(12, 0));//this is used for exception check
		Assertions.assertArrayEquals(new int[] {12, 45, 67}, new int[] {12, 45, 67});//this is used for array check
		Assertions.assertIterableEquals(Arrays.asList(12, 45, 67), Arrays.asList(12, 45, 67));//this is used for iterable  list check
		
		
		
	}
	
}

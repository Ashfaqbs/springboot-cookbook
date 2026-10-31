package com.ashfaq.outsidepackage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SampleRunner implements CommandLineRunner {
	@Override
	public void run(String... args) throws Exception {
//		log.info("Printing from outside package");
	}
// OP
//	2024-10-16T20:43:31.482+05:30[0;39m [32m INFO[0;39m [35m6472[0;39m [2m---[0;39m [2m[Spring-Ref] [  restartedMain][0;39m [2m[0;39m[36mcom.ashfaq.outsidepackage.SampleRunner  [0;39m [2m:[0;39m Printing from outside package

}

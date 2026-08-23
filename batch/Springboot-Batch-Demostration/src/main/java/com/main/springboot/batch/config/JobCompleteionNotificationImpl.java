/**
 * Code developed by Ashfaq (© [Year])
 * GitHub: https://github.com/DarkSharkAsh
 */

package com.main.springboot.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobCompleteionNotificationImpl implements JobExecutionListener {

	private static Logger LOGGER = LoggerFactory.getLogger(JobCompleteionNotificationImpl.class);

	@Override
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub

		LOGGER.info("Job started");

	}

	@Override
	public void afterJob(JobExecution jobExecution) {

		if (jobExecution.getStatus()==BatchStatus.COMPLETED) {
			LOGGER.info("Job completed");
			
		} else {
			LOGGER.info("Job is running");
		}

	}

}

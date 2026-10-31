package com.ashfaq.dev.scheduler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ashfaq.dev.constants.MainConstants;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class DrinkWaterReminder {
//	@Scheduled(fixedRate = 60000) // Run every 1 minute
//	public void sample() {
//		System.out.println("Running samples");
//	}

	private static final List<String> WATER_MESSAGES = Arrays.asList(
			"🚰 Time to drink water! 💦\n\nDid you know? Water is essential for our health. It helps in maintaining our body's hydration levels, supports digestion, and keeps our skin healthy. Stay hydrated and healthy! 😊",
			"🚰 It's time to hydrate! Water is key to our well-being. It aids in digestion, keeps our skin moist, and helps our bodies function properly. Don't forget to drink up! 😊",
			"🚰 Remember to stay hydrated! Water is crucial for our health, supporting digestion, maintaining skin health, and ensuring our bodies work efficiently. Let's keep our bodies in top shape! 😊",
			"🚰 Time for a water break! Water is vital for our health, supporting digestion, keeping our skin healthy, and ensuring our bodies function optimally. Let's keep hydrated and healthy together! 😊");

	private static final Random RANDOM = new Random();

	public static String getRandomWaterMessage() {
		return WATER_MESSAGES.get(RANDOM.nextInt(WATER_MESSAGES.size()));
	}

//	@Scheduled(fixedRate = 1500000) // 25 minutes in milliseconds
//	public void sendDrinkWaterReminder() {
//		OkHttpClient client = new OkHttpClient();
//		String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage?chat_id=" + GROUP_CHAT_ID + "&text="
//				+ getRandomWaterMessage();
//
//		Request request = new Request.Builder().url(url).build();
//
//		client.newCall(request).enqueue(new Callback() {
//			@Override
//			public void onFailure(Call call, IOException e) {
//				e.printStackTrace();
//			}
//
//			@Override
//			public void onResponse(Call call, Response response) throws IOException {
//				if (response.isSuccessful()) {
//					System.out.println("Message sent successfully");
//				} else {
//					System.out.println("Failed to send message: " + response.message());
//				}
//			}
//		});
//
//		System.out.println("Drink water reminder sent");
//	}

	// Retry Mechanism

//	@Scheduled(fixedRate = 1500000) // 25 minutes in milliseconds
//	@Scheduled(cron = "0,25,50 8-19 * * *", zone = "Asia/Kolkata") // 8AM - 8PM IST
	@Scheduled(cron = "0 0,25,50 8-19 * * *", zone = "Asia/Kolkata")
	public void sendDrinkWaterReminder() {
	    System.out.println("Drink water reminder started at: " + new java.util.Date());

		OkHttpClient client = new OkHttpClient();
		String url = "https://api.telegram.org/bot" + MainConstants.BOT_TOKEN + "/sendMessage?chat_id="
				+ MainConstants.GROUP_CHAT_ID + "&text=" + getRandomWaterMessage();

		Request request = new Request.Builder().url(url).build();

		// Use an array to hold the retry count
		final int[] retryCount = { 0 };
		final int maxRetries = 3;

		while (retryCount[0] < maxRetries) {
			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					e.printStackTrace();
					// Increment the retry count and log the failure
					retryCount[0]++;
					System.out.println("Failed to send message. Retry attempt: " + retryCount[0]);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {

					try {
						if (response.isSuccessful()) {
							System.out.println("Message sent successfully");
							// Reset the retry count on success
							retryCount[0] = 0;
						} else {
							System.err.println("Failed to send message. HTTP status: " + response.code() + " - "
									+ response.message());
							// Increment the retry count on failure
							retryCount[0]++;
						}
					} finally {
						response.close(); // Ensure the response is closed to avoid leaking resources
					}

				}
			});

			// Wait for a short period before retrying to avoid overwhelming the server or
			// hitting rate limits
			try {
				Thread.sleep(5000); // Wait for 5 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Break the loop if the message was sent successfully
			if (retryCount[0] == 0) {
				break;
			}
		}

	    System.out.println("Drink water reminder sent at: " + new java.util.Date());
	}

}
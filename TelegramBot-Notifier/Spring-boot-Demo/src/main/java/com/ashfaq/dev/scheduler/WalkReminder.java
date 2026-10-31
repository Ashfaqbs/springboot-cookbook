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
public class WalkReminder {

	private static final List<String> WALK_MESSAGES = Arrays.asList(
			"👟 Time for a quick walk! Stretch those legs and get your blood flowing! 💪",
			"👟 Stand up and take a short walk! Remember, a little movement goes a long way for your health. 💪",
			"👟 Let's keep our bodies active! Take a brief walk and enjoy a moment of mobility. 💪",
			"👟 Quick walking break! A few steps now can boost your mood and productivity. 💪");

	private static final Random RANDOM = new Random();

	private static String getRandomWalkMessage() {
		return WALK_MESSAGES.get(RANDOM.nextInt(WALK_MESSAGES.size()));
	}

//	@Scheduled(fixedRate = 2700000) // 45 minutes in milliseconds
//	public void sendWalkReminder() {
//		OkHttpClient client = new OkHttpClient();
//		String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage?chat_id=" + GROUP_CHAT_ID + "&text="
//				+ getRandomWalkMessage();
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
//					System.out.println("Walk reminder sent successfully");
//				} else {
//					System.out.println("Failed to send walk reminder: " + response.message());
//				}
//			}
//		});
//
//		System.out.println("Walk reminder scheduled");
//	}

//	Retry 

//	@Scheduled(fixedRate = 2700000) // 45 minutes in milliseconds
	@Scheduled(cron = "0 0/45 9-19 * * *", zone = "Asia/Kolkata") // Between 9 AM and 7 PM

	public void sendWalkReminder() {
		OkHttpClient client = new OkHttpClient();
		String url = "https://api.telegram.org/bot" + MainConstants.BOT_TOKEN + "/sendMessage?chat_id=" + MainConstants.GROUP_CHAT_ID + "&text="
				+ getRandomWalkMessage();

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
					System.out.println("Failed to send walk reminder. Retry attempt: " + retryCount[0]);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {

					try {
						if (response.isSuccessful()) {
							System.out.println("Walk reminder sent successfully");
							// Reset the retry count on success
							retryCount[0] = 0;
						} else {
							System.out.println("Failed to send walk reminder: " + response.message());
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

		System.out.println("Walk reminder scheduled");
	}

}

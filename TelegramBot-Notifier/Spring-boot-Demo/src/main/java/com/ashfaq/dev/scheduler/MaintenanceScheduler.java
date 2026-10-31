package com.ashfaq.dev.scheduler;

import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ashfaq.dev.constants.MainConstants;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class MaintenanceScheduler {

	// Bike and Car Emission Due Reminder (Every 6 Months)
	@Scheduled(cron = "0 0 9 1 1,7 *", zone = "Asia/Kolkata")
	public void emissionDueReminder() {
		OkHttpClient client = new OkHttpClient();
		String url = "https://api.telegram.org/bot" + MainConstants.BOT_TOKEN + "/sendMessage?chat_id="
				+ MainConstants.GROUP_CHAT_ID + "&text=" + "Reminder: Time to check bike and car emissions!";

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
							System.out.println("Emission Message sent successfully");
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

		System.out.println("Emission reminder sent");
	}

	// Weekly Water Fill Reminder (Every Week)
	@Scheduled(cron = "0 0 9 * * MON", zone = "Asia/Kolkata")
	public void weeklyWaterFillReminder() {
		OkHttpClient client = new OkHttpClient();
		String url = "https://api.telegram.org/bot" + MainConstants.BOT_TOKEN + "/sendMessage?chat_id="
				+ MainConstants.GROUP_CHAT_ID + "&text=" + "Reminder: Time to fill the water tank!";

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
							System.out.println("Weekly Water Fill Reminder sent successfully");
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

		System.out.println("Weekly Water Fill reminder sent");
	}

	// Monthly Petrol Fill Reminders for Different Vehicles
	@Scheduled(cron = "0 0 10 1 * *", zone = "Asia/Kolkata")
	public void petrolFillVehicleReminder() {
		OkHttpClient client = new OkHttpClient();
		// Messages for each vehicle
		String messagePassionXpro = "Reminder: Time to fill petrol in Passion Xpro!";
		String messageYamahaR15 = "Reminder: Time to fill petrol in Yamaha R15!";
		String messageSwiftCar = "Reminder: Time to fill petrol in Swift Car!";

		// Combine messages
		String combinedMessage = messagePassionXpro + "\n" + messageYamahaR15 + "\n" + messageSwiftCar;

		// Build the URL with the combined message
		String url = "https://api.telegram.org/bot" + MainConstants.BOT_TOKEN + "/sendMessage?chat_id="
				+ MainConstants.GROUP_CHAT_ID + "&text=" + combinedMessage;

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
							System.out.println("Monthly Petrol Fill Reminders sent successfully");
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

		System.out.println("Monthly Petrol Fill reminder sent");
	}
}

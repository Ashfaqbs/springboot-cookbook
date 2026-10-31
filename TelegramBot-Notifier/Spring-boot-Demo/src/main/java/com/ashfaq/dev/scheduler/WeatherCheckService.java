package com.ashfaq.dev.scheduler;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ashfaq.dev.constants.MainConstants;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class WeatherCheckService {

	// Map to store weather conditions and their corresponding concerns
	private static final Map<String, List<String>> weatherConcernsMap = new HashMap<>();

	static {
		// Populate the map with weather conditions and concerns
		weatherConcernsMap.put("sunny", Arrays.asList("Remember to stay hydrated and protect your skin.",
				"Wear sunscreen to avoid sunburn.", "Avoid prolonged exposure to direct sunlight."));
		weatherConcernsMap.put("rainy", Arrays.asList("Don't forget to carry an umbrella or raincoat.",
				"Be cautious while driving on wet roads.", "Keep electronic devices protected from water damage."));
		weatherConcernsMap.put("cloudy", Arrays.asList(

				"Possibility of rain showers, so carry an umbrella.", "Expect cooler temperatures with cloud cover.",
				"Be prepared for changing weather conditions."

				)

				);
		weatherConcernsMap.put("snow", Arrays.asList("Be cautious while walking on icy surfaces.",
				"Dress warmly in layers to stay insulated.", "Check road conditions before traveling."));
		// Add more weather conditions and concerns as needed
	}

	// @Scheduled(cron = "0 * * * * *")

	@Scheduled(cron = "0 0 8,17 * * *")
	public void fetchBangloreWeatherData() {
		OkHttpClient client = new OkHttpClient();

		// Replace the API endpoint and query parameters with your actual values
		String apiUrl = "https://api.weatherapi.com/v1/current.json?key=77f0a703116744919e9163402241105&q=Bangalore";

		Request request = new Request.Builder().url(apiUrl).build();

		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				String responseData = response.body().string();
				JSONObject jsonObject = new JSONObject(responseData);

				// Extract relevant weather information
				JSONObject location = jsonObject.getJSONObject("location");
				String cityName = location.getString("name");
				String currentTime = location.getString("localtime");
				JSONObject current = jsonObject.getJSONObject("current");
				String weatherCondition = current.getJSONObject("condition").getString("text");
				double windSpeed = current.getDouble("wind_kph");
				double temperature = current.getDouble("temp_c");

				// Generate dynamic concerns based on weather conditions
				String concerns = generateConcerns(weatherCondition);

				// Print important data along with concerns
				// System.out.println("City: " + cityName);
				// System.out.println("Current Time: " + currentTime);
				// System.out.println("Weather Condition: " + weatherCondition);
				// System.out.println("Wind Speed: " + windSpeed + " km/h");
				// System.out.println("Temperature: " + temperature + "°C");
				// System.out.println("Concerns: " + concerns);

				String finalMessage = "In " + cityName + " at " + currentTime + ", the weather is " + weatherCondition
						+ ". " + "The wind speed is " + windSpeed + " km/h, and the temperature is " + temperature
						+ "°C. " + "Concerns: " + concerns;

				System.out.println("weather reminder started at: " + new java.util.Date());

				OkHttpClient teleClient = new OkHttpClient();
				String url = "https://api.telegram.org/bot" + MainConstants.BOT_TOKEN + "/sendMessage?chat_id="
						+ MainConstants.GROUP_CHAT_ID + "&text=" + finalMessage;

				Request teleRequest = new Request.Builder().url(url).build();

				// Use an array to hold the retry count
				final int[] retryCount = { 0 };
				final int maxRetries = 3;

				while (retryCount[0] < maxRetries) {
					teleClient.newCall(teleRequest).enqueue(new Callback() {
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
									System.out.println("Weather Message sent successfully");
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

			} else {
				System.err.println("Failed to fetch weather data. HTTP status code: " + response.code());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Scheduled(cron = "0 * * * * *")
	@Scheduled(cron = "0 0 8,17 * * *")
	public void fetchHyderabadWeatherData() {
		OkHttpClient client = new OkHttpClient();

		// Replace the API endpoint and query parameters with your actual values
		String apiUrl = "https://api.weatherapi.com/v1/current.json?key=77f0a703116744919e9163402241105&q=hyderabad";

		Request request = new Request.Builder().url(apiUrl).build();

		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				String responseData = response.body().string();
				JSONObject jsonObject = new JSONObject(responseData);

				// Extract relevant weather information
				JSONObject location = jsonObject.getJSONObject("location");
				String cityName = location.getString("name");
				String currentTime = location.getString("localtime");
				JSONObject current = jsonObject.getJSONObject("current");
				String weatherCondition = current.getJSONObject("condition").getString("text");
				double windSpeed = current.getDouble("wind_kph");
				double temperature = current.getDouble("temp_c");

				// Generate dynamic concerns based on weather conditions
				String concerns = generateConcerns(weatherCondition);

				// Print important data along with concerns
				// System.out.println("City: " + cityName);
				// System.out.println("Current Time: " + currentTime);
				// System.out.println("Weather Condition: " + weatherCondition);
				// System.out.println("Wind Speed: " + windSpeed + " km/h");
				// System.out.println("Temperature: " + temperature + "°C");
				// System.out.println("Concerns: " + concerns);

				String finalMessage = "In " + cityName + " at " + currentTime + ", the weather is " + weatherCondition
						+ ". " + "The wind speed is " + windSpeed + " km/h, and the temperature is " + temperature
						+ "°C. " + "Concerns: " + concerns;

				System.out.println("weather reminder started at: " + new java.util.Date());

				OkHttpClient teleClient = new OkHttpClient();
				String url = "https://api.telegram.org/bot" + MainConstants.BOT_TOKEN + "/sendMessage?chat_id="
						+ MainConstants.GROUP_CHAT_ID + "&text=" + finalMessage;

				Request teleRequest = new Request.Builder().url(url).build();

				// Use an array to hold the retry count
				final int[] retryCount = { 0 };
				final int maxRetries = 3;

				while (retryCount[0] < maxRetries) {
					teleClient.newCall(teleRequest).enqueue(new Callback() {
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
									System.out.println("Weather Message sent successfully");
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

			} else {
				System.err.println("Failed to fetch weather data. HTTP status code: " + response.code());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String generateConcerns(String weatherCondition) {
		// Initialize an empty string to store the concerns
		String concerns = "";

		// Check if the weather condition matches any key in the map
		for (String condition : weatherConcernsMap.keySet()) {
			// Check if the weather condition string contains the condition key
			if (weatherCondition.toLowerCase().contains(condition.toLowerCase())) {
				// Retrieve the list of concerns for the matched condition
				List<String> concernsList = weatherConcernsMap.get(condition);

				// Generate a random index to select a concern from the list
				int randomIndex = new Random().nextInt(concernsList.size());

				// Retrieve a random concern from the list
				concerns = concernsList.get(randomIndex);

				// Break out of the loop since we found a match
				break;
			}
		}

		// If no match was found, provide a generic message
		if (concerns.isEmpty()) {
			concerns = "Weather condition is " + weatherCondition + ". Be prepared for any changes.";
		}

		// Return the generated concerns
		return concerns;
	}

}

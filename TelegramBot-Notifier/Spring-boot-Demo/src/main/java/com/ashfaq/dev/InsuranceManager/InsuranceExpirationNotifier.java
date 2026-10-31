
package com.ashfaq.dev.InsuranceManager;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ashfaq.dev.constants.MainConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class InsuranceExpirationNotifier {

	public List<Insurance> insuranceList;

	@Autowired
	private ObjectMapper objectMapper;

	public void loadInsuranceData() {
		try {
			ClassPathResource resource = new ClassPathResource("insurance_data.json");
			InputStream inputStream = resource.getInputStream();
			CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class,
					Insurance.class);
			insuranceList = objectMapper.readValue(inputStream, listType);

			System.out.println("JSON data loaded successfully." + "  : " + insuranceList.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Scheduled(fixedRate = 86400000) // Run once per day (86400000 milliseconds = 24 hours)
	public void notifyAboutExpiringInsurances() {
		if (insuranceList != null && !insuranceList.isEmpty()) {
			// Get the list of insurances expiring in the next 15 days
			LocalDate today = LocalDate.now();
			LocalDate ftenDaysLater = today.plusDays(15);

			// Filter insurances that expire within the next 15 days
			List<Insurance> expiringInsurances = insuranceList.stream()
					.filter(insurance -> insurance.getExpiryDate() != null && insurance.getExpiryDate().isAfter(today)
							&& insurance.getExpiryDate().isBefore(ftenDaysLater))
					.toList();

			System.out.println("In notifyAboutExpiringInsurances and these are the list of expiring insurance : "
					+ expiringInsurances.toString());

			// Iterate over the filtered insurances and send reminders
			for (Insurance insurance : expiringInsurances) {
				System.out.println("Sending Expiry Notifications");
				sendTelegramMessage("Reminder: Your insurance for " + insurance.getHolderName() + " is expiring on "
						+ insurance.getExpiryDate() + "!");
			}

		} else {

			System.out.println(
					"Unable to notify the Expiring Insurances , Might be the list is not loaded yet  " + insuranceList);
		}
	}

	private void sendTelegramMessage(String message) {
		// Configure OkHttpClient to send HTTP request to Telegram API
		OkHttpClient client = new OkHttpClient();
		String url = "https://api.telegram.org/bot" + MainConstants.BOT_TOKEN + "/sendMessage?chat_id="
				+ MainConstants.GROUP_CHAT_ID + "&text=" + message;
		Request request = new Request.Builder().url(url).build();

		// Send the HTTP request asynchronously
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					throw new IOException("Unexpected response code: " + response);
				}
			}
		});
	}
}

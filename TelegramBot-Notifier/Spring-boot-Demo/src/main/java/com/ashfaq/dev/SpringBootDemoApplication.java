package com.ashfaq.dev;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

import com.ashfaq.dev.InsuranceManager.InsuranceExpirationNotifier;
import com.ashfaq.dev.constants.MainConstants;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SpringBootApplication
@RestController
@EnableScheduling
public class SpringBootDemoApplication implements CommandLineRunner {
	// String url =
	// https://newsapi.org/v2/everything?q=Bangalore&date=2024-05-08&apiKey=d209d16fcee3418ab2a7b6d6cb184e5e
	// NEWS API
	public static void main(String[] args) {

		SpringApplication.run(SpringBootDemoApplication.class, args);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			// Code to be executed when the application is shutting down
			sendShutdownNotification();

			try {
				Thread.sleep(950);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Shutting down gracefully...");

		}));

	}

	private static void sendShutdownNotification() {

		OkHttpClient client = new OkHttpClient();
		String url = "https://api.telegram.org/bot" + MainConstants.BOT_TOKEN + "/sendMessage?chat_id="
				+ MainConstants.GROUP_CHAT_ID + "&text=" + "Hey Ashfaq Application is shutting down...";

		Request request = new Request.Builder().url(url).build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response.isSuccessful()) {
					System.out.println("Shutdown notification sent successfully");
				} else {
					System.err.println("Failed to send shutdown notification. HTTP status: " + response.code() + " - "
							+ response.message());
				}
			}
		});

	}

	@Autowired
	private InsuranceExpirationNotifier notifier;

	@Override
	public void run(String... args) throws Exception {
		notifier.loadInsuranceData();
	}

}

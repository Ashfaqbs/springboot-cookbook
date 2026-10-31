package com.ashfaq.dev.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashfaq.dev.constants.MainConstants;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RestController
@RequestMapping("/api/telegram")
public class TelegramBotController {

	private static final String MESSAGE_TEXT = "‍Im Active";

	@GetMapping("/send")
	public String sendMessage() {
		OkHttpClient client = new OkHttpClient();
		String url = "https://api.telegram.org/bot" + MainConstants.BOT_TOKEN + "/sendMessage?chat_id="
				+ MainConstants.GROUP_CHAT_ID + "&text=" + MESSAGE_TEXT;

		Request request = new Request.Builder().url(url).build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response.isSuccessful()) {
					System.out.println("Message sent successfully");
				} else {
					System.out.println("Failed to send message: " + response.message());
				}
			}
		});

		return "Message sent to Telegram group";
	}
}

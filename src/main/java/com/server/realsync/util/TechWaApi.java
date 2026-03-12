package com.server.realsync.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class TechWaApi {

	
	private static final String API_URL = "https://cloud.kalaminfotec.in/api/6ce8e7a4-b211-45ef-9fc1-e990ec889404/contact/send-template-message";
    private static final String TOKEN = "7VvPsMmw6qHnxWGyzpTrNC6KbrGLY4fjj6Cn4fp59SvUxyZkTr0LwtNsYLQpHBsm";
    private static final String COOKIE = "PHPSESSID=7q0idtt5gjihntppj4org0rn13";
    
    public static void sendTemplateMessage(String phoneNumber, String templateName, String templateLanguage,
                                           String field1, String field2, String field3, String field4, String field5) {
        try {
            URL url = new URL(API_URL + "?token=" + TOKEN);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cookie", COOKIE);
            conn.setDoOutput(true);

            String jsonPayload = String.format(
                "{\"phone_number\": \"%s\", \"template_name\": \"%s\", \"template_language\": \"%s\", " +
                "\"field_1\": \"%s\", \"field_2\": \"%s\", \"field_3\": \"%s\", \"field_4\": \"%s\", \"field_5\": \"%s\"}",
                phoneNumber, templateName, templateLanguage, field1, field2, field3, field4, field5
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	public static void sendTemplateMessageFood(String phoneNumber, String templateName, String templateLanguage,
			String field1, String field2, String field3, String field4, String field5, String field6, String field7,
			String field8, String field9, String field10) {
		try {
			URL url = new URL(API_URL + "?token=" + TOKEN);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Cookie", COOKIE);
			conn.setDoOutput(true);

			String jsonPayload = String.format(
				    "{\"phone_number\": \"%s\", \"template_name\": \"%s\", \"template_language\": \"%s\", "
				    + "\"field_1\": \"%s\", \"field_2\": \"%s\", \"field_3\": \"%s\", \"field_4\": \"%s\", "
				    + "\"field_5\": \"%s\", \"field_6\": \"%s\", \"field_7\": \"%s\", \"field_8\": \"%s\", "
				    + "\"field_9\": \"%s\", \"field_10\": \"%s\"}",
				    phoneNumber, templateName, templateLanguage,
				    field1, field2, field3, field4, field5, field6, field7, field8, field9, field10
				);

			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		//sendTemplateMessage("13068801749", "event_created_new1", "en","Muthu", "Ram weds Sita1", "https://www.aipixture.com/7/e/5/guest?pin=9780", "Sri Balaji Digital Studio",
		//		"9789562069");
		sendTemplateMessageFood("9789562069", "aifoodsnap", "en_US", "Muthu", "Rajesh", "450",
				"Rice and Vegetable Stew", "70", "15", "10", "https://www.aipixture.com/7/e/5/guest?pin=9780", "Rajesh",
				"9789562069");
	}
	
	@Async
	public void aifoodsnap(String mobile, String custName, String docName, String calory, String food, String carbs,
			String fat, String protein, String url, String docNumber) {
		sendTemplateMessageFood(mobile, "aifoodsnap", "en_US", custName, docName, calory, food, carbs, fat, protein, url,
				docName, docNumber);
	}
	
}

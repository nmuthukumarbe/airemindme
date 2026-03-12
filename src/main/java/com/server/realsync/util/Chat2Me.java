/**
 * 
 */
package com.server.realsync.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class Chat2Me {

    private final String instanceId = "690353DC2DCBF";
    private final String accessToken = "6902092978c42";

    public static String buildSiteVisitMessage(String customerName, String propertyName, String visitDate, String visitTime) {
        return String.format("""
                Hello %s,
                Your property site visit has been successfully scheduled! ✅

                📍 Property: %s
                📅 Date: %s
                ⏰ Time: %s

                We’re excited to welcome you! 🌿
                Our representative from Vittal Property Solutions will guide you through the property, highlight its unique features, and show you why it’s not just a home — but a smart, future-ready investment.

                💰 Excellent value for money, great location growth, and high investment returns make this property truly worth exploring.

                ✨ Great opportunities don’t last long — don’t miss it!

                For more details, visit vittalpropertysolutions.com/login

                We look forward to meeting you and helping you find your dream property. 🏠
                - Team Vittal Property Solutions
                - 9994676459
                """, customerName, propertyName, visitDate, visitTime);
    }

    @Async
    public void siteVisitNotify(String customerName, String propertyName, String visitDate, String visitTime, String mobile) {
        try {
            String message = buildSiteVisitMessage(customerName, propertyName, visitDate, visitTime);

            // Encode special characters safely for JSON
            message = message.replace("\"", "\\\"");

            JSONObject json = new JSONObject();
            json.put("number", "91" + mobile);
            json.put("type", "text");
            json.put("message", message);
            json.put("instance_id", instanceId);
            json.put("access_token", accessToken);

            sendWhatsapp(json.toString());
        } catch (Exception e) {
            System.out.println("Exception in siteVisitNotify: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendWhatsapp(String jsonBody) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://chatwithus.in/api/send"))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response: " + response.body());
        } catch (Exception e) {
            System.out.println("Exception in sendWhatsapp: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

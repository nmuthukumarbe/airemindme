package com.server.realsync.mvc.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.realsync.dto.TemplateGenerateRequest;
import com.server.realsync.dto.TemplateGenerateResponse;
import com.server.realsync.entity.Account;
import com.server.realsync.util.SecurityUtil;

@RestController
@RequestMapping("/api/ai")
public class AITemplateController {

    @Value("${gemini.api.key}")
    private String apiKey;

    @PostMapping("/template/generate")
    public ResponseEntity<?> generateTemplate(@RequestBody TemplateGenerateRequest request) {
        try {
            Account account = SecurityUtil.getCurrentAccountId();
            if (account == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            if (apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_GEMINI_API_KEY")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Gemini API key is not configured in application properties.");
            }

            // Build Prompt
            String prompt = String.format("""
                Generate a professional customer communication template.

                Business Name:
                %s

                Business Category:
                %s

                Business Subcategory:
                %s

                Purpose:
                %s

                Template Type:
                %s

                Language:
                %s

                Rules:
                1. Generate only the template message.
                2. No explanation.
                3. No markdown.
                4. No title.
                5. Use professional tone.
                6. Include variables when relevant.

                Supported Variables:
                {customer_name}
                {amount}
                {due_date}
                {business_name}
                {plan_name}

                Examples:
                Payment reminders should use:
                {amount}
                {due_date}

                Birthday greetings should use:
                {customer_name}

                Business name should always use:
                {business_name}

                Return only the final template content.
                """, 
                account.getBusinessName() != null ? account.getBusinessName() : "",
                account.getCategory() != null ? account.getCategory() : "",
                account.getSubcategory() != null ? account.getSubcategory() : "",
                request.getPurpose(),
                request.getTemplateType(),
                request.getLanguage()
            );

            // Construct JSON request for Gemini
            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);

            JSONArray partsArray = new JSONArray();
            partsArray.put(textPart);

            JSONObject contentObj = new JSONObject();
            contentObj.put("parts", partsArray);

            JSONArray contentsArray = new JSONArray();
            contentsArray.put(contentObj);

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contentsArray);

            // Send request to Gemini API
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Error from Gemini API: " + httpResponse.body());
            }

            JSONObject responseJson = new JSONObject(httpResponse.body());
            String generatedContent = responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            if (generatedContent != null) {
                generatedContent = generatedContent.trim();
                if (generatedContent.startsWith("```")) {
                    generatedContent = generatedContent.replaceAll("^```[a-zA-Z]*\\n", "");
                    generatedContent = generatedContent.replaceAll("\\n```$", "");
                    generatedContent = generatedContent.trim();
                }
            }

            return ResponseEntity.ok(new TemplateGenerateResponse(generatedContent));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error generating template: " + e.getMessage());
        }
    }
}

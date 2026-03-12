package com.server.realsync.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.ImageSource;
import com.google.protobuf.ByteString;

public class GoogleVisionCaptcha {

	// Download image bytes from URL
	public static byte[] downloadImageBytes(String imageUrl) throws Exception {
	    URL url = new URL(imageUrl);
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	    // Set request method
	    connection.setRequestMethod("GET");
	    
	    // Set timeouts
	    connection.setConnectTimeout(35000);
	    connection.setReadTimeout(35000);

	    // Set browser-like headers (customize as needed)
	    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36");
	    connection.setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
	    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
	    connection.setRequestProperty("Referer", "https://hcmadras.tn.gov.in/case_status_mdu.php");
	    connection.setRequestProperty("Connection", "keep-alive");

	    // If you need to send cookies (like PHPSESSID)
	    // Replace with your actual cookie value or pass as param
	    connection.setRequestProperty("Cookie", "PHPSESSID=5h0jfuenvpihvggu6peg864mnk");

	    try (InputStream in = connection.getInputStream();
	         ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

	        byte[] buffer = new byte[8192];
	        int bytesRead;

	        while ((bytesRead = in.read(buffer)) != -1) {
	            baos.write(buffer, 0, bytesRead);
	        }

	        return baos.toByteArray();
	    }
	}


	// Extract text from image bytes using Vision API
	public static String extractFromBytes(ImageAnnotatorClient client, byte[] imageBytes) throws Exception {
		Image img = Image.newBuilder().setContent(ByteString.copyFrom(imageBytes)).build();
		Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

		BatchAnnotateImagesResponse response = client.batchAnnotateImages(List.of(request));
		for (AnnotateImageResponse res : response.getResponsesList()) {
			if (res.hasError()) {
				System.err.println("Error: " + res.getError().getMessage());
				return null;
			}
			for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
				// Return first detected text (whole block)
				return annotation.getDescription().replaceAll("\\s+", "");
			}
		}
		return null;
	}

	public static String getCaptcha() {
		// Load service account key file
		String result = null;
		try {
			InputStream serviceAccount =
			        new ClassPathResource("gcloudvisionbhuvana.json").getInputStream();
			
			//FileInputStream serviceAccount = new FileInputStream(
			//		"/Users/m0n00qb/Documents/Office/Backup/GitHub/realsync/gcloudvisionbhuvana.json");
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

			ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
					.setCredentialsProvider(() -> credentials).build();

			try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
				String url = "https://hcmadras.tn.gov.in/securimage_show.php?namespace=contact&0.9072714771937415";
				byte[] imageBytes = downloadImageBytes(url);
				result = extractFromBytes(client, imageBytes);
				//System.out.println("Extracted Captcha: " + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(GoogleVisionCaptcha.getCaptcha());
	}
}

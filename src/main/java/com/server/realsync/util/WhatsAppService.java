/**
 * 
 */
package com.server.realsync.util;

/**
 * 
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WhatsAppService {

	private static final String API_URL = "https://bulky.watzup.in/api/send/whatsapp";
	private static final String SECRET = "6886d6f14438fbd379ab8ef1343cf5cc3a992be0";
	private static final String ACCOUNT = "1760202463d82c8d1619ad8176d665453cfb2e55f068ea8edf8bd6b";
	private static final String COOKIE = "PHPSESSID=97fd37416c5d0df97011f5635493da1b";

	public static void sendDayEndReport(String recipient, File pdfFile, int totalCases) throws Exception {
		// ✅ Build message
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		String inspiringQuote = "Reflect on today's work, learn from challenges, and prepare for a stronger tomorrow.";

		String message = "Good Evening 🌙,\n\n" + "📅 Date: " + today + "\n"
				+ "📑 Day End Report is now available with remarks.\n" + "Total Cases Today: " + totalCases + "\n"
				+ "Please review the updates and remarks for today’s cases.\n\n" + "🔗 https://ailawmate.com/\n\n"
				+ "✨ \"" + inspiringQuote + "\"\n" + "Thank you for your dedication today. Have a restful evening!\n\n"
				+ "— AiLawMate";

		// ✅ Setup HTTP connection
		URL url = new URL(API_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Cookie", COOKIE);

		String boundary = "----Boundary" + System.currentTimeMillis();
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
			// Form fields
			writeFormField(out, boundary, "secret", SECRET);
			writeFormField(out, boundary, "account", ACCOUNT);
			writeFormField(out, boundary, "recipient", recipient);
			writeFormField(out, boundary, "type", "document");
			writeFormField(out, boundary, "message", message);
			writeFormField(out, boundary, "document_type", "pdf");
			writeFileField(out, boundary, "document_file", pdfFile, "application/pdf");

			// End boundary
			out.writeBytes("--" + boundary + "--\r\n");
		}

		// ✅ Read response
		int responseCode = conn.getResponseCode();
		InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}
	}

	public static void sendDailyCauseList(String recipient, File pdfFile, int caseCount, boolean isToday)
			throws Exception {
		// ✅ Build message
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		if (!isToday) {
			today = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		}
		String inspiringQuote = "Justice is the constant and perpetual wish to render everyone their due.";

		String messageStart = "Good Evening 🌙";
		if(isToday) {
			messageStart = "Good Morning ";
		} 
		String message = messageStart+",\n\n" + "📅 Date: " + today + "\n" + "📂 Total Cases Listed : " + caseCount
				+ "\n\n" + "Please plan your day accordingly.\n"
				+ "For detailed information (Court No. & Teams link to join), kindly sign in to the Dashboard.\n\n"
				+ "🔗 https://ailawmate.com/\n\n" + "✨ \"" + inspiringQuote + "\"\n"
				+ "Wishing you a productive and successful day ahead.\n\n" + "— AiLawMate";

		// ✅ Setup HTTP connection
		URL url = new URL(API_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Cookie", COOKIE);

		String boundary = "----Boundary" + System.currentTimeMillis();
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
			// Fields
			writeFormField(out, boundary, "secret", SECRET);
			writeFormField(out, boundary, "account", ACCOUNT);
			writeFormField(out, boundary, "recipient", recipient);
			writeFormField(out, boundary, "type", "document");
			writeFormField(out, boundary, "message", message);
			writeFormField(out, boundary, "document_type", "pdf");
			writeFileField(out, boundary, "document_file", pdfFile, "application/pdf");

			// End boundary
			out.writeBytes("--" + boundary + "--\r\n");
		}

		// ✅ Read response
		int responseCode = conn.getResponseCode();
		InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}
	}

	private static void writeFormField(DataOutputStream out, String boundary, String name, String value)
			throws IOException {
		out.writeBytes("--" + boundary + "\r\n");
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
		// Ensure UTF-8 encoding for emojis
		out.write(value.getBytes("UTF-8"));
		out.writeBytes("\r\n");
	}

	private static void writeFileField(DataOutputStream out, String boundary, String fieldName, File file,
			String mimeType) throws IOException {
		out.writeBytes("--" + boundary + "\r\n");
		out.writeBytes(
				"Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"\r\n");
		out.writeBytes("Content-Type: " + mimeType + "\r\n\r\n");
		Files.copy(file.toPath(), out);
		out.writeBytes("\r\n");
	}

	public static String buildAppointmentScheduledMessage(String clientName, String appointmentId,
			String appointmentDate, String appointmentTime, String relatedCase) {
		StringBuilder msg = new StringBuilder();

		msg.append("✅ *Appointment Scheduled*\n\n");
		msg.append("👤 *Client:* ").append(clientName).append("\n");
		msg.append("🆔 *Appointment ID:* ").append(appointmentId).append("\n");
		msg.append("📂 *Related Case:* ").append(relatedCase != null && !relatedCase.isEmpty() ? relatedCase : "N/A")
				.append("\n");
		msg.append("🗓️ *Date:* ").append(appointmentDate).append("\n");
		msg.append("⏰ *Time:* ").append(appointmentTime).append("\n\n");
		msg.append("🙏 Thank you for booking with us.\n");
		msg.append("📲 We will send you a reminder before your appointment.\n\n");
		msg.append("— *AI LawMate* ⚖️");

		return msg.toString();
	}

	// 2️⃣ Appointment On Date
	public static String buildAppointmentReminderMessage(String clientName, String appointmentId,
			String appointmentDate, String appointmentTime, String relatedCase) {
		StringBuilder msg = new StringBuilder();

		msg.append("🔔 *Appointment Reminder*\n\n");
		msg.append("👤 *Client:* ").append(clientName).append("\n");
		msg.append("🆔 *Appointment ID:* ").append(appointmentId).append("\n");
		msg.append("📂 *Related Case:* ").append(relatedCase != null && !relatedCase.isEmpty() ? relatedCase : "N/A")
				.append("\n");
		msg.append("🗓️ *Date:* ").append(appointmentDate).append("\n");
		msg.append("⏰ *Time:* ").append(appointmentTime).append("\n\n");
		msg.append("✅ Please confirm your availability.\n");
		msg.append("📲 Reply here if you have any questions.\n\n");
		msg.append("— *AI LawMate* ⚖️");

		return msg.toString();
	}

	public static void sendWhatsAppMessage(String recipient, String message) throws Exception {
		// ✅ Setup HTTP connection
		URL url = new URL(API_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Cookie", COOKIE);

		String boundary = "----Boundary" + System.currentTimeMillis();
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
			// Fields
			writeFormField(out, boundary, "secret", SECRET);
			writeFormField(out, boundary, "account", ACCOUNT);
			writeFormField(out, boundary, "recipient", "+91" + recipient);
			writeFormField(out, boundary, "type", "text");
			writeFormField(out, boundary, "message", message);

			// End boundary
			out.writeBytes("--" + boundary + "--\r\n");
		}

		// ✅ Read response
		int responseCode = conn.getResponseCode();
		InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}
	}


}

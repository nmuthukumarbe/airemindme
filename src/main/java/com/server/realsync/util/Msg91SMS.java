package com.server.realsync.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * This class is used to deliver application sms to end user
 * 
 * @author Muthu N
 * @since 2.0
 * 
 */
@Component
public class Msg91SMS implements IAppSms {
	private Log log = LogFactory.getLog(this.getClass());
	private final String goodStartzSenderName = "GUDSTZ";
	// Your authentication key
	private final String authkey = "317952AtUwDOh2wv5e43d16cP1";// 317952AtUwDOh2wv5e43d16cP1
	// define route
	private final String route = "4";

	/**
	 * Method to send SMS to end user
	 * 
	 * @param textMessageVO
	 * @throws Exception
	 */
	public void sendTransactionlSMS(final TextMessageVO textMessageVO, String companyName) throws Exception {
		log.info("Preparing to send mail for :" + textMessageVO.getNumber().getNationalNumber());
		try {
			String senderName = this.goodStartzSenderName;

			// URLEncoder.encode(textMessageVO.getMessage(), "UTF-8")
			// textMessageVO.getNumber().getNationalNumber()

			// Prepare Url
			URLConnection myURLConnection = null;
			URL myURL = null;
			BufferedReader reader = null;

			// encoding message
			String encoded_message = URLEncoder.encode(textMessageVO.getMessage(), "UTF-8");

			// Send SMS API
			String mainUrl = "https://control.msg91.com/api/sendhttp.php?";

			// Prepare parameter string
			StringBuilder sbPostData = new StringBuilder(mainUrl);
			sbPostData.append("authkey=" + authkey);
			sbPostData.append("&mobiles=" + textMessageVO.getNumber().getNationalNumber());
			sbPostData.append("&message=" + encoded_message);
			sbPostData.append("&route=" + route);
			sbPostData.append("&sender=" + senderName);
			sbPostData.append("&DLT_TE_ID=" + textMessageVO.getDltTemplateId());

			// final string
			mainUrl = sbPostData.toString();
			// prepare connection
			myURL = new URL(mainUrl);
			myURLConnection = myURL.openConnection();
			myURLConnection.connect();
			reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
			// reading response
			String response;
			while ((response = reader.readLine()) != null)
				// print response
				System.out.println(response);

			// finally close connection
			reader.close();
			log.info("SMS delivered successfully :" + textMessageVO.getNumber().getNationalNumber());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("App SMS delivery failed", e);
		}
	}
}

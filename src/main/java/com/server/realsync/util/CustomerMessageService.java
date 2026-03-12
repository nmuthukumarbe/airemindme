package com.server.realsync.util;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.realsync.config.AppConstants;
import com.server.realsync.util.Phonenumber.PhoneNumber;

@Component
public class CustomerMessageService {

	@Autowired
	Msg91SMS msg91SMS;

	public void sendCustomerRegistrationSms(String mobileNumber, String customerName, String companyName) {
		try {
			PhoneNumber phoneNumber = new PhoneNumber();
			phoneNumber.setNationalNumber(NumberUtils.toLong(mobileNumber));

			TextMessageVO textMessageVO = new TextMessageVO();
			textMessageVO.setNumber(phoneNumber);
			textMessageVO.setDltTemplateId(AppConstants.custRegisTempId);
			textMessageVO.setMessage(generateCustRegisSMSContent(customerName, companyName));
			// Send Notification
			//msg91SMS.sendTransactionlSMS(textMessageVO, companyName);
		} catch (Exception e) {
			System.out.println("Failed sendCustomerRegistrationSms :" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static String generateCustRegisSMSContent(String customerName, String companyName) {
		// Define the template with placeholders
		String template = "Welcome {customerName}! Your registration is complete. We’ll notify you when your photo is matched by AiPixture. Stay tuned! - {companyName} by GUDSTZ";

		// Replace placeholders with actual values
		String smsContent = template.replace("{customerName}", customerName).replace("{companyName}", companyName);

		return smsContent;
	}

}

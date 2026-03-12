/**
 * 
 */
package com.server.realsync.util;

/**
 * @author test
 *
 */
public interface IAppSms {

	public void sendTransactionlSMS(final TextMessageVO textMessageVO, String companyName) throws Exception;
	
}

/**
 * 
 */
package com.server.realsync.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.models.MetaInfo;
import com.phonepe.sdk.pg.common.models.response.OrderStatusResponse;
import com.phonepe.sdk.pg.payments.v2.StandardCheckoutClient;
import com.phonepe.sdk.pg.payments.v2.models.request.StandardCheckoutPayRequest;
import com.phonepe.sdk.pg.payments.v2.models.response.StandardCheckoutPayResponse;
import com.server.realsync.entity.Account;
import com.server.realsync.entity.AccountPlan;
import com.server.realsync.entity.CustomUserDetails;
import com.server.realsync.entity.Invoice;
import com.server.realsync.services.AccountPlanService;
import com.server.realsync.services.AccountService;
import com.server.realsync.services.InvoiceService;



/**
 * 
 */

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private AccountPlanService accountPlanService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private InvoiceService invoiceService;

    //Merchant id - M23SPHQJMHGJS
    private final String clientId = "SU2509151740239531257987";
    private final String clientSecret = "4d2bb67c-151a-4dd7-bfd6-a8970dae8650";
    private final Integer clientVersion = 1;
    private final String mid = "M23TMSPV01W7R";
    private final Env env = Env.PRODUCTION; // Change to Env.PRODUCTION when live, SANDBOX

    private final StandardCheckoutClient client = StandardCheckoutClient.getInstance(clientId, clientSecret, clientVersion, env);

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, String>> initiatePayment(@RequestParam long amount,
    		@RequestParam("planId") Integer planId) {
        try {
        	int accountId = 0;
        	//long customerId = 0;
        	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
				CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
				accountId = customUserDetails.getAccountId();
				//customerId = customUserDetails.getCustomerId();
			}
            String merchantOrderId = UUID.randomUUID().toString();
			System.out.println("merchantOrderId = " + merchantOrderId+", Amount: "+amount+", PlanId: "+planId);
            String redirectUrl = "https://aipixture.com/api/payments/complete";
            MetaInfo metaInfo = MetaInfo.builder()
                    .udf1(String.valueOf(accountId))
                    .udf2(merchantOrderId)
                    .udf3(String.valueOf(planId))
                    .udf4(String.valueOf(amount))
                    .udf5(String.valueOf(accountId))
                    .build();

            StandardCheckoutPayRequest standardCheckoutPayRequest = StandardCheckoutPayRequest.builder()
                    .merchantOrderId(merchantOrderId)
                    .amount(amount*100)
                    .redirectUrl(redirectUrl)
                    .metaInfo(metaInfo)
                    .build();
             
            StandardCheckoutPayResponse standardCheckoutPayResponse = client.pay(standardCheckoutPayRequest);
            String redirectUrlForUI = standardCheckoutPayResponse.getRedirectUrl();
            // Return both values in a JSON response
            Map<String, String> response = new HashMap<>();
            response.put("merchantOrderId", merchantOrderId);
            response.put("redirectUrl", redirectUrlForUI);

            //
            Invoice invoice = new Invoice();
            invoice.setAccountId(Integer.valueOf(accountId));
            invoice.setMerchantOrderId(merchantOrderId);
            invoice.setStatus("Initiated");
            invoice.setModeOfPayment("Phonepe");
			invoice.setStartDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
			invoice.setAmount(Double.valueOf(amount));
			invoice.setPlanId(Integer.valueOf(planId));
			invoice = invoiceService.create(invoice);
			response.put("invoiceId", String.valueOf(invoice.getId()));
			
            return ResponseEntity.ok(response);
        } catch (Exception e) {
        	e.printStackTrace();
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    
    @GetMapping("/status")
    public void getOrderStatus(@RequestParam("invoiceId") String invoiceId) {
        try {
        	Invoice invoice = invoiceService.getById(Integer.valueOf(invoiceId)).get();
            OrderStatusResponse orderStatusResponse = client.getOrderStatus(invoice.getMerchantOrderId());
            System.out.println("State : " + orderStatusResponse.getState());
            System.out.println("Meta : "+ orderStatusResponse.getMetaInfo());
            if ("COMPLETED".equals(orderStatusResponse.getState())) {
				invoice.setModeOfPayment("Phonepe");
				invoice.setStatus("COMPLETED");
				Optional<Account> optionalAccount = accountService
						.findById(Integer.valueOf(orderStatusResponse.getMetaInfo().getUdf5()));
				Optional<AccountPlan> optionalAccountPlanUsage = accountPlanService
						.getAccountPlanUsage(Integer.valueOf(orderStatusResponse.getMetaInfo().getUdf1()));
				Account account = optionalAccount.get();
				AccountPlan optionalAccountPlan = null;
				if(optionalAccountPlanUsage.isPresent()) {
					optionalAccountPlan = optionalAccountPlanUsage.get();
				} else {
					optionalAccountPlan = new AccountPlan();
					optionalAccountPlan.setAccount(account);
					//optionalAccountPlan.setCustomerId(Long.valueOf(orderStatusResponse.getMetaInfo().getUdf1()));
				}
				accountPlanService.updateAccountPlanUsage(optionalAccountPlan, account,
						Integer.valueOf(orderStatusResponse.getMetaInfo().getUdf3()), invoice);
				//
				System.out.println("Invoice saved...");
				System.out.println("Payment Mode : " + orderStatusResponse.getPaymentDetails().get(0).getPaymentMode());
            }
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    @GetMapping("/complete")
    public void getOrderComplete() {
        try {
        	System.out.println("Complete method called...");
            //OrderStatusResponse orderStatusResponse = client.getOrderStatus(merchantOrderId);
            //orderStatusResponse.getState();
            //return orderStatusResponse.getState();
        } catch (Exception e) {
            //return "Error: " + e.getMessage();
        }
    }
    
}
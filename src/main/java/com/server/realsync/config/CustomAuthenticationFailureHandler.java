/**
 * 
 */
package com.server.realsync.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		// String source = request.getParameter("source");
		// Add error attribute to request
        request.setAttribute("error", "Invalid username or password");

        // Forward to login page (no redirect)
        response.sendRedirect(request.getContextPath() + "/login?error=true");

		/*
		 * if ("mweb-customer".equalsIgnoreCase(source)) { // Redirect to your customer
		 * login page with error param
		 * //response.sendRedirect("/mweb/customer/signin?error=true");
		 * 
		 * } else if ("mweb".equalsIgnoreCase(source)) {
		 * response.sendRedirect("/mweb/signin?error=true"); } else { // fallback
		 * response.sendRedirect("/login?error=true"); }
		 */
	}
}

/**
 * 
 */
package com.server.realsync.services;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;


@Service
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;

    public PdfGeneratorService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

   
}

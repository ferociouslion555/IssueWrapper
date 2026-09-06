// Author: Salih Eren Yüzbaşıoğlu
package com.assignment.issues.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoint as required by 12-factor app design.
 * Author: Salih Eren Yüzbaşıoğlu
 */
@RestController
public class HealthController {

    @GetMapping("/healthz")
    public ResponseEntity<String> healthz() {
        return ResponseEntity.ok("OK");
    }
}

// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoint as required by 12-factor app design.
 * Author: Salih Eren Yuzbazzozlu
 */
@RestController
public class HealthController {

    @GetMapping("/healthz")
    public ResponseEntity<String> healthz() {
        return ResponseEntity.ok("OK");
    }
}

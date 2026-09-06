// Author: Salih Eren Yüzbaşıoğlu
package com.assignment.issues.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WebhookSignatureValidatorTest {

    @Test
    void testValidSignature() {
        WebhookSignatureValidator validator = new WebhookSignatureValidator();
        String payload = "Hello, World!";
        String secret = "mysecret";
        // Expected HMAC-SHA256 for "Hello, World!" with key "mysecret"
        String signature = "sha256=734cc62f32841568f45715aeb9f4d7891324e6d948e4c6c60c0621cdac48623a";

        assertTrue(validator.isValidSignature(payload, signature, secret));
    }

    @Test
    void testInvalidSignature() {
        WebhookSignatureValidator validator = new WebhookSignatureValidator();
        String payload = "Hello, World!";
        String secret = "mysecret";
        String signature = "sha256=invalid1234567890abcdef";

        assertFalse(validator.isValidSignature(payload, signature, secret));
    }
    
    @Test
    void testMissingPrefix() {
        WebhookSignatureValidator validator = new WebhookSignatureValidator();
        String payload = "Hello, World!";
        String secret = "mysecret";
        String signature = "734cc62f32841568f45715aeb9f4d7891324e6d948e4c6c60c0621cdac48623a";

        assertFalse(validator.isValidSignature(payload, signature, secret));
    }
}

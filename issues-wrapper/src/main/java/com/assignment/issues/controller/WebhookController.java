// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues.controller;

import com.assignment.issues.entity.WebhookEvent;
import com.assignment.issues.repository.WebhookEventRepository;
import com.assignment.issues.service.WebhookSignatureValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private final WebhookSignatureValidator signatureValidator;
    private final WebhookEventRepository webhookEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String webhookSecret;

    public WebhookController(
            WebhookSignatureValidator signatureValidator,
            WebhookEventRepository webhookEventRepository) {
        this.signatureValidator = signatureValidator;
        this.webhookEventRepository = webhookEventRepository;
        this.webhookSecret = System.getProperty("WEBHOOK_SECRET");
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader(value = "x-hub-signature-256", required = false) String signatureHeader,
            @RequestHeader(value = "x-github-event", required = false) String githubEvent,
            @RequestHeader(value = "x-github-delivery", required = false) String deliveryId,
            @RequestBody String payload) {
            
        // 1. Verify Signature
        if (webhookSecret == null || !signatureValidator.isValidSignature(payload, signatureHeader, webhookSecret)) {
            logger.warn("Invalid or missing webhook signature. Delivery ID: {}", deliveryId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Accept only issues / issue_comment / ping
        if (!"issues".equals(githubEvent) && !"issue_comment".equals(githubEvent) && !"ping".equals(githubEvent)) {
            logger.warn("Unknown or unsupported event type: {}", githubEvent);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 3. Idempotency Check
        if (deliveryId != null && webhookEventRepository.existsByDeliveryId(deliveryId)) {
            logger.info("Webhook delivery {} already processed. Skipping.", deliveryId);
            return ResponseEntity.noContent().build();
        }

        // 4. Parse payload and save
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            String action = rootNode.path("action").asText(null);
            Integer issueNumber = null;
            
            if (rootNode.has("issue") && rootNode.get("issue").has("number")) {
                issueNumber = rootNode.get("issue").get("number").asInt();
            }

            WebhookEvent event = new WebhookEvent();
            event.setDeliveryId(deliveryId);
            event.setEvent(githubEvent);
            event.setAction(action);
            event.setIssueNumber(issueNumber);
            event.setTimestamp(OffsetDateTime.now());

            webhookEventRepository.save(event);
            logger.info("Successfully processed {} event (action: {}) for delivery {}", githubEvent, action, deliveryId);

        } catch (Exception e) {
            logger.error("Failed to parse webhook payload for delivery {}", deliveryId, e);
            // Even if parsing fails, we return a 2xx to ack the webhook if it was just malformed, 
            // or 400 if it's completely unreadable. Let's return 400 for bad JSON.
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events")
    public ResponseEntity<List<WebhookEvent>> getRecentEvents() {
        return ResponseEntity.ok(webhookEventRepository.findTop10ByOrderByTimestampDesc());
    }
}

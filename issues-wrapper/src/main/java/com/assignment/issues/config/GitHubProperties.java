// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Base64;

@Configuration
@ConfigurationProperties(prefix = "github")
public class GitHubProperties implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(GitHubProperties.class);

    private String token;
    private String owner;
    private String repo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    @Override
    public void afterPropertiesSet() {
        // Handle full URL in GITHUB_REPO e.g. https://github.com/saliherenyuzbazzozlu/IssueWrapper.git
        if (StringUtils.hasText(this.repo) && this.repo.contains("/")) {
            String[] parts = this.repo.split("/");
            String lastPart = parts[parts.length - 1];
            if (lastPart.endsWith(".git")) {
                lastPart = lastPart.substring(0, lastPart.length() - 4);
            }
            logger.info("Extracted repository name '{}' from '{}'", lastPart, this.repo);
            this.repo = lastPart;
        }

        // Generate webhook secret if missing
        String webhookSecret = System.getProperty("WEBHOOK_SECRET");
        if (!StringUtils.hasText(webhookSecret)) {
            byte[] randomBytes = new byte[32];
            new SecureRandom().nextBytes(randomBytes);
            webhookSecret = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
            System.setProperty("WEBHOOK_SECRET", webhookSecret);
            logger.warn("=========================================================");
            logger.warn("WEBHOOK_SECRET was not set in .env.");
            logger.warn("Generated a secure random WEBHOOK_SECRET for you to use:");
            logger.warn(webhookSecret);
            logger.warn("Please configure this in your GitHub webhook settings!");
            logger.warn("=========================================================");
        }
    }
}

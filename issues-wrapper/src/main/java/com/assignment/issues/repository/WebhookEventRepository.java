// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues.repository;

import com.assignment.issues.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
    
    boolean existsByDeliveryId(String deliveryId);
    
    List<WebhookEvent> findTop10ByOrderByTimestampDesc();
}

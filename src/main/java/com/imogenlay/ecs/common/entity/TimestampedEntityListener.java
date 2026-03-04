package com.imogenlay.ecs.common.entity;

import java.time.LocalDateTime;
 
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class TimestampedEntityListener {
    @PrePersist
    public void onPrePersist(Object entity) {
        if (entity instanceof ITimestampable timestampable) {
            LocalDateTime now = LocalDateTime.now();
            timestampable.setCreatedAt(now);
            timestampable.setUpdatedAt(now);
        }
    }

    @PreUpdate
    public void onPreUpdate(Object entity) {
        if (entity instanceof ITimestampable timestampable) {
            LocalDateTime now = LocalDateTime.now();
            timestampable.setUpdatedAt(now);
        }
    }
}

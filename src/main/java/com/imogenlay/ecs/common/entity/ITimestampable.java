package com.imogenlay.ecs.common.entity;

import java.time.LocalDateTime;

public interface ITimestampable {
    
    public LocalDateTime getCreatedAt();
    public void setCreatedAt(LocalDateTime timestamp);
    
    public LocalDateTime getUpdatedAt();
    public void setUpdatedAt(LocalDateTime timestamp);
}

package com.imogenlay.ecs.common.entity;
 
import java.time.LocalDateTime;
import jakarta.persistence.MappedSuperclass;
 
@MappedSuperclass
public class BaseTimestampedEntity extends BaseEntity implements ITimestampable {
     
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BaseTimestampedEntity() {}
     
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

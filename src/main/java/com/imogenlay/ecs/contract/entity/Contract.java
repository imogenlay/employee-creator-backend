package com.imogenlay.ecs.contract.entity;

import com.imogenlay.ecs.common.entity.BaseEntity;
import com.imogenlay.ecs.contract.dtos.ContractResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "contracts")
public class Contract extends BaseEntity
{
	private String name;
	private Boolean isFullTime;

	public Contract() { }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public Boolean getIsFullTime() { return isFullTime; }

	public void setIsFullTime(Boolean isFullTime) { this.isFullTime = isFullTime; }

	public ContractResponse createResponse()
	{
		return new ContractResponse(getId(), getName(), getIsFullTime());
	}
}

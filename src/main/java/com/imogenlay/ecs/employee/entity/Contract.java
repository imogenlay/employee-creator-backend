package com.imogenlay.ecs.employee.entity;

import com.imogenlay.ecs.common.entity.BaseEntity;
import com.imogenlay.ecs.employee.dtos.ContractResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "contracts")
public class Contract extends BaseEntity
{
	private String name;

	public Contract() { }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }
 
	public ContractResponse createResponse()
	{
		return new ContractResponse(getId(), getName());
	}
}

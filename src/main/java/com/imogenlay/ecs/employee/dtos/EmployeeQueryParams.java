package com.imogenlay.ecs.employee.dtos;

import com.imogenlay.ecs.common.SortOrder;

public record EmployeeQueryParams(SortOrder order)
{
	public SortOrder orderOrDefault()
	{
		return order == null ? SortOrder.DESC : order;
	}
}

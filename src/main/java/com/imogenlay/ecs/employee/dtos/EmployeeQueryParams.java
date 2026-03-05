package com.imogenlay.ecs.employee.dtos;

import com.imogenlay.ecs.common.SortOrder;

import java.util.List;

public record EmployeeQueryParams(List<String> names, SortOrder order) {

	public SortOrder orderOrDefault() {
		return order == null ? SortOrder.DESC : order;
	}
}

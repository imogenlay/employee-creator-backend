package com.imogenlay.ecs.employee.dtos;

import java.time.LocalDate;

public record EmployeeResponse(
		Long id,
		String firstName,
		String middleName,
		String lastName,
		String email,
		String mobile,
		String address,
		Long contractId,
		String contractName,
		Long hoursPerWeek,
		LocalDate startDate,
		LocalDate endDate)
{
}

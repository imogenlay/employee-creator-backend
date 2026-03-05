package com.imogenlay.ecs.employee.dtos;

import com.imogenlay.ecs.common.entity.IFullName;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateEmployeeDto(

		@NotBlank(message = "First name is required")
		@Size(max = 100, message = "First name must be <= 100 characters")
		String firstName,

		@Size(max = 100, message = "Middle name must be <= 100 characters")
		String middleName,

		@NotBlank(message = "Last name is required")
		@Size(max = 100, message = "Last name must be <= 100 characters")
		String lastName,

		@NotBlank(message = "Email is required")
		@Email(message = "Email must be valid")
		String email,

		@Pattern(
				regexp = "^[0-9+\\-() ]{7,20}$",
				message = "Mobile number is invalid"
		)
		String mobile,

		@NotBlank(message = "Address is required")
		@Size(max = 255, message = "Address must be <= 255 characters")
		String address,

		@NotNull(message = "Employment type (isFullTime) is required")
		Boolean isFullTime,

		@NotNull(message = "Hours per week is required")
		@Min(value = 1, message = "Hours per week must be at least 1")
		@Max(value = 40, message = "Hours per week must be <= 40")
		Long hoursPerWeek,

		@NotNull(message = "Start date is required")
		LocalDate startDate,

		LocalDate endDate
) implements IFullName
{
	@Override
	public String getFirstName() { return firstName(); }

	@Override
	public String getMiddleName() { return middleName(); }

	@Override
	public String getLastName() { return lastName(); }
}

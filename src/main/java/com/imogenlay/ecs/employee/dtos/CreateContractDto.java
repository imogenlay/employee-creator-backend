package com.imogenlay.ecs.employee.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateContractDto(
		@NotBlank(message = "Name is required")
		@Size(max = 100, message = "Name must be <= 100 characters")
		String name
)
{
}

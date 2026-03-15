package com.imogenlay.ecs.claude.dtos;

import jakarta.validation.constraints.NotBlank;

public record Message(

		@NotBlank(message = "Role must not be blank")
		String role,
		@NotBlank(message = "Content must not be blank")
		String content
)
{
}

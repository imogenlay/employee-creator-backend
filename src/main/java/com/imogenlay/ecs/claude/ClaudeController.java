package com.imogenlay.ecs.claude;

import com.imogenlay.ecs.claude.dtos.ClaudeResponse;
import com.imogenlay.ecs.claude.dtos.Message;
import com.imogenlay.ecs.common.ConditionalObject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/claude")
public class ClaudeController
{
	private final ClaudeService claudeService;

	public ClaudeController(ClaudeService claudeService)
	{
		this.claudeService = claudeService;
	}

	@PostMapping()
	public ResponseEntity<ClaudeResponse> create(
			@RequestBody @NotEmpty(message = "Message list cannot be empty")
			List<@Valid Message> messages)
	{

		ConditionalObject<ClaudeResponse> result = this.claudeService.ask(messages);
		if (result.hasError())
			result.throwError();

		return ResponseEntity.status(HttpStatus.OK).body(result.getObject());
	}
}

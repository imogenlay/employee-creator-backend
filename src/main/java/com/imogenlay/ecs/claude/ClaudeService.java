package com.imogenlay.ecs.claude;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imogenlay.ecs.claude.dtos.ClaudeResponse;
import com.imogenlay.ecs.claude.dtos.Message;
import com.imogenlay.ecs.common.ConditionalObject;
import com.imogenlay.ecs.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ClaudeService
{
	@Value("${anthropic.api.key}")
	private String apiKey;

	private final RestTemplate restTemplate = new RestTemplate();
	private final String url = "https://api.anthropic.com/v1/messages";

	private final String system = """
			You are an assistant that is helping a user navigate an Employee Creator application.
			Your goal is to help the user access specific employee information with queries.
			Responses will be printed to a chat box with very limited formatting.
			All text will be displayed as is, but a highlighted inline-block can be shown if
			surrounded with a single ` character on either side. The block is monospace and has an outline.
			""";

	private final EmployeeService employeeService;
	private final ObjectMapper objectMapper;

	public ClaudeService(ObjectMapper objectMapper, EmployeeService employeeService)
	{
		this.objectMapper = objectMapper;
		this.employeeService = employeeService;
	}

	public ConditionalObject<ClaudeResponse> ask(List<Message> messages)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.set("x-api-key", apiKey);
		headers.set("anthropic-version", "2023-06-01");
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> employeeTool = Map.of(
				"name", "get_all_employees",
				"description", "Fetches all employees from the database",
				"input_schema", Map.of(
						"type", "object",
						"properties", Map.of(),
						"required", List.of()
				)
		);

		var conversationHistory = createMessageChain(messages);
		Map<String, Object> body = Map.of(
				"model", "claude-sonnet-4-20250514",
				"max_tokens", 1024,
				"system", system,
				"tools", List.of(employeeTool),
				"messages", conversationHistory
		);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
		var content = (List<Map<String, Object>>) response.getBody().get("content");
		var stopReason = response.getBody().get("stop_reason");

		if ("tool_use".equals(stopReason))
		{
			Map toolContent = null;

			for (Map<String, Object> stringObjectMap : content)
				if ("tool_use".equals(stringObjectMap.get("type")))
					toolContent = stringObjectMap;

			if (toolContent != null)
			{
				String toolName = (String) toolContent.get("name");

				conversationHistory.add(Map.of(
						"role", "assistant",
						"content", content
				));


				try
				{
					System.out.println("converting employees to string...");
					String employees = objectMapper.writeValueAsString(employeeService.findAll());
					System.out.println("gonna reply with info:\n" + employees);

					conversationHistory.add(Map.of(
							"role", "user",
							"content", List.of(Map.of(
									"type", "tool_result",
									"tool_use_id", toolContent.get("id"),
									"content", employees
							))
					));

					body = Map.of(
							"model", "claude-sonnet-4-20250514",
							"max_tokens", 1024,
							"system", system,
							"tools", List.of(employeeTool),
							"messages", conversationHistory
					);

					request = new HttpEntity<>(body, headers);
					response = restTemplate.postForEntity(url, request, Map.class);
				}
				catch (Exception ex)
				{
					System.out.println("ERRORRRR " + ex.getMessage() + "   ");
				}
			}
		}

		// Get text from response.
		System.out.println(response);
		var content2 = (List<Map<String, String>>) (response.getBody().get("content"));

		String output = content2.get(0).get("text");
		System.out.println(output);
		return new ConditionalObject<>(new ClaudeResponse("assistant", output));
	}


	private List<Map<String, Object>> createMessageChain(List<Message> messages)
	{
		var list = new ArrayList<Map<String, Object>>(messages.size());
		for (Message message : messages)
			list.add(Map.of(
					"role", message.role(),
					"content", message.content()));

		return list;
	}
}

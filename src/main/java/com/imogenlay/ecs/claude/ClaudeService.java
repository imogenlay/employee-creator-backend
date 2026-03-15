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

	private final ObjectMapper objectMapper;
	private final EmployeeService employeeService;
	private final RestTemplate restTemplate = new RestTemplate();

	public ClaudeService(ObjectMapper objectMapper, EmployeeService employeeService)
	{
		this.objectMapper = objectMapper;
		this.employeeService = employeeService;
	}

	public ConditionalObject<ClaudeResponse> ask(List<Message> messages)
	{
		var headers = buildHeaders();
		var tools = List.of(buildEmployeeTool());
		var conversationHistory = createMessageChain(messages);

		ResponseEntity<Map> response = callClaude(headers, tools, conversationHistory);

		while (ClaudeSystem.STOP_REASON_TOOL_USE.equals(getStopReason(response)))
		{
			// Run loop until AI has decided there is no reason to stop anymore.
			// The AI will request a stop if it needs a tool.
			List<Map<String, Object>> responseContent = getContent(response);
			Map<String, Object> toolUseBlock = findToolUseBlock(responseContent);

			if (toolUseBlock == null)
				break;

			conversationHistory.add(Map.of("role", "assistant", "content", responseContent));
			conversationHistory.add(buildToolResult(toolUseBlock));
			response = callClaude(headers, tools, conversationHistory);
		}

		String output = extractTextFromResponse(response);
		return new ConditionalObject<>(new ClaudeResponse("assistant", output));
	}

	private ResponseEntity<Map> callClaude(HttpHeaders headers, List<Map<String, Object>> tools, List<Map<String, Object>> history)
	{
		var body = Map.of(
				"model", ClaudeSystem.MODEL,
				"max_tokens", ClaudeSystem.MAX_TOKENS,
				"system", ClaudeSystem.PROMPT,
				"tools", tools,
				"messages", history
		);

		return restTemplate.postForEntity(ClaudeSystem.URL, new HttpEntity<>(body, headers), Map.class);
	}

	private Map<String, Object> buildToolResult(Map<String, Object> toolUseBlock)
	{
		try
		{
			String toolName = (String) toolUseBlock.get("name");
			String resultJson = resolveToolCall(toolName);

			return Map.of(
					"role", "user",
					"content", List.of(Map.of(
							"type", "tool_result",
							"tool_use_id", toolUseBlock.get("id"),
							"content", resultJson
					))
			);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("Failed to resolve tool call: " + ex.getMessage(), ex);
		}
	}

	private String resolveToolCall(String toolName) throws Exception
	{
		return switch (toolName)
		{
			case ClaudeSystem.TOOL_GET_ALL_EMPLOYEES -> objectMapper.writeValueAsString(employeeService.findAll());
			default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
		};
	}

	private HttpHeaders buildHeaders()
	{
		var headers = new HttpHeaders();
		headers.set("x-api-key", apiKey);
		headers.set("anthropic-version", "2023-06-01");
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private static Map<String, Object> buildEmployeeTool()
	{
		return Map.of(
				"name", ClaudeSystem.TOOL_GET_ALL_EMPLOYEES,
				"description", "Fetches all employees from the database",
				"input_schema", Map.of(
						"type", "object",
						"properties", Map.of(),
						"required", List.of()
				)
		);
	}

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> getContent(ResponseEntity<Map> response)
	{
		return (List<Map<String, Object>>) response.getBody().get("content");
	}

	private static String getStopReason(ResponseEntity<Map> response)
	{
		return (String) response.getBody().get("stop_reason");
	}

	private static Map<String, Object> findToolUseBlock(List<Map<String, Object>> content)
	{
		return content.stream()
				.filter(block -> "tool_use".equals(block.get("type")))
				.findFirst()
				.orElse(null);
	}

	@SuppressWarnings("unchecked")
	private static String extractTextFromResponse(ResponseEntity<Map> response)
	{
		var content = (List<Map<String, String>>) response.getBody().get("content");
		return content.get(0).get("text");
	}

	private static List<Map<String, Object>> createMessageChain(List<Message> messages)
	{
		var list = new ArrayList<Map<String, Object>>(messages.size());
		for (Message message : messages)
			list.add(Map.of("role", message.role(), "content", message.content()));
		return list;
	}
}

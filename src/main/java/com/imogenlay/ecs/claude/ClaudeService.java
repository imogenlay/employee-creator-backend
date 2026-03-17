package com.imogenlay.ecs.claude;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imogenlay.ecs.claude.dtos.ClaudeResponse;
import com.imogenlay.ecs.claude.dtos.Message;
import com.imogenlay.ecs.common.ConditionalObject;
import com.imogenlay.ecs.common.error.InternalServerException;
import com.imogenlay.ecs.contract.ContractService;
import com.imogenlay.ecs.employee.EmployeeService;
import com.imogenlay.ecs.employee.dtos.EmployeeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClaudeService
{
	@Value("${anthropic.api.key}")
	private String apiKey;

	private final ObjectMapper objectMapper;
	private final EmployeeService employeeService;
	private final ContractService contractService;
	private final RestTemplate restTemplate = new RestTemplate();

	public ClaudeService(ObjectMapper objectMapper, EmployeeService employeeService, ContractService contractService)
	{
		this.objectMapper = objectMapper;
		this.employeeService = employeeService;
		this.contractService = contractService;
	}

	public ConditionalObject<ClaudeResponse> ask(List<Message> messages)
	{
		var headers = ClaudeSystem.buildHeaders(apiKey);
		var tools = ClaudeSystem.buildAllTools();
		var conversationHistory = ClaudeSystem.createMessageChain(messages);

		ResponseEntity<Map> response = callClaude(headers, tools, conversationHistory);

		while (ClaudeSystem.claudeRequestsStop(response))
		{
			// The AI will request a stop if it needs a tool.
			// Run loop until AI has decided there is no reason to stop anymore.
			List<Map<String, Object>> responseContent = ClaudeSystem.getContent(response);
			Map<String, Object> toolUseBlock = ClaudeSystem.findToolUseBlock(responseContent);

			if (toolUseBlock == null)
				break;

			// Add the tool information given to the AI to conversation.
			conversationHistory.add(Map.of("role", "assistant", "content", responseContent));
			// Add tool response to conversation. Send that back to AI.
			conversationHistory.add(buildToolResult(toolUseBlock));
			response = callClaude(headers, tools, conversationHistory);
		}

		String output = ClaudeSystem.extractTextFromResponse(response);
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
			Map<String, Object> toolInput = (Map<String, Object>) toolUseBlock.get("input");
			String resultJson = resolveToolCall(toolName, toolInput);

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
			throw new InternalServerException("Failed to resolve tool call: " + ex.getMessage());
		}
	}

	private String resolveToolCall(String toolName, Map<String, Object> toolInput) throws Exception
	{
		List<Map<String, Object>> result = new ArrayList<>();

		switch (toolName)
		{
			case ClaudeSystem.TOOL_GET_ALL_EMPLOYEE_NAMES_AND_IDS:
			{
				List<EmployeeResponse> employees = employeeService.findAll();
				for (EmployeeResponse employee : employees)
				{
					Map<String, Object> next = new HashMap<>();
					next.put("id", employee.id());
					next.put("firstName", employee.firstName());
					next.put("middleName", employee.middleName());
					next.put("lastName", employee.lastName());
					result.add(next);
				}
				break;
			}
			case ClaudeSystem.TOOL_GET_EMPLOYEE_BY_ID:
			{
				Long employeeId = ((Integer) toolInput.get("id")).longValue();
				ConditionalObject<EmployeeResponse> employeeResponse = employeeService.findByIdResponse(employeeId);

				if (employeeResponse.hasError())
					return employeeResponse.getErrorMessage();

				Map<String, Object> employee = Map.of("employee", employeeResponse.getObject());
				result.add(employee);
				break;
			}
			case ClaudeSystem.TOOL_GET_ALL_CONTRACTS:
			{
				Map<String, Object> contracts = Map.of("contracts", contractService.findAll());
				result.add(contracts);
			}
			default:
				throw new InternalServerException("Unknown tool: " + toolName);
		}

		return objectMapper.writeValueAsString(result);
	}


}

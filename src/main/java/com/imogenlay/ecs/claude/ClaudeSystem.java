package com.imogenlay.ecs.claude;

import com.imogenlay.ecs.claude.dtos.Message;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClaudeSystem
{
	public static final String MODEL = "claude-haiku-4-5-20251001"; // Sonnet: claude-sonnet-4-20250514
	public static final int MAX_TOKENS = 1024;
	public static final String STOP_REASON_TOOL_USE = "tool_use";
	public final static String URL = "https://api.anthropic.com/v1/messages";
	public final static String PROMPT = """ 
			You are an assistant that is helping a user navigate an Employee Creator application.
			Your goal is to help the user access specific employee information with queries.
						
			You have access to a few tools that can access information about employees and other information.
			Tools can be used sequentially. Use multiple tools if needed.
									
			If questions are ambiguous in regards to subject matter, assume that the question relates to database information.
			Provide clear and comprehensive answers to all questions asked.
			Don't make assumptions about the data. No, somebody living near a lake does not mean they want to become a marine biologist.
			Attempt to keep a conversation going but offering further possibilities of tasks you could perform.
			Unless specifically asked for, you do not need to give an employee's ID when reading out their name.
						
			Responses are printed to a chat box as is. Do not attempt to use markdown formatting.
			The only exception is that a highlighted inline-block can be shown if
			surrounded with a single ` character on either side. The block is monospace and has an outline.""";


	public static final String TOOL_GET_ALL_EMPLOYEE_NAMES_AND_IDS = "get_all_employee_names_and_ids";
	public static final String TOOL_GET_EMPLOYEE_BY_ID = "get_employee_by_id";
	public static final String TOOL_GET_ALL_CONTRACTS = "get_all_contracts";
	private static List<Map<String, Object>> allTools = null;

	public static HttpHeaders buildHeaders(String apiKey)
	{
		var headers = new HttpHeaders();
		headers.set("x-api-key", apiKey);
		headers.set("anthropic-version", "2023-06-01");
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	public static List<Map<String, Object>> buildAllTools()
	{
		if (allTools == null)
			allTools = List.of(
					buildAllEmployeeNamesTool(),
					buildGetEmployeeByIdTool(),
					buildAllContractsTool()
			);

		return allTools;
	}

	private static Map<String, Object> buildAllEmployeeNamesTool()
	{
		return Map.of(
				"name", TOOL_GET_ALL_EMPLOYEE_NAMES_AND_IDS,
				"description", "Fetches all employee names and IDs from the database",
				"input_schema", Map.of(
						"type", "object",
						"properties", Map.of(),
						"required", List.of()
				)
		);
	}

	private static Map<String, Object> buildGetEmployeeByIdTool()
	{
		return Map.of(
				"name", TOOL_GET_EMPLOYEE_BY_ID,
				"description", "Fetches all information about an employee with ID",
				"input_schema", Map.of(
						"type", "object",
						"properties", Map.of(
								"id", Map.of(
										"type", "integer",
										"description", "The unique identifier of the employee"
								)
						),
						"required", List.of("id")
				)
		);
	}

	private static Map<String, Object> buildAllContractsTool()
	{
		return Map.of(
				"name", TOOL_GET_ALL_CONTRACTS,
				"description", "Fetches all contract names and IDs from the database",
				"input_schema", Map.of(
						"type", "object",
						"properties", Map.of(),
						"required", List.of()
				)
		);
	}

	public static List<Map<String, Object>> createMessageChain(List<Message> messages)
	{
		var list = new ArrayList<Map<String, Object>>(messages.size());
		int i = Math.max(0, messages.size() - 10);

		for (; i < messages.size(); i++)
			list.add(Map.of("role", messages.get(i).role(), "content", messages.get(i).content()));

		return list;
	}

	public static boolean claudeRequestsStop(ResponseEntity<Map> response)
	{
		return STOP_REASON_TOOL_USE.equals(response.getBody().get("stop_reason"));
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getContent(ResponseEntity<Map> response)
	{
		return (List<Map<String, Object>>) response.getBody().get("content");
	}

	public static Map<String, Object> findToolUseBlock(List<Map<String, Object>> content)
	{
		return content.stream()
				.filter(block -> "tool_use".equals(block.get("type")))
				.findFirst()
				.orElse(null);
	}

	@SuppressWarnings("unchecked")
	public static String extractTextFromResponse(ResponseEntity<Map> response)
	{
		var content = (List<Map<String, String>>) response.getBody().get("content");
		return content.get(0).get("text");
	}
}

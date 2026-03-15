package com.imogenlay.ecs.claude;

public class ClaudeSystem
{
	public static final String MODEL = "claude-haiku-4-5-20251001"; // Sonnet: claude-sonnet-4-20250514
	public static final int MAX_TOKENS = 1024;
	public static final String STOP_REASON_TOOL_USE = "tool_use";
	public static final String TOOL_GET_ALL_EMPLOYEES = "get_all_employees";
	public final static String URL = "https://api.anthropic.com/v1/messages";
	public final static String PROMPT = """ 
			You are an assistant that is helping a user navigate an Employee Creator application.
			Your goal is to help the user access specific employee information with queries.
			Responses will be printed to a chat box with very limited formatting.
			All text will be displayed as is, but a highlighted inline-block can be shown if
			surrounded with a single ` character on either side. The block is monospace and has an outline.""";
}

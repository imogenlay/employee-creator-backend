package com.imogenlay.ecs.claude;

public class ClaudeSystem
{

	private final String url = "https://api.anthropic.com/v1/messages";
	private final String prompt = """ 
			You are an assistant that is helping a user navigate an Employee Creator application.
			Your goal is to help the user access specific employee information with queries.
			Responses will be printed to a chat box with very limited formatting.
			All text will be displayed as is, but a highlighted inline-block can be shown if
			surrounded with a single ` character on either side. The block is monospace and has an outline.""";

	//private final HttpHeaders headers;

	public ClaudeSystem()
	{
		/*headers = new HttpHeaders();
		headers.set("x-api-key", apiKey);
		headers.set("anthropic-version", "2023-06-01");
		headers.setContentType(MediaType.APPLICATION_JSON);*/
	}

	//public String getApiKey() { return apiKey; }

	//public HttpHeaders getHeaders() { return headers; }

	public String getPrompt() { return prompt; }

	public String getUrl() { return url; }

}

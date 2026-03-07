package com.imogenlay.ecs;

import com.imogenlay.ecs.config.factory.EmployeeFactory;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class E2eBase
{
	@LocalServerPort
	private int port;

	@Autowired
	protected EmployeeFactory employeeFactory;
 
	protected RequestSpecification test()
	{
		var spec = new RequestSpecBuilder()
				.setPort(port)
				.setBaseUri("http://localhost")
				.setContentType(ContentType.JSON);

		return RestAssured.given(spec.build());
	}

	@BeforeEach
	void setUp()
	{
		RestAssured.port = port;
	}

	@AfterEach
	private void tearDown()
	{
		employeeFactory.clear();
	}
}

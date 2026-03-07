package com.imogenlay.ecs.e2e;

import com.imogenlay.ecs.E2eBase;
import com.imogenlay.ecs.config.factory.EmployeeFactoryOptions;
import com.imogenlay.ecs.employee.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Profile("test")
class EmployeeE2eTest extends E2eBase
{

	private Map<String, Object> createValidDTO()
	{
		Map<String, Object> dto = new HashMap<>();

		dto.put("firstName", "George");
		dto.put("middleName", "Mark");
		dto.put("lastName", "Carlson");
		dto.put("email", "george_mark@email.com");
		dto.put("mobile", "1800 9090 2");
		dto.put("address", "100 House Street VIC");
		dto.put("isFullTime", false);
		dto.put("hoursPerWeek", 30L);
		dto.put("startDate", "2027-01-01");
		dto.put("endDate", "2027-12-01");
		return dto;
	}

	@Test
	void getEmployee_returnsArrayWithCode200()
	{
		employeeFactory.createAndPersist(new EmployeeFactoryOptions().firstName("Sarah"));

		test()
				.when()
				.get("/employees")
				.then()
				.statusCode(200)
				.body(matchesJsonSchemaInClasspath("schemas/employee-response-list-schema.json"))
				.body("[0].firstName", equalTo("Sarah"));
	}

	@Test
	void getEmployees_FilterByNames_isCaseInsensitive_returnsMatch()
	{
		employeeFactory.createAndPersist(new EmployeeFactoryOptions()
				.firstName("Sarah")
				.middleName("")
				.lastName("Miller"));
		employeeFactory.createAndPersist(new EmployeeFactoryOptions()
				.firstName("Catherine")
				.middleName("")
				.lastName("Reed"));
		employeeFactory.createAndPersist(new EmployeeFactoryOptions()
				.firstName("Georgia")
				.middleName("")
				.lastName("Miller"));

		test()
				.when()
				.queryParam("order", "ASC")
				.queryParam("names", "miller")
				.get("/employees")
				.then()
				.statusCode(200)
				.body(matchesJsonSchemaInClasspath("schemas/employee-response-list-schema.json"))
				.body("[0].lastName", equalTo("Miller"))
				.body("[1].lastName", equalTo("Miller"));
	}

	@Test
	void postEmployee_validDto_returns201()
	{
		var dto = createValidDTO();

		test()
				.contentType("application/json")
				.body(dto)
				.when()
				.post("/employees")
				.then()
				.statusCode(201)
				.body(matchesJsonSchemaInClasspath("schemas/employee-response-schema.json"));
	}

	@Test
	void postEmployee_invalidDto_returns400()
	{
		Map<String, Object> dto = new HashMap<>();
		dto.put("firstName", "Bill");

		test()
				.contentType("application/json")
				.when()
				.body(dto)
				.post("/employees")
				.then()
				.statusCode(400)
				.body(matchesJsonSchemaInClasspath("schemas/exception-response-schema.json"))
				.body("details.lastName", equalTo("Last name is required"))
				.body("details.email", equalTo("Email is required"))
				.body("details.address", equalTo("Address is required"))
				.body("details.hoursPerWeek", equalTo("Hours per week is required"))
				.body("details.isFullTime", equalTo("Employment type (isFullTime) is required"));
	}

	@Test
	void postEmployee_invalidDto_MobileNumberIsLetters_returns400()
	{
		var dto = createValidDTO();
		dto.put("mobile", "NUMBER");

		test()
				.contentType("application/json")
				.when()
				.body(dto)
				.post("/employees")
				.then()
				.statusCode(400)
				.body("details.mobile", equalTo("Mobile number is invalid"));
	}

	@Test
	void postEmployee_invalidDto_EndDateIsBeforeStartDate_returns400()
	{
		var dto = createValidDTO();
		dto.put("endDate", "1999-09-09");

		test()
				.contentType("application/json")
				.when()
				.body(dto)
				.post("/employees")
				.then()
				.statusCode(400)
				.body("details.error", equalTo("Cannot create employee where end date is before start date"));
	}

	@Test
	void putEmployee_updateFirstName_returns200()
	{
		Employee employee = employeeFactory.createAndPersist(new EmployeeFactoryOptions().firstName("Sarah"));
		Map<String, Object> dto = new HashMap<>();
		dto.put("firstName", "   Bill    ");

		test()
				.contentType("application/json")
				.when()
				.body(dto)
				.put("/employees/" + employee.getId())
				.then()
				.statusCode(200)
				.body(matchesJsonSchemaInClasspath("schemas/employee-response-schema.json"))
				.body("firstName", equalTo("Bill"));
	}

	@Test
	void putEmployee_updateFirstName_IdDoesNotExist_returns404()
	{
		Employee employee = employeeFactory.createAndPersist(new EmployeeFactoryOptions().firstName("Sarah"));
		Map<String, Object> dto = new HashMap<>();
		dto.put("firstName", "  Jimm  ");
		long id = employee.getId() + 1;

		test()
				.contentType("application/json")
				.when()
				.body(dto)
				.put("/employees/" + id)
				.then()
				.statusCode(404)
				.body(matchesJsonSchemaInClasspath("schemas/exception-response-schema.json"))
				.body("details.error", equalTo("Employee with ID [" + id + "] does not exist"));
	}

	@Test
	void deleteEmployee_returns204()
	{
		Employee employee = employeeFactory.createAndPersist(new EmployeeFactoryOptions().firstName("Sarah"));

		test()
				.contentType("application/json")
				.when()
				.delete("/employees/" + employee.getId())
				.then()
				.statusCode(204);

		assertThat(employeeFactory.repoEmpty(), is(true));
	}

	@Test
	void deleteEmployee_employeeDoesNotExist_returns404()
	{
		Employee employee = employeeFactory.createAndPersist(new EmployeeFactoryOptions().firstName("Sarah"));
		long id = employee.getId().longValue() + 1L;

		test()
				.contentType("application/json")
				.when()
				.delete("/employees/" + id)
				.then()
				.statusCode(404)
				.body("details.error", equalTo("Employee with ID [" + id + "] does not exist"));

		assertThat(employeeFactory.repoEmpty(), is(false));
	}
}
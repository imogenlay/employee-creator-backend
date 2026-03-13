package com.imogenlay.ecs.e2e;

import com.imogenlay.ecs.E2eBase;
import com.imogenlay.ecs.config.factory.EmployeeFactoryOptions;
import com.imogenlay.ecs.contract.entity.Contract;
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
	private Map<String, Object> createValidDTO(long contractId)
	{
		Map<String, Object> dto = new HashMap<>();

		dto.put("firstName", "George");
		dto.put("middleName", "Mark");
		dto.put("lastName", "Carlson");
		dto.put("email", "george_mark@email.com");
		dto.put("mobile", "1800 9090 2");
		dto.put("address", "100 House Street VIC");
		dto.put("contractId", contractId);
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
				.queryParam("order", "ASC")
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
				.queryParam("names", "miller")
				.get("/employees")
				.then()
				.statusCode(200)
				.body(matchesJsonSchemaInClasspath("schemas/employee-response-list-schema.json"))
				.body("[0].lastName", equalTo("Miller"))
				.body("[1].lastName", equalTo("Miller"));
	}

	@Test
	void getContract_ordered_returnsArrayWithCode200()
	{
		employeeFactory.createAndPersistContract("Contract Type A", true);
		employeeFactory.createAndPersistContract("Contract Type B", true);

		// Order is descending by default.
		test()
				.when()
				.get("/contracts")
				.then()
				.body("[0].name", equalTo("Contract Type B"))
				.body("[1].name", equalTo("Contract Type A"));

		test()
				.when()
				.queryParam("order", "ASC")
				.get("/contracts")
				.then()
				.statusCode(200)
				.body(matchesJsonSchemaInClasspath("schemas/contract-response-list-schema.json"))
				.body("[0].name", equalTo("Contract Type A"))
				.body("[1].name", equalTo("Contract Type B"));
	}

	@Test
	void postEmployee_validDto_returns201()
	{
		Contract contract = employeeFactory.createAndPersistContract("Freelance Project", true);
		var dto = createValidDTO(contract.getId());

		test()
				.contentType("application/json")
				.body(dto)
				.when()
				.post("/employees")
				.then()
				.log().all()
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
				.body("details.hoursPerWeek", equalTo("Hours per week is required"));
	}

	@Test
	void postEmployee_invalidDto_MobileNumberIsLetters_returns400()
	{
		Contract contract = employeeFactory.createAndPersistContract("Freelance Project", true);
		var dto = createValidDTO(contract.getId());
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
		Contract contract = employeeFactory.createAndPersistContract("Enterprise Agreement 1999", false);
		var dto = createValidDTO(contract.getId());
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
	void putEmployee_updateFirstName_trimsName_returns200()
	{
		Employee employee = employeeFactory.createAndPersist(new EmployeeFactoryOptions().firstName("Sarah"));
		Map<String, Object> dto = new HashMap<>();
		dto.put("firstName", "   Bill    ");

		test()
				.contentType("application/json")
				.when()
				.body(dto)
				.patch("/employees/" + employee.getId())
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
		dto.put("firstName", "  Jim  ");
		long id = employee.getId() + 1;

		test()
				.contentType("application/json")
				.when()
				.body(dto)
				.patch("/employees/" + id)
				.then()
				.statusCode(404)
				.body(matchesJsonSchemaInClasspath("schemas/exception-response-schema.json"))
				.body("details.error", equalTo("Employee with ID [" + id + "] does not exist"));
	}

	@Test
	void putEmployee_updateContractId_IdDoesNotExist_returns404()
	{
		Employee employee = employeeFactory.createAndPersist(new EmployeeFactoryOptions().firstName("Sarah"));
		Map<String, Object> dto = new HashMap<>();
		dto.put("contractId", 333L);

		test()
				.contentType("application/json")
				.when()
				.body(dto)
				.patch("/employees/" + employee.getId())
				.then()
				.statusCode(400)
				.body(matchesJsonSchemaInClasspath("schemas/exception-response-schema.json"))
				.body("details.error", equalTo("Contract with ID [333] does not exist"));
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
		long id = employee.getId() + 1L;

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
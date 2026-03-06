package com.imogenlay.ecs.employee;

import com.imogenlay.ecs.common.ConditionalObject;
import com.imogenlay.ecs.common.SortOrder;
import com.imogenlay.ecs.employee.dtos.CreateEmployeeDto;
import com.imogenlay.ecs.employee.dtos.EmployeeQueryParams;
import com.imogenlay.ecs.employee.dtos.EmployeeResponse;
import com.imogenlay.ecs.employee.dtos.UpdateEmployeeDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController
{
	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService)
	{
		this.employeeService = employeeService;
	}

	@GetMapping
	public ResponseEntity<List<EmployeeResponse>> findAll(
			@RequestParam(required = false) List<String> names,
			@RequestParam(defaultValue = "DESC") SortOrder order)
	{
		EmployeeQueryParams params = new EmployeeQueryParams(names, order);

		Sort sort = Sort.by(
				params.orderOrDefault() == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC,
				"lastName"
		);

		return ResponseEntity.status(HttpStatus.OK).body(employeeService.findAll(params.names(), sort));
	}

	@PostMapping()
	public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeDto data)
	{
		ConditionalObject<EmployeeResponse> result = this.employeeService.create(data);
		if (result.hasError())
			result.throwError();

		return ResponseEntity.status(HttpStatus.CREATED).body(result.getObject());
	}

	@PutMapping("/{id}")
	public ResponseEntity<EmployeeResponse> update(
			@PathVariable Long id,
			@Valid @RequestBody UpdateEmployeeDto data)
	{
		ConditionalObject<EmployeeResponse> result = this.employeeService.update(id, data);
		if (result.hasError())
			result.throwError();

		return ResponseEntity.status(HttpStatus.OK).body(result.getObject());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id)
	{
		ConditionalObject<EmployeeResponse> result = this.employeeService.delete(id);
		if (result.hasError())
			result.throwError();

		return ResponseEntity.noContent().build();
	}
}

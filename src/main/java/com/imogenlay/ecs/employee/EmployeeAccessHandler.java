package com.imogenlay.ecs.employee;

import com.imogenlay.ecs.employee.dtos.EmployeeResponse;
import com.imogenlay.ecs.employee.entity.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EmployeeAccessHandler {

	private final EmployeeRepository employeeRepository;

	public EmployeeAccessHandler(EmployeeRepository employeeRepository) { this.employeeRepository = employeeRepository; }

	public List<Employee> findAll(Sort sort) {
		return employeeRepository.findAll(sort);
	}

	public List<EmployeeResponse> findAll(List<String> names, Sort sort) {
		List<Employee> employees;
		if (names == null || names.isEmpty())
			employees = findAll(sort);
		else {
			employees = findByEmployeeNameIgnoreCase(
					names.stream().map((c) -> c.toLowerCase()).toList(), sort);
		}

		return employees.stream().map((t) -> t.createResponse()).toList();
	}

	public Optional<Employee> findById(Long id) {
		return employeeRepository.findById(id);
	}

	public List<Employee> findByEmployeeNameIgnoreCase(List<String> names, Sort sort) {
		return employeeRepository.findDistinctWithNamesIgnoreCase(names, sort);
	}

	public void saveAndFlush(Employee employee) { employeeRepository.saveAndFlush(employee); }
	public void delete(Employee employee) { employeeRepository.delete(employee); }

	public void setEmployeeOnId(Long id, Employee employee) {
		Optional<Employee> result = findById(id);
		if (result.isPresent())
		{
			//Employee employee = result.get();
			//employee.set(employee);
		}
	}
}

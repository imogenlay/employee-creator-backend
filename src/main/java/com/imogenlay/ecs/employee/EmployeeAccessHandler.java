package com.imogenlay.ecs.employee;

import com.imogenlay.ecs.employee.dtos.EmployeeResponse;
import com.imogenlay.ecs.employee.entity.Employee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class EmployeeAccessHandler
{

	private final EmployeeRepository employeeRepository;

	public EmployeeAccessHandler(EmployeeRepository employeeRepository) { this.employeeRepository = employeeRepository; }

	public List<Employee> findAll(Sort sort)
	{
		return employeeRepository.findAll(sort);
	}

	public List<EmployeeResponse> findAll(List<String> names, Sort sort)
	{
		List<Employee> employees;
		if (names == null || names.isEmpty())
			employees = findAll(sort);
		else
		{
			employees = employeeRepository.findAll(nameStartsWith(names), sort);
		}

		return employees.stream().map((t) -> t.createResponse()).toList();
	}

	public Optional<Employee> findById(Long id)
	{
		return employeeRepository.findById(id);
	}

	/*public List<Employee> findByEmployeeNameIgnoreCase(List<String> names, Sort sort)
	{
		return employeeRepository.findDistinctWithNamesIgnoreCase(names, sort);
	}*/

	public void saveAndFlush(Employee employee) { employeeRepository.saveAndFlush(employee); }

	public void delete(Employee employee) { employeeRepository.delete(employee); }

	public void setEmployeeOnId(Long id, Employee employee)
	{
		Optional<Employee> result = findById(id);
		if (result.isPresent())
		{
			//Employee employee = result.get();
			//employee.set(employee);
		}
	}

	private Specification<Employee> nameStartsWith(List<String> names)
	{
		return (root, query, cb) -> {
			query.distinct(true);
			List<Predicate> predicates = new ArrayList<>();

			for (String name : names)
			{
				String escaped = name.toLowerCase()
						.replace("!", "!!")
						.replace("%", "!%")
						.replace("_", "!_");

				String pattern = "%" + escaped + "%";
				predicates.add(cb.or(
						cb.like(cb.lower(root.get("firstName")), pattern, '!'),
						cb.like(cb.lower(root.get("middleName")), pattern, '!'),
						cb.like(cb.lower(root.get("lastName")), pattern, '!')
				));
			}

			return cb.or(predicates.toArray(new Predicate[0]));
		};
	}
}

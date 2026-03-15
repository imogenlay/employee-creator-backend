package com.imogenlay.ecs.employee;

import com.imogenlay.ecs.contract.ContractRepository;
import com.imogenlay.ecs.contract.entity.Contract;
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
	private final ContractRepository contractRepository;

	public EmployeeAccessHandler(EmployeeRepository employeeRepository, ContractRepository contractRepository)
	{
		this.employeeRepository = employeeRepository;
		this.contractRepository = contractRepository;
	}

	public List<EmployeeResponse> findAll(Sort sort)
	{
		return employeeRepository.findAll(sort).stream().map((t) -> t.createResponse()).toList();
	}

	public List<EmployeeResponse> findAll(List<String> names, Sort sort)
	{
		if (names == null || names.isEmpty())
			return findAll(sort);
		else
			return employeeRepository.findAll(nameStartsWith(names), sort).stream().map((t) -> t.createResponse()).toList();
	}

	public Optional<Employee> findById(Long id)
	{
		return employeeRepository.findById(id);
	}

	public Optional<Contract> findContractById(Long id)
	{
		return contractRepository.findById(id);
	}

	public void saveAndFlush(Employee employee) { employeeRepository.saveAndFlush(employee); }

	public void delete(Employee employee) { employeeRepository.delete(employee); }

	public Optional<Employee> setContractOnId(Long id, Contract contract)
	{
		Optional<Employee> result = findById(id);
		if (result.isPresent())
		{
			Employee employee = result.get();
			employee.setContract(contract);
			employeeRepository.save(employee);
		}

		return result;
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

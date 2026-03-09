package com.imogenlay.ecs.employee;

import com.imogenlay.ecs.common.ConditionalObject;
import com.imogenlay.ecs.common.entity.IFullName;
import com.imogenlay.ecs.common.entity.Named;
import com.imogenlay.ecs.employee.dtos.ContractResponse;
import com.imogenlay.ecs.employee.dtos.CreateEmployeeDto;
import com.imogenlay.ecs.employee.dtos.EmployeeResponse;
import com.imogenlay.ecs.employee.dtos.UpdateEmployeeDto;
import com.imogenlay.ecs.employee.entity.Contract;
import com.imogenlay.ecs.employee.entity.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService
{
	private final EmployeeAccessHandler employeeAccessHandler;

	public EmployeeService(EmployeeAccessHandler employeeAccessHandler)
	{
		this.employeeAccessHandler = employeeAccessHandler;
	}

	public List<EmployeeResponse> findAll(List<String> employees, Sort sort)
	{
		return employeeAccessHandler.findAll(employees, sort);
	}

	public List<ContractResponse> findAllContracts(Sort sort)
	{
		return employeeAccessHandler.findAllContracts(sort);
	}

	public ConditionalObject<Employee> findById(Long id)
	{
		Optional<Employee> result = employeeAccessHandler.findById(id);
		if (result.isEmpty())
			return new ConditionalObject<>(HttpStatus.NOT_FOUND, "Employee with ID [" + id + "] does not exist");

		return new ConditionalObject<>(result.get());
	}

	public ConditionalObject<EmployeeResponse> create(CreateEmployeeDto data)
	{
		if (!fullNameIsValid(data, 2))
			return new ConditionalObject<>(HttpStatus.BAD_REQUEST, "Employee's full name must be more than 1 letter");
		if (!isEndDateValid(data.startDate(), data.endDate()))
			return new ConditionalObject<>(HttpStatus.BAD_REQUEST, "Cannot create employee where end date is before start date");
		Optional<Contract> resultContract = employeeAccessHandler.findContractById(data.contractId());
		if (resultContract.isEmpty())
			return new ConditionalObject<>(HttpStatus.BAD_REQUEST, "Contract with ID [" + data.contractId() + "] does not exist");

		Employee employee = createEmployeeFromData(data, resultContract.get());
		employeeAccessHandler.saveAndFlush(employee);
		return new ConditionalObject<>(employee.createResponse());
	}

	public ConditionalObject<EmployeeResponse> update(Long id, UpdateEmployeeDto data)
	{
		ConditionalObject<Employee> resultEmployee = findById(id);
		if (resultEmployee.hasError())
			return resultEmployee.copyError();

		Employee employee = resultEmployee.getObject();
		Named named = new Named(data, employee);
		if (!fullNameIsValid(named, 2))
			return new ConditionalObject<>(HttpStatus.BAD_REQUEST, "Employee's full name must be more than 1 letter");

		if (data.contractId() != null)
		{
			Optional<Contract> resultContract = employeeAccessHandler.findContractById(id);
			if (resultContract.isEmpty())
				return new ConditionalObject<>(HttpStatus.BAD_REQUEST, "Contract with ID [" + data.contractId() + "] does not exist");

			employee.setContract(resultContract.get());
		}

		employee.setFirstName(normaliseString(named.getFirstName()));
		employee.setMiddleName(normaliseString(named.getMiddleName()));
		employee.setLastName(normaliseString(named.getLastName()));
		if (data.email() != null)
			employee.setEmail(normaliseString(data.email()));
		if (data.mobile() != null)
			employee.setMobile(normaliseString(data.mobile()));
		if (data.address() != null)
			employee.setAddress(normaliseString(data.address()));
		if (data.hoursPerWeek() != null)
			employee.setHoursPerWeek(data.hoursPerWeek());
		if (data.startDate() != null)
			employee.setStartDate(data.startDate());
		if (data.endDate() != null)
			employee.setEndDate(data.endDate());
		employeeAccessHandler.saveAndFlush(employee);
		return new ConditionalObject<>(employee.createResponse());
	}

	public ConditionalObject<EmployeeResponse> delete(Long id)
	{
		ConditionalObject<Employee> result = findById(id);
		if (result.hasError())
			return result.copyError();

		Employee employee = result.getObject();
		employeeAccessHandler.delete(employee);
		return new ConditionalObject<>(employee.createResponse());
	}

	private boolean fullNameIsValid(IFullName fullName, int minimumLength)
	{
		String firstName = normaliseString(fullName.getFirstName());
		String middleName = normaliseString(fullName.getMiddleName());
		String lastName = normaliseString(fullName.getLastName());
		String newFullName = firstName + middleName + lastName;
		return normaliseString(newFullName).length() >= minimumLength;
	}

	private String normaliseString(String value)
	{
		if (value == null)
			return "";
		return value.trim();
	}

	public boolean isEndDateValid(LocalDate startDate, LocalDate endDate)
	{
		if (endDate == null)
			return true;
		return !endDate.isBefore(startDate);
	}

	private Employee createEmployeeFromData(CreateEmployeeDto data, Contract contract)
	{
		Employee employee = new Employee();
		employee.setFirstName(normaliseString(data.firstName()));
		employee.setMiddleName(normaliseString(data.middleName()));
		employee.setLastName(normaliseString(data.lastName()));
		employee.setEmail(normaliseString(data.email()));
		employee.setMobile(normaliseString(data.mobile()));
		employee.setAddress(normaliseString(data.address()));

		employee.setContract(contract);
		employee.setHoursPerWeek(data.hoursPerWeek());
		employee.setStartDate(data.startDate());
		employee.setEndDate(data.endDate());
		return employee;
	}
}

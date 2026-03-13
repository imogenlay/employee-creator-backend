package com.imogenlay.ecs.config.factory;

import com.imogenlay.ecs.contract.entity.Contract;

import java.time.LocalDate;

public class EmployeeFactoryOptions
{
	public String firstName;
	public String middleName;
	public String lastName;

	public String email;
	public String mobile;
	public String address;

	public Contract contract;
	public Long hoursPerWeek;

	public LocalDate startDate;
	public LocalDate endDate;

	public EmployeeFactoryOptions firstName(String firstName)
	{
		this.firstName = firstName;
		return this;
	}

	public EmployeeFactoryOptions middleName(String middleName)
	{
		this.middleName = middleName;
		return this;
	}

	public EmployeeFactoryOptions lastName(String lastName)
	{
		this.lastName = lastName;
		return this;
	}

	public EmployeeFactoryOptions email(String email)
	{
		this.email = email;
		return this;
	}

	public EmployeeFactoryOptions mobile(String mobile)
	{
		this.mobile = mobile;
		return this;
	}

	public EmployeeFactoryOptions address(String address)
	{
		this.address = address;
		return this;
	}

	public EmployeeFactoryOptions contract(Contract contract)
	{
		this.contract = contract;
		return this;
	}

	public EmployeeFactoryOptions hoursPerWeek(Long hoursPerWeek)
	{
		this.hoursPerWeek = hoursPerWeek;
		return this;
	}

	public EmployeeFactoryOptions startDate(LocalDate startDate)
	{
		this.startDate = startDate;
		return this;
	}

	public EmployeeFactoryOptions endDate(LocalDate endDate)
	{
		this.endDate = endDate;
		return this;
	}
}

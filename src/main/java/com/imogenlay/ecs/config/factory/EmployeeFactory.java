package com.imogenlay.ecs.config.factory;

import com.github.javafaker.Faker;
import com.imogenlay.ecs.contract.ContractRepository;
import com.imogenlay.ecs.contract.entity.Contract;
import com.imogenlay.ecs.employee.EmployeeRepository;
import com.imogenlay.ecs.employee.entity.Employee;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component
@Profile({ "dev", "test" })
public class EmployeeFactory
{
	private final EmployeeRepository employeeRepository;
	private final ContractRepository contractRepository;
	private final Faker faker = new Faker();
	private int numberTicker = 0;

	public EmployeeFactory(EmployeeRepository employeeRepository, ContractRepository contractRepository)
	{
		this.employeeRepository = employeeRepository;
		this.contractRepository = contractRepository;
	}

	public boolean repoEmpty()
	{
		return employeeRepository.count() == 0;
	}

	public boolean contractRepoEmpty()
	{
		return contractRepository.count() == 0;
	}

	private Employee create(EmployeeFactoryOptions options)
	{
		Employee employee = new Employee();
		employee.setFirstName(options.firstName != null ? options.firstName : faker.name().firstName());
		employee.setMiddleName(getMiddleName(options));
		employee.setLastName(options.lastName != null ? options.lastName : faker.name().lastName());

		employee.setEmail(getEmail(options, employee));
		employee.setMobile(getMobile(options));
		employee.setAddress(getAddress(options));

		employee.setContract(options.contract != null ? options.contract : getContract());
		employee.setHoursPerWeek(options.hoursPerWeek != null ? options.hoursPerWeek : (int) (Math.random() * 40));

		employee.setStartDate(getStartDate(options));
		employee.setEndDate(getEndDate(options));

		return employee;
	}

	private Employee create()
	{
		return create(new EmployeeFactoryOptions());
	}

	public Employee createAndPersist(EmployeeFactoryOptions options)
	{
		Employee employee = create(options);
		return employeeRepository.save(employee);
	}

	public Employee createAndPersist()
	{
		Employee employee = create();
		return employeeRepository.save(employee);
	}

	public Contract createAndPersistContract(String name, Boolean isFullTime)
	{
		Contract contract = new Contract();
		contract.setName(name);
		contract.setIsFullTime(isFullTime);
		return contractRepository.save(contract);
	}

	public void clear()
	{
		this.employeeRepository.deleteAll();
		this.contractRepository.deleteAll();
	}

	private String getMiddleName(EmployeeFactoryOptions options)
	{
		if (options.middleName != null)
			return options.middleName;

		if (Math.random() > 0.5)
			return null;
		return faker.name().firstName();
	}

	private String getEmail(EmployeeFactoryOptions options, Employee employee)
	{
		if (options.email != null)
			return options.email;

		return (employee.getFirstName() + "_" + employee.getLastName() + (numberTicker++) + "@email.com").toLowerCase();
	}

	private String getMobile(EmployeeFactoryOptions options)
	{
		if (options.mobile != null)
			return options.mobile;

		Random random = new Random();
		String number = "04";
		number += random.nextInt(10);
		number += random.nextInt(10);

		for (int i = 0; i < 2; i++)
		{
			number += " ";
			for (int j = 0; j < 3; j++)
				number += random.nextInt(10);
		}

		return number;
	}

	private String getAddress(EmployeeFactoryOptions options)
	{
		if (options.address != null)
			return options.address;

		Random random = new Random();
		String address = "";
		address += random.nextInt(222) + 1 + " ";
		address += faker.name().firstName() + " Street ";
		address += faker.address().cityName();
		if (Math.random() > 0.5)
			address += " VIC";
		else
			address += " NSW";

		return address;
	}

	private Contract getContract()
	{
		if (contractRepoEmpty())
		{
			Contract contract = new Contract();
			contract.setName("Unknown Contract");
			contractRepository.save(contract);
			return contract;
		}

		List<Contract> allContracts = contractRepository.findAll();
		return allContracts.get((int) (Math.random() * allContracts.size()));
	}

	private LocalDate getStartDate(EmployeeFactoryOptions options)
	{
		if (options.startDate != null)
			return options.startDate;

		return LocalDate.ofYearDay(2026, 1 + (int) (Math.random() * 360));
	}

	private LocalDate getEndDate(EmployeeFactoryOptions options)
	{
		if (options.endDate != null)
			return options.endDate;

		return LocalDate.ofYearDay(2027, 1 + (int) (Math.random() * 360));
	}
}

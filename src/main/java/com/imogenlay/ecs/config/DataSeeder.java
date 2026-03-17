package com.imogenlay.ecs.config;

import com.imogenlay.ecs.config.factory.EmployeeFactory;
import com.imogenlay.ecs.config.factory.EmployeeFactoryOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner
{
	private final EmployeeFactory employeeFactory;

	public DataSeeder(EmployeeFactory employeeFactory)
	{
		this.employeeFactory = employeeFactory;
	}

	@Override
	public void run(String... args) throws Exception
	{
		System.out.println("SEED NEW EMPLOYEES");
		if (employeeFactory.repoEmpty())
		{
			employeeFactory.createAndPersistContract("Permanent Full-Time", true);
			employeeFactory.createAndPersistContract("Permanent Part-Time", false);
			employeeFactory.createAndPersistContract("Contract Full-Time", true);
			employeeFactory.createAndPersistContract("Contract Part-Time", false);
		}

		if (employeeFactory.repoEmpty())
		{
			employeeFactory.createAndPersist(new EmployeeFactoryOptions().lastName("Aardy"));
			employeeFactory.createAndPersist(new EmployeeFactoryOptions().lastName("Zhao"));
			for (int i = 0; i < 10; i++)
				employeeFactory.createAndPersist();
		}
	}
}

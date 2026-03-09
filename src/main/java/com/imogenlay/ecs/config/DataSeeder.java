package com.imogenlay.ecs.config;

import com.imogenlay.ecs.config.factory.EmployeeFactory;
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
			employeeFactory.createAndPersistContract("Permanent Full-Time");
			employeeFactory.createAndPersistContract("Permanent Part-Time");
			employeeFactory.createAndPersistContract("Contract Full-Time");
			employeeFactory.createAndPersistContract("Contract Part-Time");
		}
 
		if (employeeFactory.repoEmpty())
			for (int i = 0; i < 15; i++)
				employeeFactory.createAndPersist();
	}
}

package com.imogenlay.ecs.common.entity;

public class Named implements IFullName
{
	private final String firstName;
	private final String middleName;
	private final String lastName;

	public Named(String firstName, String middleName, String lastName)
	{
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
	}

	public Named(IFullName priority, IFullName backup)
	{
		this.firstName = getString(priority.getFirstName(), backup.getFirstName());
		this.middleName = getString(priority.getMiddleName(), backup.getMiddleName());
		this.lastName = getString(priority.getLastName(), backup.getLastName());
	}

	private String getString(String a, String b)
	{
		return a == null || a == "" ? b : a;
	}

	@Override
	public String getFirstName() { return firstName; }

	@Override
	public String getMiddleName() { return middleName; }

	@Override
	public String getLastName() { return lastName; }
}

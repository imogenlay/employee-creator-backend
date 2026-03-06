package com.imogenlay.ecs.employee.entity;

import com.imogenlay.ecs.common.entity.BaseTimestampedEntity;
import com.imogenlay.ecs.common.entity.IFullName;
import com.imogenlay.ecs.common.entity.TimestampedEntityListener;
import com.imogenlay.ecs.employee.dtos.EmployeeResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@EntityListeners(TimestampedEntityListener.class)
public class Employee extends BaseTimestampedEntity implements IFullName
{

	private String firstName;
	private String middleName = "";
	private String lastName;

	private String email;
	private String mobile;
	private String address;

	private Boolean isFullTime;
	private Long hoursPerWeek;

	private LocalDate startDate;
	private LocalDate endDate;


	public Employee() { }

	public String getFirstName() { return firstName; }

	public void setFirstName(String firstName) { this.firstName = firstName; }

	public String getMiddleName() { return middleName; }

	public void setMiddleName(String middleName) { this.middleName = middleName; }

	public String getLastName() { return lastName; }

	public void setLastName(String lastName) { this.lastName = lastName; }

	public String getEmail() { return email; }

	public void setEmail(String email) { this.email = email; }

	public String getMobile() { return mobile; }

	public void setMobile(String mobile) { this.mobile = mobile; }

	public String getAddress() { return address; }

	public void setAddress(String address) { this.address = address; }

	public Boolean getIsFullTime() { return isFullTime; }

	public void setIsFullTime(Boolean isFullTime) { this.isFullTime = isFullTime; }

	public Long getHoursPerWeek() { return hoursPerWeek; }

	public void setHoursPerWeek(Long hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

	public LocalDate getStartDate() { return startDate; }

	public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

	public LocalDate getEndDate() { return endDate; }

	public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

	public EmployeeResponse createResponse()
	{
		return new EmployeeResponse(
				getId(),
				getFirstName(),
				getMiddleName(),
				getLastName(),
				getEmail(),
				getMobile(),
				getAddress(),
				getIsFullTime(),
				getHoursPerWeek(),
				getStartDate(),
				getEndDate());
	}
}
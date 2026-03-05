package com.imogenlay.ecs.employee;

import com.imogenlay.ecs.employee.entity.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long>
{
	@Query("""
        SELECT DISTINCT e
        FROM Employee e
        WHERE LOWER(e.firstName) IN :names
        OR LOWER(e.middleName) IN :names
        OR LOWER(e.lastName) IN :names
    """)
	List<Employee> findDistinctWithNamesIgnoreCase(
			@Param("names") List<String> names,
			Sort sort);

}

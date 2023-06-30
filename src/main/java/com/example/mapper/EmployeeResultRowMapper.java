package com.example.mapper;

import com.example.entity.Employee;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeResultRowMapper implements RowMapper<Employee> {
    @Override
    public Employee mapRow(ResultSet rs, int i) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employeeId"));
        employee.setLastName(rs.getString("lastName"));
        employee.setFirstName(rs.getString("firstName"));
        employee.setTitle(rs.getString("title"));
        return employee;
    }
}

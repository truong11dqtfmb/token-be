package com.dqt.token.services;


import com.dqt.token.dtos.request.EmployeeDTO;
import com.dqt.token.entities.Employee;
import com.dqt.token.exceptions.ResourceNotFoundException;
import com.dqt.token.repositories.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;


//    @Cacheable(value = "Employee")
    public List<EmployeeDTO> findAll() {
        log.info("Get All Employee from Database!");
        return this.employeeRepository.findAll().stream().map((employee -> this.modelMapper.map(employee, EmployeeDTO.class))).collect(Collectors.toList());
    }


    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        try {
            Employee employee = this.modelMapper.map(employeeDTO, Employee.class);
            employee.setEnabled(true);
            Employee createdEmployee = this.employeeRepository.save(employee);
            return this.modelMapper.map(createdEmployee, EmployeeDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


//    @CachePut(value = "Employee",key = "#id")
    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO, Long id) {
        try {
            Employee employee = this.convertDTOtoEntity(employeeDTO, this.findById(id));

            this.employeeRepository.save(employee);

            return this.modelMapper.map(employee, EmployeeDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


//    @CacheEvict(value = "Employee",key = "#id")
    public void deleteEmployee(Long id) {
        Employee employee = this.employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id + ""));

        employee.setEnabled(false);
    }


    public EmployeeDTO enabledEmployee(Long id) {
        Employee employee = this.employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id + ""));

        employee.setEnabled(true);

        return this.modelMapper.map(employee, EmployeeDTO.class);
    }


//    @Cacheable(value = "Employee",key = "#id")
    public Employee findById(Long id) {
        return this.employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id + ""));
    }


    public EmployeeDTO findDetailById(Long id) {
        return this.modelMapper.map(this.findById(id), EmployeeDTO.class);
    }


    private EmployeeDTO convertEntityToDTO(EmployeeDTO employeeDTO, Employee employee) {
        employeeDTO.setId(employee.getId());
        employeeDTO.setFullName(employee.getFullName());
        employeeDTO.setYear(employee.getYear());
        employeeDTO.setAddress(employee.getAddress());
        employeeDTO.setEmail(employee.getEmail());
        return employeeDTO;
    }

    private Employee convertDTOtoEntity(EmployeeDTO employeeDTO, Employee employee) {
        employee.setId(employeeDTO.getId());
        employee.setFullName(employeeDTO.getFullName());
        employee.setYear((Date) employeeDTO.getYear());
        employee.setAddress(employeeDTO.getAddress());
        employee.setEmail(employeeDTO.getEmail());
        return employee;
    }
}


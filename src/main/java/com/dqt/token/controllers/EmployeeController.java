package com.dqt.token.controllers;

import com.dqt.token.dtos.request.EmployeeDTO;
import com.dqt.token.dtos.response.ApiResponse;
import com.dqt.token.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/employees")
@RestController
@CrossOrigin("*")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    //    Employee - Find All Employees
    @GetMapping("/")
    public ResponseEntity<ApiResponse> findAllEmployees() {

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(true);
        apiResponse.setMessage("Get All Employees");

        apiResponse.setData(employeeService.findAll());

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    //    Employee - Find Detail Employees
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findDetailEmployees(@PathVariable("id") Long id) {

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(true);
        apiResponse.setMessage("Get Detail Employees: " + id);

        apiResponse.setData(employeeService.findDetailById(id));

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    //    Employee - Create Employee
    @PostMapping("/")
    public ResponseEntity<ApiResponse> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(true);
        apiResponse.setMessage("Create Employee");

        apiResponse.setData(employeeService.createEmployee(employeeDTO));

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    //    Employee - Update Employee
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEmployee(@Valid @RequestBody EmployeeDTO employeeDTO, @PathVariable("id") Long id) {

        return new ResponseEntity<>(new ApiResponse(true, "Update Employee: " + id, employeeService.updateEmployee(employeeDTO, id)), HttpStatus.OK);
    }

    //    Employee - Delete Employee
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable("id") Long id) {
        this.employeeService.deleteEmployee(id);
        return new ResponseEntity<>(new ApiResponse(true, "Delete Employee Successfully!"), HttpStatus.OK);
    }

    //    Employee - Enabled Employee
    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse> enabledEmployee(@PathVariable("id") Long id) {
        this.employeeService.enabledEmployee(id);

        return new ResponseEntity<>(new ApiResponse(true, "Enable Employee Successfully!", employeeService.enabledEmployee(id)), HttpStatus.OK);
    }


}

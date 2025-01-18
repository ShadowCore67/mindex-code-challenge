package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompensationService compensationService;

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }

    @GetMapping("/{id}")
    public ResponseEntity read(@PathVariable String id) {
        LOG.debug("Received employee read request for id [{}]", id);

        try {
            return ResponseEntity.status(HttpStatus.OK).body(employeeService.read(id));
        }
        catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee create request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }

    //Including these endpoints in the employee controller instead of a separate controller,
    //as these are still very much an employee-based queries in my opinion
    //Endpoints return ResponseEntities to allow for accurate error messaging
    @GetMapping("/reportStructure/{id}")
    public ResponseEntity getReportingStructure(@PathVariable String id) {
        LOG.debug("Retrieving reporting structure for id [{}]", id);

        try {
            return ResponseEntity.status(HttpStatus.OK).body(employeeService.getReportingStructure(id));
        }
        catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //Employee version
    @GetMapping("/reports/{id}")
    public ResponseEntity getEmployeeReportingStructure(@PathVariable String id) {
        LOG.debug("Retrieving reporting structure for id [{}]", id);

        try {
            return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeeReportStructure(id));
        }
        catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/compensation")
    public ResponseEntity createCompensation(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for [{}]", compensation);

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(compensationService.create(compensation));
        }
        catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/compensation/{id}")
    public ResponseEntity readCompensation(@PathVariable String id) {
        LOG.debug("Received compensation read request for id [{}]", id);

        try {
            return ResponseEntity.status(HttpStatus.OK).body(compensationService.read(id));
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

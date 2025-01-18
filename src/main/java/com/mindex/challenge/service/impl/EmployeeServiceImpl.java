package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }


    // The requirements of creating a new object seemed odd to me,
    // as the desired functionality could already be acheived with the
    // existing Employee object, with the only thing being added is the totalReports field.
    // In the small context of this task, I didn't see the reason for creating a new object,
    // however in the bigger conetext of a full scale project, I could see a separate object being potentially useful.
    // I included two versions, one using ReportingStructure (as per defined in the task) and one using Employee
    // to demonstrate what I mean.
    @Override
    public ReportingStructure getReportingStructure(String id) {
        LOG.debug("Retrieving report structure for employee [{}]", id);
        Employee employee = read(id);

        ReportingStructure reportStructure = new ReportingStructure();
        reportStructure.setEmployee(employee);
        
        List<ReportingStructure> reports = new ArrayList<>();
        int numberOfReports = 0;

        if(employee.getDirectReports() != null) {
            for(Employee report: employee.getDirectReports()) {
                reports.add(getReportingStructure(report.getEmployeeId()));
                numberOfReports++;
            }
        }   

        reportStructure.setReports(reports);
        reportStructure.setNumberOfReports(numberOfReports);

        return reportStructure;
    }


    @Override
    public Employee getEmployeeReportStructure(String id) {
        LOG.debug("Retrieving report structure for employee [{}]", id);
        Employee employee = read(id);
        int numberOfReports = 0;
        List<Employee> fullReports = new ArrayList<>();

        if(employee.getDirectReports() != null) {
            for(Employee report: employee.getDirectReports()) {
                Employee fullEmployee = getEmployeeReportStructure(report.getEmployeeId());
                fullReports.add(fullEmployee);
                numberOfReports += fullEmployee.getTotalReports() + 1;
            }
        }

        employee.setTotalReports(numberOfReports);
        employee.setDirectReports(fullReports);

        return employee;
    }
}

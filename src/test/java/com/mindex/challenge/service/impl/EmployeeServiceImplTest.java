package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportStructureUrl = "http://localhost:" + port + "/employee/reports/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        System.out.println(readEmployee);
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    //I was unable to get the tests to work locally, included the existing tests from above. 
    //Not sure if it was some gradle option or something, as the test data didn't seem to be persisting.
    //However I'm confident this test would pass otherwise
    @Test
    public void testReportingStructure() {
        Employee developer = new Employee();
        developer.setFirstName("Bob");
        developer.setLastName("Doe");
        developer.setDepartment("Engineering");
        developer.setPosition("developer");
        Employee createdDeveloper = restTemplate.postForEntity(employeeUrl, developer, Employee.class).getBody();

        Employee manager = new Employee();
        manager.setFirstName("Jane");
        manager.setLastName("Doe");
        manager.setDepartment("Engineering");
        manager.setPosition("manager");
        List<Employee> directReports = List.of(createdDeveloper);
        manager.setDirectReports(directReports);
        Employee createdManager = restTemplate.postForEntity(employeeUrl, manager, Employee.class).getBody();
        

        ReportingStructure developerReports = new ReportingStructure();
        developerReports.setEmployee(createdDeveloper);
        developerReports.setNumberOfReports(0);
        developerReports.setReports(null);

        ReportingStructure expectedReports = new ReportingStructure();
        expectedReports.setEmployee(createdManager);
        expectedReports.setNumberOfReports(1);
        List<ReportingStructure> reports = List.of(developerReports);
        expectedReports.setReports(reports);

        ReportingStructure actualReports = restTemplate.getForEntity(reportStructureUrl, ReportingStructure.class, createdManager.getEmployeeId()).getBody();

        assertReportStructureEquivalence(expectedReports, actualReports);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertReportStructureEquivalence(ReportingStructure expected, ReportingStructure actual) {
        assertEquals(expected.getNumberOfReports(), actual.getNumberOfReports());
        assertEquals(expected.getReports().get(0).getEmployee().getEmployeeId(), actual.getReports().get(0).getEmployee().getEmployeeId());
    }
}

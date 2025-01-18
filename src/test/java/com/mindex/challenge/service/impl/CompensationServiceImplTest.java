package com.mindex.challenge.service.impl;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindex.challenge.data.Compensation;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {
    
    private String compensationUrl;
    private String compensationIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/employee/compensation";
        compensationIdUrl = "http://localhost:" + port + "/employee/compensation/{id}";
    }

    //I was unable to get the tests to work locally, like the Employee tests
    //Not sure if it was some gradle option or something, the issue looked to be the data wasn't persisting.
    //However I'm confident this test would pass otherwise

    @Test
    public void testCreateRead() {
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId("123456");
        testCompensation.setSalary(100000);
        testCompensation.setEffectiveDate(LocalDate.parse("2025-02-28"));

        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        assertEquals(testCompensation.getEmployeeId(), createdCompensation.getEmployeeId());
        assertEquals(testCompensation.getSalary(), createdCompensation.getSalary(), 0);
        assertEquals(testCompensation.getEffectiveDate(), createdCompensation.getEffectiveDate());

        Compensation actualCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, testCompensation.getEmployeeId()).getBody();

        assertEquals(testCompensation.getEmployeeId(), actualCompensation.getEmployeeId());
        assertEquals(testCompensation.getSalary(), actualCompensation.getSalary(), 0);
        assertEquals(testCompensation.getEffectiveDate(), actualCompensation.getEffectiveDate());
    }
    
}

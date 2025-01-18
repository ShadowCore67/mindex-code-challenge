package com.mindex.challenge.data;

import java.time.LocalDate;

public class Compensation {
    private String employeeId;
    private double salary;
    private LocalDate effectiveDate;

    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(LocalDate effectivDate) {
        this.effectiveDate = effectivDate;
    }
}



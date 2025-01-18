package com.mindex.challenge.data;

import java.util.List;

public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;
    private List<ReportingStructure> reports;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }

    public List<ReportingStructure> getReports() {
        return reports;
    }

    public void setReports(List<ReportingStructure> reports) {
        this.reports = reports;
    }

}

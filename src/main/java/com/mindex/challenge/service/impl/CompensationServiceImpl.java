package com.mindex.challenge.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        //This logic has some grey area due to the openess of the task. I've designed it to allow for future effective dates,
        //and then added checking to prevent from adding effective dates that come before the most recent as well as
        //preventing dates being set in the past. A lot of this logic could change based on customer requirements,
        //they may not need future dates, or would need the ability to delete future dates, or update existing ones.
        if(compensation.getEffectiveDate() == null) {
            compensation.setEffectiveDate(LocalDate.now());
        }
        else {
            if(compensation.getEffectiveDate().isBefore(LocalDate.now())) {
                throw new RuntimeException("Effective date cannot be set in the past");
            }
        }

        List<Compensation> currentCompensations = new ArrayList<>();
        try {
            currentCompensations = read(compensation.getEmployeeId());
        }
        catch(RuntimeException e) {
            currentCompensations = null;
        }

        if(currentCompensations != null && !currentCompensations.isEmpty()) {
            if(currentCompensations.get(0).getEffectiveDate().isAfter(compensation.getEffectiveDate())) {
                throw new RuntimeException("Effective date must be set after " + currentCompensations.get(0).getEffectiveDate() + " due to existing compensations");
            }
        }

        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public List<Compensation> read(String employeeId) {
        LOG.debug("Retrieving compensation for employee [{}]", employeeId);
        List<Compensation> compensations = compensationRepository.findCompensationByEmployeeIdOrderByEffectiveDateDesc(employeeId);

        if(compensations == null || compensations.isEmpty()) {
            throw new RuntimeException("No compensations found for employee " + employeeId);
        }

        return compensations;
    }
}

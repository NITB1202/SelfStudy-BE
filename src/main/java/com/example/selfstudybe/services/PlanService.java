package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.Plan.CreatePlanDto;
import com.example.selfstudybe.dtos.Plan.PlanDto;
import com.example.selfstudybe.models.Plan;
import com.example.selfstudybe.repositories.PlanRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;

    private PlanDto PlanToDto(Plan plan) {
        return new PlanDto(
          plan.getId(),
          plan.getName(),
          plan.getStartDate(),
          plan.getEndDate(),
          plan.getNotifyBefore(),
          plan.getStatus()
        );
    }

    public PlanDto createPlan(CreatePlanDto plan) {
        Plan newPlan = new Plan();
        newPlan.setName(plan.getName());
        newPlan.setStartDate(plan.getStartDate());
        newPlan.setEndDate(plan.getEndDate());
        if(plan.getNotifyBefore() != null)
            newPlan.setNotifyBefore(plan.getNotifyBefore());

        return PlanToDto(planRepository.save(newPlan));
    }

    //View plan progress

    //Delete

}

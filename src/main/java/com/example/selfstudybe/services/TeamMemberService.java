package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.Team.*;
import com.example.selfstudybe.enums.TeamRole;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.*;
import com.example.selfstudybe.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TeamMemberService {
    private final UserTeamRepository userTeamRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final PlanUserRepository planUserRepository;
    private final TeamPlanRepository teamPlanRepository;

    @Transactional
    public void addTeamMember(AddTeamMemberDto addTeamMemberDto) {
        Team team = teamRepository.findById(addTeamMemberDto.getTeamId()).orElseThrow(
                () -> new CustomNotFoundException("Can't find team with id " + addTeamMemberDto.getTeamId())
        );


        for(UUID memberId : addTeamMemberDto.getMemberIds()) {
            User user = userRepository.findById(memberId).orElseThrow(
                    () -> new CustomNotFoundException("Can't find user with id " + memberId)
            );

            UserTeamId id = new UserTeamId();
            id.setTeamId(addTeamMemberDto.getTeamId());
            id.setUserId(memberId);

            UserTeam userTeam = new UserTeam();
            userTeam.setId(id);
            userTeam.setTeam(team);
            userTeam.setUser(user);

            userTeam.setRole(TeamRole.MEMBER);

            userTeamRepository.save(userTeam);
        }

        team.setNum(team.getNum() + addTeamMemberDto.getMemberIds().size());
        teamRepository.save(team);
    }

    public void updateTeamMemberRole(UpdateMemberRoleDto updateMember) {
        UserTeamId  id = new UserTeamId();
        id.setUserId(updateMember.getUserId());
        id.setTeamId(updateMember.getTeamId());

        UserTeam userTeam = userTeamRepository.findById(id).orElseThrow(
                ()-> new CustomNotFoundException("Invalid team id or user id")
        );

        if(updateMember.getRole() == TeamRole.CREATOR)
            throw new CustomBadRequestException("Can't set role to CREATOR");

        userTeam.setRole(updateMember.getRole());
        userTeamRepository.save(userTeam);
    }

    public String getUserRole(UUID teamId, UUID userId) {
        UserTeamId  id = new UserTeamId();
        id.setUserId(userId);
        id.setTeamId(teamId);

        UserTeam userTeam = userTeamRepository.findById(id).orElseThrow(
                ()-> new CustomNotFoundException("Invalid team id or user id")
        );

        return userTeam.getRole().toString();
    }

    @Transactional
    public void removeTeamMember(RemoveTeamMemberDto removeTeamMemberDto) {
        Team team = teamRepository.findById(removeTeamMemberDto.getTeamId()).orElseThrow(
                ()-> new CustomNotFoundException("Can't find team with id " + removeTeamMemberDto.getTeamId())
        );

        for(UUID memberId : removeTeamMemberDto.getMemberIds()) {
            UserTeamId  id = new UserTeamId();
            id.setUserId(memberId);
            id.setTeamId(removeTeamMemberDto.getTeamId());

            UserTeam userTeam = userTeamRepository.findById(id).orElseThrow(
                    ()-> new CustomNotFoundException("Invalid team id or user id")
            );

            if(userTeam.getRole() == TeamRole.CREATOR) continue;

            userTeamRepository.delete(userTeam);
        }

        team.setNum(team.getNum() - removeTeamMemberDto.getMemberIds().size());
        teamRepository.save(team);
    }

    @Transactional
    public void assignTeamMember(AssignMemberDto assignMember) {
        Plan plan = planRepository.findById(assignMember.getPlanId()).orElseThrow(
                ()-> new CustomNotFoundException("Can't find plan with id " + assignMember.getPlanId())
        );

        // Check if the plan is team's plan
        if(plan.getPersonal())
            throw new CustomBadRequestException("Can't assign member to a personal plan");

        TeamPlan teamPlan = teamPlanRepository.findByPlan(plan);
        UUID teamId = teamPlan.getTeam().getId();

        for(UUID memberId : assignMember.getUserIds())
        {
            UserTeamId  userTeamId = new UserTeamId();
            userTeamId.setTeamId(teamId);
            userTeamId.setUserId(memberId);

            // Check if the user is in the team
            if(!userTeamRepository.existsById(userTeamId))
                throw new CustomBadRequestException("This user is not in the team");

            User user = userRepository.findById(memberId).orElseThrow(
                   ()->new CustomNotFoundException("Can't find user with id " + memberId)
            );

            PlanUserId  id = new PlanUserId();
            id.setPlanId(assignMember.getPlanId());
            id.setAssigneeId(memberId);

            PlanUser planUser = new PlanUser();
            planUser.setId(id);
            planUser.setPlan(plan);
            planUser.setAssignee(user);

            planUserRepository.save(planUser);
        }
    }
}

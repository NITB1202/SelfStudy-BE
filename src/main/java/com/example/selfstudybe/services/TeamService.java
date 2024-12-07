package com.example.selfstudybe.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.selfstudybe.dtos.Team.CreateTeamDto;
import com.example.selfstudybe.dtos.Team.TeamDto;
import com.example.selfstudybe.dtos.Team.TeamMemberDto;
import com.example.selfstudybe.dtos.Team.UpdateTeamDto;
import com.example.selfstudybe.enums.TeamRole;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.models.*;
import com.example.selfstudybe.repositories.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final Cloudinary cloudinary;
    private final PlanRepository planRepository;
    private final SubjectService subjectService;
    private final TeamPlanRepository teamPlanRepository;
    private final TeamSubjectRepository teamSubjectRepository;
    private final ModelMapper modelMapper;
    private final TeamMemberService teamMemberService;

    public TeamDto createNewTeam(CreateTeamDto request) {
        User user = userRepository.findById(request.getCreatorId()).orElseThrow(
                ()-> new CustomBadRequestException("Can't find user with id " + request.getCreatorId())
        );

        // Check duplicated
        if(teamRepository.existsByNameAndCreator(request.getName(),user))
            throw new CustomBadRequestException("Team with name " + request.getName() + " already exists");

        // Save team
        Team team = modelMapper.map(request, Team.class);
        team.setCreator(user);
        team.setCreatedAt(LocalDateTime.now());
        team.setNum(1);
        team.setUsage(0f);

        Team savedTeam = teamRepository.save(team);

        // Save team member
        UserTeamId userTeamId = new UserTeamId();
        userTeamId.setTeamId(savedTeam.getId());
        userTeamId.setUserId(user.getId());

        UserTeam userTeam = new UserTeam();
        userTeam.setId(userTeamId);
        userTeam.setUser(user);
        userTeam.setTeam(savedTeam);
        userTeam.setRole(TeamRole.CREATOR);

        userTeamRepository.save(userTeam);

        return modelMapper.map(savedTeam, TeamDto.class);
    }

    public List<TeamDto> getAllTeamsForUser(UUID userId) {
        List<UserTeam> userTeams = userTeamRepository.findByUserId(userId);
        List<Team> teams = userTeams.stream().map(UserTeam::getTeam).toList();
        return modelMapper.map(teams, new TypeToken<List<TeamDto>>() {}.getType());
    }

    public List<TeamMemberDto> getAllTeamMembers(UUID teamId, TeamRole teamRole) {
        List<UserTeam> userTeams;

        if(teamRole == null)
            userTeams = userTeamRepository.findByTeamId(teamId);
        else
            userTeams = userTeamRepository.findByTeamIdAndRole(teamId,teamRole);

        List<User> users = userTeams.stream().map(UserTeam::getUser).toList();

        return teamMemberService.membersMapper(users,userTeams);
    }

    public String uploadTeamImage(UUID teamId, MultipartFile file) throws IOException {
        Team team = teamRepository.findById(teamId).orElseThrow(
                ()-> new CustomBadRequestException("Can't find team with id " + teamId)
        );

        Map params = ObjectUtils.asMap(
                "resource_type", "auto",
                "public_id", teamId.toString(),
                "asset_folder", "Team",
                "overwrite", true
        );

        Map result = cloudinary.uploader().upload(file.getBytes(),params);
        String url = result.get("secure_url").toString();

        team.setImageLink(url);
        teamRepository.save(team);

        return url;
    }

    public TeamDto updateTeam(UpdateTeamDto request) {
        Team team = teamRepository.findById(request.getTeamId()).orElseThrow(
                ()-> new CustomBadRequestException("Can't find team with id " + request.getTeamId())
        );

        modelMapper.map(request, team);
        teamRepository.save(team);

        return modelMapper.map(team, TeamDto.class);
    }

    public void deleteTeam(UUID teamId) throws Exception {
        Team team = teamRepository.findById(teamId).orElseThrow(
                ()-> new CustomBadRequestException("Can't find team with id " + teamId)
        );

        //Delete image
        if(team.getImageLink() != null)
            cloudinary.uploader().destroy(teamId.toString(),ObjectUtils.emptyMap());

        // Delete team's plans
        List<TeamPlan> teamPlans = teamPlanRepository.findByTeam(team);
        List<Plan> plans = teamPlans.stream().map(TeamPlan::getPlan).toList();

        teamPlanRepository.deleteAll(teamPlans);
        planRepository.deleteAll(plans);

        //Delete team's subjects
        List<TeamSubject> teamSubjects = teamSubjectRepository.findByTeam(team);
        List<Subject> subjects = teamSubjects.stream().map(TeamSubject::getSubject).toList();

        teamSubjectRepository.deleteAll(teamSubjects);
        for(Subject subject : subjects)
            subjectService.deleteSubject(subject.getId());

        teamRepository.delete(team);
    }
}

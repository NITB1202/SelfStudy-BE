package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.Invitation.InvitationDto;
import com.example.selfstudybe.dtos.Invitation.SendInvitationDto;
import com.example.selfstudybe.dtos.Notification.NotificationDto;
import com.example.selfstudybe.dtos.Team.AddTeamMemberDto;
import com.example.selfstudybe.enums.Response;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.Invitation;
import com.example.selfstudybe.models.Team;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.models.UserTeamId;
import com.example.selfstudybe.repositories.InvitationRepository;
import com.example.selfstudybe.repositories.TeamRepository;
import com.example.selfstudybe.repositories.UserRepository;
import com.example.selfstudybe.repositories.UserTeamRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final TeamMemberService teamMemberService;

    public InvitationDto sendInvitation(SendInvitationDto invitationDto) {
        Team team = teamRepository.findById(invitationDto.getTeamId()).orElseThrow(
                ()-> new CustomNotFoundException("Can't find team with id " + invitationDto.getTeamId())
        );

        User user = userRepository.findById(invitationDto.getUserId()).orElseThrow(
                ()-> new CustomNotFoundException("Can't find user with id " + invitationDto.getUserId())
        );

        UserTeamId id = new UserTeamId();
        id.setTeamId(team.getId());
        id.setUserId(user.getId());

        if(userTeamRepository.existsById(id))
            throw new CustomBadRequestException("This uses already in the team");

        Invitation invitation = new Invitation();
        invitation.setTeam(team);
        invitation.setUser(user);
        invitation.setCreatedAt(LocalDateTime.now());

        Invitation savedInvitation = invitationRepository.save(invitation);

        return modelMapper.map(savedInvitation, InvitationDto.class);
    }

    public List<InvitationDto> getAllInvitations(UUID userId) {
        List<Invitation> invitations = invitationRepository.findByUserId(userId);
        return modelMapper.map(invitations, new TypeToken<List<InvitationDto>>() {}.getType());
    }

    public InvitationDto respondInvitation(UUID invitationId, Response response) {
        Invitation invitation = invitationRepository.findById(invitationId).orElseThrow(
                ()-> new CustomNotFoundException("Can't find invitation with id " + invitationId)
        );

        // Save the invitation
        invitation.setResponse(response);
        invitationRepository.save(invitation);

        // Add user to the team if the response is ACCEPT
        if(response.equals(Response.ACCEPT)) {
            Hibernate.initialize(invitation.getTeam());
            Hibernate.initialize(invitation.getUser());

            UUID teamId = invitation.getTeam().getId();
            List<UUID> userIds = List.of(invitation.getUser().getId());

            AddTeamMemberDto teamMemberDto = new AddTeamMemberDto(teamId, userIds);
            teamMemberService.addTeamMember(teamMemberDto);
        }

        return modelMapper.map(invitation, InvitationDto.class);
    }
}

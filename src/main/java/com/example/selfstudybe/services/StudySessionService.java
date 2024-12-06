package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.StudySession.CreateStudySessionDto;
import com.example.selfstudybe.dtos.StudySession.StudySessionDto;
import com.example.selfstudybe.enums.SessionStatus;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.models.StudySession;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.StudySessionRepository;
import com.example.selfstudybe.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class StudySessionService {
    private final StudySessionRepository studySessionRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public StudySessionDto createSession(CreateStudySessionDto sessionDto) {
        User user = userRepository.findById(sessionDto.getUserId()).orElseThrow(
                () -> new CustomBadRequestException("Can't find user with id "+ sessionDto.getUserId())
        );

        if(sessionDto.getTotalTime().isBefore(sessionDto.getEndTime()))
            throw new CustomBadRequestException("End time exceeds total time");

        StudySession session = modelMapper.map(sessionDto, StudySession.class);
        session.setUser(user);
        session.setDateCreate(LocalDate.now());
        if(session.getEndTime().equals(session.getTotalTime()))
            session.setStatus(SessionStatus.COMPLETE);
        else
            session.setStatus(SessionStatus.INCOMPLETE);

        StudySession savedSession = studySessionRepository.save(session);

        return modelMapper.map(savedSession, StudySessionDto.class);
    }
}

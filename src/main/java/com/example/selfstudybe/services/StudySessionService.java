package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.StudySession.CreateStudySessionDto;
import com.example.selfstudybe.dtos.StudySession.StudySessionDto;
import com.example.selfstudybe.dtos.StudySession.UpdateStudySessionDto;
import com.example.selfstudybe.enums.SessionStatus;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.StudySession;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.StudySessionRepository;
import com.example.selfstudybe.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StudySessionService {
    private final StudySessionRepository studySessionRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public StudySessionDto createSession(CreateStudySessionDto createStudySessionDto) {
        User user = userRepository.findById(createStudySessionDto.getUserId()).orElseThrow(
                () -> new CustomBadRequestException("Can't find user with id "+createStudySessionDto.getUserId())
        );

        StudySession session = modelMapper.map(createStudySessionDto, StudySession.class);
        session.setUser(user);
        session.setUpdateAt(LocalDateTime.now());

        long focusTime = createStudySessionDto.getFocusTime().toSecondOfDay();
        long breakTime = createStudySessionDto.getBreakTime().toSecondOfDay();
        LocalTime totalTime = LocalTime.ofSecondOfDay((focusTime + breakTime)*createStudySessionDto.getTotalStage());
        session.setTotalTime(totalTime);

        session.setCurrentStage(0);
        session.setTimeLeft(createStudySessionDto.getFocusTime());

        StudySession savedSession = studySessionRepository.save(session);

        return modelMapper.map(savedSession, StudySessionDto.class);
    }

    public StudySessionDto getStudySession(UUID userId) {
        if(!userRepository.existsById(userId))
            throw new CustomNotFoundException("Can't find user with id: " + userId);

        List<StudySession> sessions = studySessionRepository.findByUserIdAndStatus(userId, SessionStatus.PAUSED);

        Optional<StudySession> studySession = sessions.stream().max(Comparator.comparing(StudySession::getUpdateAt));
        if(studySession.isPresent()) {
            return modelMapper.map(studySession.get(), StudySessionDto.class);
        }

        throw new CustomNotFoundException("This user has not yet created a study session.");
    }

    public StudySessionDto updateSession(UpdateStudySessionDto updateStudySessionDto) {
        StudySession studySession = studySessionRepository.findById(updateStudySessionDto.getId()).orElseThrow(
                ()-> new CustomNotFoundException("Can't find study session with id: " + updateStudySessionDto.getId())
        );

        if(updateStudySessionDto.getTimeLeft().isAfter(studySession.getFocusTime()))
            throw new CustomBadRequestException("Invalid time left");

        if(updateStudySessionDto.getCurrentStage() > studySession.getTotalStage())
            throw new CustomBadRequestException("Invalid current stage");

        modelMapper.map(updateStudySessionDto, studySession);
        studySession.setUpdateAt(LocalDateTime.now());

        return modelMapper.map(studySessionRepository.save(studySession), StudySessionDto.class);
    }
}

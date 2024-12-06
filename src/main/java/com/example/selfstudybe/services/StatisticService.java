package com.example.selfstudybe.services;

import com.example.selfstudybe.enums.PlanStatus;
import com.example.selfstudybe.enums.SessionStatus;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.Plan;
import com.example.selfstudybe.models.PlanUser;
import com.example.selfstudybe.models.StudySession;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.PlanRepository;
import com.example.selfstudybe.repositories.PlanUserRepository;
import com.example.selfstudybe.repositories.StudySessionRepository;
import com.example.selfstudybe.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StatisticService {

    private final PlanRepository planRepository;
    private final PlanUserRepository planUserRepository;
    private final UserRepository userRepository;
    private final StudySessionRepository studySessionRepository;

    public int getFinishedPlanInWeek(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new CustomNotFoundException("Can't find user with id " + userId)
        );

        List<PlanUser> planUsers = planUserRepository.findByAssignee(user);
        List<Plan> plans = planUsers.stream().map(PlanUser::getPlan).toList();

        int finishedPlanInWeek = 0;

        for (Plan plan : plans) {
            if(inWeek(plan.getCompleteDate()))
                finishedPlanInWeek++;
        }

        return finishedPlanInWeek;
    }

    public LocalTime focusTimeInWeek(UUID userId) {
        if(!userRepository.existsById(userId))
            throw new CustomNotFoundException("Can't find user with id " + userId);

        List<StudySession> sessions = studySessionRepository.findByUserId(userId);
        LocalTime totalTime = LocalTime.of(0, 0, 0);

        for (StudySession session : sessions) {
            LocalDateTime time = LocalDateTime.of(session.getDateCreate(), LocalTime.MIN);
            if(inWeek(time)) {
                long totalSeconds = totalTime.toSecondOfDay() + session.getEndTime().toSecondOfDay();
                totalTime = LocalTime.ofSecondOfDay(totalSeconds);
            }
        }

        return totalTime;
    }

    public float levelOfCompletion(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new CustomNotFoundException("Can't find user with id " + userId)
        );

        List<PlanUser> planUsers = planUserRepository.findByAssignee(user);
        List<Plan> plans = planUsers.stream().map(PlanUser::getPlan).toList();
        int totalPlans = 0;
        int finishedPlans = 0;

        for (Plan plan : plans) {
            if(inWeek(plan.getEndDate()))
            {
                totalPlans++;
                if(inWeek(plan.getCompleteDate()))
                    finishedPlans++;
            }
        }

        return (float)finishedPlans/totalPlans;
    }

    public float levelOfFinishedSession(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new CustomNotFoundException("Can't find user with id " + userId)
        );

        List<StudySession> studySessions = studySessionRepository.findByUserId(userId);
        int totalSessions = 0;
        int finishedSessions = 0;
        for(StudySession session : studySessions) {
            LocalDateTime time = LocalDateTime.of(session.getDateCreate(), LocalTime.MIN);
            if(inWeek(time)) {
                totalSessions++;
                if(session.getStatus().equals(SessionStatus.COMPLETE))
                    finishedSessions++;
            }
        }

        return (float)finishedSessions/totalSessions;
    }

    private boolean inWeek(LocalDateTime endDate) {
        if(endDate == null) return false;

        LocalDate today = LocalDate.now();
        LocalDateTime startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        LocalDateTime endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .atTime(LocalTime.MAX);

        return (endDate.isAfter(startOfWeek) || endDate.equals(startOfWeek)) &&
                (endDate.isBefore(endOfWeek) || endDate.equals(endOfWeek));
    }
}

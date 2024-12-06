package com.example.selfstudybe.services;

import com.example.selfstudybe.enums.PlanStatus;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.Plan;
import com.example.selfstudybe.models.PlanUser;
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

//    public int getFinishedPlanInWeek(UUID userId) {
//        User user = userRepository.findById(userId).orElseThrow(
//                ()-> new CustomNotFoundException("Can't find user with id " + userId)
//        );
//
//        List<PlanUser> planUsers = planUserRepository.findByAssignee(user);
//        List<Plan> plans = planUsers.stream().map(PlanUser::getPlan).toList();
//
//        int finishedPlanInWeek = 0;
//
//        for (Plan plan : plans) {
//            if(inWeek(plan.getEndDate()) && plan.getStatus().equals(PlanStatus.COMPLETE))
//                finishedPlanInWeek++;
//        }
//
//        return finishedPlanInWeek;
//    }
//
//    public LocalTime focusTimeInWeek(UUID userId) {
//        if(!userRepository.existsById(userId))
//            throw new CustomNotFoundException("Can't find user with id " + userId);
//
//        List<StudySession> sessions = studySessionRepository.findByUserId(userId);
//        long totalTime = 0;
//        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
//                .atStartOfDay();
//
//        for (StudySession session : sessions) {
//            if(session.getUpdateAt().isBefore(LocalDateTime.now()) && session.getUpdateAt().isAfter(startOfWeek)) {
//                long focusTime = session.getFocusTime().toSecondOfDay();
//                long timeLeft = session.getTimeLeft().toSecondOfDay();
//                long realFocusTime = focusTime * (session.getCurrentStage()-1) + (focusTime-timeLeft);
//                totalTime += realFocusTime;
//            }
//        }
//
//        return LocalTime.ofSecondOfDay(totalTime);
//    }

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
                if(plan.getStatus().equals(PlanStatus.COMPLETE))
                    finishedPlans++;
            }
        }

        return (float)finishedPlans/totalPlans;
    }

//    public Map<String,Integer> focusChart(UUID userId) {
//        if(!userRepository.existsById(userId))
//            throw new CustomNotFoundException("Can't find user with id " + userId);
//
//        List<StudySession> sessions = studySessionRepository.findByUserId(userId);
//        Map<DayOfWeek,LocalTime> weeklyStudyTime = sessions.stream()
//                .collect(Collectors.groupingBy(
//                        session -> session.getUpdateAt()
//                ));
//
//    }

    private boolean inWeek(LocalDateTime endDate) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        LocalDateTime endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .atTime(LocalTime.MAX);

        return (endDate.isAfter(startOfWeek) || endDate.equals(startOfWeek)) &&
                (endDate.isBefore(endOfWeek) || endDate.equals(endOfWeek));
    }
}

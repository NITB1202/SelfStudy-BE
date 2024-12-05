package com.example.selfstudybe.models;

import com.example.selfstudybe.enums.SessionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "study-sessions")
public class StudySession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "session_id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "status", nullable = false, length = Integer.MAX_VALUE)
    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Column(name = "total_time")
    private LocalTime totalTime;

    @NotNull
    @Column(name = "focus_time", nullable = false)
    private LocalTime focusTime;

    @NotNull
    @Column(name = "break_time", nullable = false)
    private LocalTime breakTime;

    @NotNull
    @Column(name = "total_stage", nullable = false)
    private Integer totalStage;

    @ColumnDefault("1")
    @Column(name = "current_stage")
    private Integer currentStage;

    @Column(name = "music_link", length = Integer.MAX_VALUE)
    private String musicLink;

    @Column(name = "music_timestamp")
    private LocalTime musicTimestamp;

    @NotNull
    @Column(name = "time_left", nullable = false)
    private LocalTime timeLeft;

    @Column(name = "on_loop")
    private Boolean onLoop;
}
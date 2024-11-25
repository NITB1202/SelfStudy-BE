package com.example.selfstudybe.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "study-session-details")
public class StudySessionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "details_id", nullable = false)
    private UUID id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "session_id", nullable = false)
    private StudySession session;

    @NotNull
    @Column(name = "focus_time", nullable = false)
    private LocalTime focusTime;

    @Column(name = "break_time")
    private LocalTime breakTime;

    @Column(name = "total_stage")
    private Short totalStage;

    @Column(name = "\"current-stage\"")
    private Short currentStage;

    @Column(name = "music_link", length = Integer.MAX_VALUE)
    private String musicLink;

    @Column(name = "music_timestamp")
    private LocalTime musicTimestamp;

    @Column(name = "time_left")
    private LocalTime timeLeft;

    @ColumnDefault("false")
    @Column(name = "on_loop")
    private Boolean onLoop;

}
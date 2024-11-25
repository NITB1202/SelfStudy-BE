package com.example.selfstudybe.models;

import com.example.selfstudybe.enums.SessionStatus;
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

    @Column(name = "end_time")
    private LocalTime endTime;

    @NotNull
    @Column(name = "duration", nullable = false)
    private LocalTime duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private SessionStatus state;
}
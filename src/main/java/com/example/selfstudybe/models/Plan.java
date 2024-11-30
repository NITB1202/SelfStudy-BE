package com.example.selfstudybe.models;

import com.example.selfstudybe.enums.PlanStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "plan_id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @ColumnDefault("'01:00:00'")
    @Column(name = "notify_before")
    private LocalTime notifyBefore;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanStatus status;

    @ColumnDefault("0")
    @Column(name = "process")
    private Float process;

    @ColumnDefault("true")
    @Column(name = "is_personal")
    private Boolean isPersonal;

    @OneToMany(mappedBy = "plan")
    private Set<Notification> notifications = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "plan_user",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "assignee_id"))
    private Set<User> users = new LinkedHashSet<>();

    @OneToMany(mappedBy = "plan")
    private Set<Task> tasks = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "team_plan",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private Set<Team> teams = new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        if (notifyBefore == null) notifyBefore = LocalTime.of(1, 0, 0);
        if (process == null) process = 0f;
        if (isPersonal == null) isPersonal = true;
    }
}
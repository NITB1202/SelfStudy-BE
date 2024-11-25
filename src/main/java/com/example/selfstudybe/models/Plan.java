package com.example.selfstudybe.models;

import com.example.selfstudybe.enums.PlanStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"Plan\"")
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
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @ColumnDefault("'01:00:00'")
    @Column(name = "notify_before", nullable = false)
    private LocalTime notifyBefore;

    @OneToMany(mappedBy = "plan")
    private Set<Notification> notifications = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "Plan_User",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "assignee_id"))
    private Set<User> users = new LinkedHashSet<>();

    @OneToMany(mappedBy = "plan")
    private Set<Task> tasks = new LinkedHashSet<>();
    @ManyToMany
    @JoinTable(name = "Team_Plan",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private Set<Team> teams = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlanStatus status;
}
package com.example.selfstudybe.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "plan_user")
public class PlanUser {
    @EmbeddedId
    private PlanUserId id;

    @MapsId("planId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @MapsId("assigneeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;

}
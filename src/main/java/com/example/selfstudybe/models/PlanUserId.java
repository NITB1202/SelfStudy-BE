package com.example.selfstudybe.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class PlanUserId implements Serializable {
//    private static final long serialVersionUID = 6474067494125747588L;
    @NotNull
    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @NotNull
    @Column(name = "assignee_id", nullable = false)
    private UUID assigneeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PlanUserId entity = (PlanUserId) o;
        return Objects.equals(this.planId, entity.planId) &&
                Objects.equals(this.assigneeId, entity.assigneeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId, assigneeId);
    }

}
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
public class TeamPlanId implements Serializable {
    private static final long serialVersionUID = 6097894036961472750L;
    @NotNull
    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @NotNull
    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TeamPlanId entity = (TeamPlanId) o;
        return Objects.equals(this.teamId, entity.teamId) &&
                Objects.equals(this.planId, entity.planId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, planId);
    }

}
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
public class TeamSubjectId implements Serializable {
    private static final long serialVersionUID = 1708338185120273410L;
    @NotNull
    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @NotNull
    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TeamSubjectId entity = (TeamSubjectId) o;
        return Objects.equals(this.teamId, entity.teamId) &&
                Objects.equals(this.subjectId, entity.subjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, subjectId);
    }

}
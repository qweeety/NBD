package jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "athlete")
public class AthleteEnt {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "athlete")
    private Set<TrainingEnt> trainings = new HashSet<>();

    protected AthleteEnt() {
        // wymagany przez JPA
    }

    public AthleteEnt(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UUID getId() { return id; }
    public Long getVersion() { return version; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    public Set<TrainingEnt> getTrainings() {
        return Collections.unmodifiableSet(trainings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AthleteEnt that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}

package jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Korzeń hierarchii dziedziczenia encji sal (strategia JOINED):
 * wspólne kolumny w tabeli training_room, kolumny podklas w gym_room i pool_room.
 * Jedna sala ma wiele treningów (relacja jeden-do-wielu, dwukierunkowa).
 */
@Entity
@Table(name = "training_room")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class TrainingRoomEnt {

    @Id
    private UUID id;

    /** Blokada optymistyczna: każda zmiana wiersza sali podbija wersję. */
    @Version
    private Long version;

    @Column(name = "room_number", nullable = false, unique = true)
    private int roomNumber;

    @Column(nullable = false)
    private boolean archived;

    /** Strona odwrotna relacji. Właścicielem jest TrainingEnt.room. */
    @OneToMany(mappedBy = "room")
    private Set<TrainingEnt> trainings = new HashSet<>();

    protected TrainingRoomEnt() {
        // wymagany przez JPA
    }

    protected TrainingRoomEnt(UUID id, int roomNumber, boolean archived) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.archived = archived;
    }

    public UUID getId() { return id; }
    public Long getVersion() { return version; }
    public int getRoomNumber() { return roomNumber; }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }

    public Set<TrainingEnt> getTrainings() {
        return Collections.unmodifiableSet(trainings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainingRoomEnt that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}

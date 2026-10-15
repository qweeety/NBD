package jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Strona "wiele" obu relacji: wiele treningów należy do jednego zawodnika
 * i wiele treningów odbywa się w jednej sali. To ta encja jest właścicielem kluczy obcych.
 */
@Entity
@Table(name = "training",
        indexes = @Index(name = "idx_training_room_date", columnList = "room_id, training_date"))
public class TrainingEnt {

    @Id
    private UUID id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "athlete_id", nullable = false)
    private AthleteEnt athlete;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private TrainingRoomEnt room;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "begin_time", nullable = false)
    private LocalTime beginTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    protected TrainingEnt() {
        // wymagany przez JPA
    }

    public TrainingEnt(UUID id, AthleteEnt athlete, TrainingRoomEnt room,
                       LocalDate trainingDate, LocalTime beginTime, LocalTime endTime) {
        this.id = id;
        this.athlete = athlete;
        this.room = room;
        this.trainingDate = trainingDate;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public UUID getId() { return id; }
    public Long getVersion() { return version; }
    public AthleteEnt getAthlete() { return athlete; }
    public TrainingRoomEnt getRoom() { return room; }
    public LocalDate getTrainingDate() { return trainingDate; }
    public LocalTime getBeginTime() { return beginTime; }
    public LocalTime getEndTime() { return endTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainingEnt that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}

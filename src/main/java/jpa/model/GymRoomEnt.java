package jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "gym_room")
public class GymRoomEnt extends TrainingRoomEnt {

    @Column(nullable = false)
    private String equipment;

    protected GymRoomEnt() {
        // wymagany przez JPA
    }

    public GymRoomEnt(UUID id, int roomNumber, boolean archived, String equipment) {
        super(id, roomNumber, archived);
        this.equipment = equipment;
    }

    public String getEquipment() { return equipment; }
}

package jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("GYM")
public class GymRoomEnt extends TrainingRoomEnt {

    @Column(name = "equipment")
    private String equipment;

    protected GymRoomEnt() {
    }

    public GymRoomEnt(UUID id, int roomNumber, boolean archived, String equipment) {
        super(id, roomNumber, archived);
        if (equipment == null || equipment.isBlank()) {
            throw new IllegalArgumentException("Equipment cannot be empty");
        }
        this.equipment = equipment;
    }

    public String getEquipment() { return equipment; }
}
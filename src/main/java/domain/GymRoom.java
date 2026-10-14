package domain;

import java.util.UUID;

public class GymRoom extends TrainingRoom {
    private final String equipment;

    public GymRoom(UUID id, int roomNumber, boolean archived, String equipment) {
        super(id, roomNumber, archived);
        this.equipment = equipment;
    }

    public String getEquipment() { return equipment; }
}
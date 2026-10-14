package domain;

import java.util.UUID;

public class PoolRoom extends TrainingRoom {
    private final int laneCount;

    public PoolRoom(UUID id, int roomNumber, boolean archived, int laneCount) {
        super(id, roomNumber, archived);
        this.laneCount = laneCount;
    }

    public int getLaneCount() { return laneCount; }
}
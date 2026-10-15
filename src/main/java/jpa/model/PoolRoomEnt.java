package jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "pool_room")
public class PoolRoomEnt extends TrainingRoomEnt {

    @Column(name = "lane_count", nullable = false)
    private int laneCount;

    protected PoolRoomEnt() {
        // wymagany przez JPA
    }

    public PoolRoomEnt(UUID id, int roomNumber, boolean archived, int laneCount) {
        super(id, roomNumber, archived);
        this.laneCount = laneCount;
    }

    public int getLaneCount() { return laneCount; }
}

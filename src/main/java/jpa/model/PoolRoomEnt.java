package jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("POOL")
public class PoolRoomEnt extends TrainingRoomEnt {

    @Column(name = "lane_count")
    private Integer laneCount;

    protected PoolRoomEnt() {
    }

    public PoolRoomEnt(UUID id, int roomNumber, boolean archived, int laneCount) {
        super(id, roomNumber, archived);
        if (laneCount <= 0) {
            throw new IllegalArgumentException("Lane count must be positive");
        }
        this.laneCount = laneCount;
    }

    public int getLaneCount() { return laneCount; }
}
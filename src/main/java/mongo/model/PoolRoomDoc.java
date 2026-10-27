package mongo.model;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@BsonDiscriminator(key = "type", value = "POOL")
public class PoolRoomDoc extends TrainingRoomDoc {

    @BsonProperty("laneCount")
    private int laneCount;

    public PoolRoomDoc() {}

    public PoolRoomDoc(UUID id, int roomNumber, boolean archived, int laneCount) {
        super(id, roomNumber, archived);
        this.laneCount = laneCount;
    }

    public int getLaneCount() { return laneCount; }
    public void setLaneCount(int laneCount) { this.laneCount = laneCount; }
}
package mongo.model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.util.UUID;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(key = "type", value = "ROOM")
public class TrainingRoomDoc {
    @BsonId
    private UUID id;
    @BsonProperty("roomNumber")
    private int roomNumber;
    @BsonProperty("archived")
    private boolean archived;

    public TrainingRoomDoc() {}
    public TrainingRoomDoc(UUID id, int roomNumber, boolean archived) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.archived = archived;
    }

    public UUID getId() { return id; }
    public void setId(UUID id){this.id = id;}
    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber){this.roomNumber = roomNumber;}
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
}
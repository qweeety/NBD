package mongo.model;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@BsonDiscriminator(key = "type", value = "GYM")
public class GymRoomDoc extends TrainingRoomDoc {

    @BsonProperty("equipment")
    private String equipment;

    public GymRoomDoc() {}

    public GymRoomDoc(UUID id, int roomNumber, boolean archived, String equipment) {
        super(id, roomNumber, archived);
        this.equipment = equipment;
    }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
}
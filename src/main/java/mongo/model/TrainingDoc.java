package mongo.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.util.UUID;

public class TrainingDoc{

    @BsonId
    private UUID id;
    @BsonProperty("athleteId")
    private UUID athleteId;
    @BsonProperty("roomId")
    private UUID roomId;
    @BsonProperty("date")
    private String date;
    @BsonProperty("beginTime")
    private String beginTime;
    @BsonProperty("endTime")
    private String endTime;

    public TrainingDoc() {}

    public TrainingDoc(UUID id, UUID athleteId, UUID roomId,
                       String date, String beginTime, String endTime) {
        this.id = id;
        this.athleteId = athleteId;
        this.roomId = roomId;
        this.date = date;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAthleteId() { return athleteId; }
    public void setAthleteId(UUID athleteId) { this.athleteId = athleteId; }

    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getBeginTime() { return beginTime; }
    public void setBeginTime(String beginTime) { this.beginTime = beginTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
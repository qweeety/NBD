package domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Training {
    private final UUID id;
    private final Athlete athlete;
    private final TrainingRoom room;
    private final LocalDate date;
    private final LocalTime beginTime;
    private final LocalTime endTime;

    public Training(UUID id, Athlete athlete, TrainingRoom room,
                    LocalDate date, LocalTime beginTime, LocalTime endTime) {
        if (room.isArchived()) {
            throw new IllegalArgumentException("Room is archived");
        }
        if (!beginTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Wrong time");
        }
        this.id = id;
        this.athlete = athlete;
        this.room = room;
        this.date = date;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public boolean overlaps(Training other) {
        return date.equals(other.date)
                && beginTime.isBefore(other.endTime)
                && endTime.isAfter(other.beginTime);
    }

    public UUID getId() { return id; }
    public Athlete getAthlete() { return athlete; }
    public TrainingRoom getRoom() { return room; }
    public LocalDate getDate() { return date; }
    public LocalTime getBeginTime() { return beginTime; }
    public LocalTime getEndTime() { return endTime; }
}
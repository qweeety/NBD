package domain;

import java.util.UUID;

public abstract class TrainingRoom {
    private final UUID id;
    private final int roomNumber;
    private boolean archived;

    protected TrainingRoom(UUID id, int roomNumber, boolean archived) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.archived = archived;
    }

    public UUID getId() { return id; }
    public int getRoomNumber() { return roomNumber; }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
}
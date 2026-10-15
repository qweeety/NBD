package repositories;

import domain.TrainingRoom;
import java.util.UUID;

public interface TrainingRoomRepository {
    void add(TrainingRoom room);
    TrainingRoom findById(UUID id);
    void setArchived(UUID id, boolean archived);
}
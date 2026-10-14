package repositories;

import domain.Training;
import java.util.List;
import java.util.UUID;

public interface TrainingRepository {
    void add(Training training);
    List<Training> findByRoom(UUID roomId);
}
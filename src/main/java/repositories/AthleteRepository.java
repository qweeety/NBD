package repositories;

import domain.Athlete;
import java.util.UUID;

public interface AthleteRepository {
    void add(Athlete athlete);
    Athlete findById(UUID id);
}
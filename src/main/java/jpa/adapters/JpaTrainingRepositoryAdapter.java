package jpa.adapters;

import domain.Training;
import jakarta.persistence.EntityManagerFactory;
import jpa.JpaTransactions;
import jpa.mappers.TrainingMapper;
import jpa.model.AthleteEnt;
import jpa.model.TrainingEnt;
import jpa.model.TrainingRoomEnt;
import jpa.repositories.JpaAthleteRepository;
import jpa.repositories.JpaTrainingRepository;
import jpa.repositories.JpaTrainingRoomRepository;
import repositories.TrainingRepository;

import java.util.List;
import java.util.UUID;

public class JpaTrainingRepositoryAdapter implements TrainingRepository {

    private final JpaTransactions tx;

    public JpaTrainingRepositoryAdapter(EntityManagerFactory emf) {
        this.tx = new JpaTransactions(emf);
    }

    @Override
    public void add(Training training) {
        tx.run(em -> {
            JpaTrainingRoomRepository rooms = new JpaTrainingRoomRepository(em);
            JpaAthleteRepository athletes = new JpaAthleteRepository(em);
            JpaTrainingRepository trainings = new JpaTrainingRepository(em);

            UUID roomId = training.getRoom().getId();
            TrainingRoomEnt room = rooms.findByIdForUpdate(roomId);
            if (room == null) {
                throw new IllegalArgumentException("Room does not exist: " + roomId);
            }
            if (room.isArchived()) {
                throw new IllegalStateException("Room is archived: " + roomId);
            }

            UUID athleteId = training.getAthlete().getId();
            AthleteEnt athlete = athletes.findById(athleteId);
            if (athlete == null) {
                throw new IllegalArgumentException("Athlete does not exist: " + athleteId);
            }

            for (TrainingEnt existing : trainings.findByRoomAndDate(roomId, training.getDate())) {
                if (training.overlaps(TrainingMapper.toDomain(existing))) {
                    throw new IllegalStateException("Room is already booked in this time slot");
                }
            }

            trainings.add(TrainingMapper.toEnt(training, athlete, room));
        });
    }

    @Override
    public List<Training> findByRoom(UUID roomId) {
        return tx.call(em -> new JpaTrainingRepository(em).findByRoom(roomId).stream()
                .map(TrainingMapper::toDomain)
                .toList());
    }
}

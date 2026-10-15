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

/** Adapter: implementuje interfejs domenowy, konwertuje model i deleguje do repozytoriów JPA. */
public class JpaTrainingRepositoryAdapter implements TrainingRepository {

    private final JpaTransactions tx;

    public JpaTrainingRepositoryAdapter(EntityManagerFactory emf) {
        this.tx = new JpaTransactions(emf);
    }

    /**
     * Dodaje trening w jednej transakcji, pilnując reguły biznesowej: w danej sali nie mogą
     * nakładać się dwa treningi. Operację można wywołać współbieżnie dla tej samej sali.
     *
     * Kolejność: blokada wiersza sali -> sprawdzenie sali i zawodnika -> sprawdzenie nakładania
     * -> zapis. Wszystkie transakcje rezerwujące tę samą salę ustawiają się w kolejce na blokadzie,
     * więc każda następna widzi już zatwierdzone treningi poprzedniej i odrzuca nakładający się.
     *
     * @throws IllegalArgumentException gdy sala albo zawodnik nie istnieje w bazie
     * @throws IllegalStateException    gdy sala jest zarchiwizowana lub termin się nakłada
     */
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

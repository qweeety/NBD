package jpa.adapters;

import domain.TrainingRoom;
import jakarta.persistence.EntityManagerFactory;
import jpa.JpaTransactions;
import jpa.mappers.TrainingRoomMapper;
import jpa.model.TrainingRoomEnt;
import jpa.repositories.JpaTrainingRoomRepository;
import repositories.TrainingRoomRepository;

import java.util.UUID;

/** Adapter: implementuje interfejs domenowy, konwertuje model i deleguje do repozytorium JPA. */
public class JpaTrainingRoomRepositoryAdapter implements TrainingRoomRepository {

    private final JpaTransactions tx;

    public JpaTrainingRoomRepositoryAdapter(EntityManagerFactory emf) {
        this.tx = new JpaTransactions(emf);
    }

    @Override
    public void add(TrainingRoom room) {
        tx.run(em -> new JpaTrainingRoomRepository(em).add(TrainingRoomMapper.toEnt(room)));
    }

    /** @return sala (GymRoom lub PoolRoom) albo null, gdy nie istnieje */
    @Override
    public TrainingRoom findById(UUID id) {
        return tx.call(em -> {
            TrainingRoomEnt ent = new JpaTrainingRoomRepository(em).findById(id);
            return ent == null ? null : TrainingRoomMapper.toDomain(ent);
        });
    }
}

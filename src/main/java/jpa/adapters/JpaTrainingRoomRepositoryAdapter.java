package jpa.adapters;

import domain.TrainingRoom;
import jakarta.persistence.EntityManagerFactory;
import jpa.JpaTransactions;
import jpa.mappers.TrainingRoomMapper;
import jpa.model.TrainingRoomEnt;
import jpa.repositories.JpaTrainingRoomRepository;
import repositories.TrainingRoomRepository;

import java.util.UUID;

public class JpaTrainingRoomRepositoryAdapter implements TrainingRoomRepository {

    private final JpaTransactions tx;

    public JpaTrainingRoomRepositoryAdapter(EntityManagerFactory emf) {
        this.tx = new JpaTransactions(emf);
    }

    @Override
    public void add(TrainingRoom room) {
        tx.run(em -> new JpaTrainingRoomRepository(em).add(TrainingRoomMapper.toEnt(room)));
    }

    @Override
    public TrainingRoom findById(UUID id) {
        return tx.call(em -> {
            TrainingRoomEnt ent = new JpaTrainingRoomRepository(em).findById(id);
            return ent == null ? null : TrainingRoomMapper.toDomain(ent);
        });
    }

    @Override
    public void setArchived(UUID id, boolean archived) {
        tx.run(em -> {
            TrainingRoomEnt ent = new JpaTrainingRoomRepository(em).findByIdForUpdate(id);
            if (ent == null) {
                throw new IllegalArgumentException("Room does not exist: " + id);
            }
            ent.setArchived(archived);
        });
    }
}
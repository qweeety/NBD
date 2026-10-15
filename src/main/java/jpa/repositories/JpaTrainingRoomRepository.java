package jpa.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jpa.model.TrainingRoomEnt;

import java.util.UUID;

public class JpaTrainingRoomRepository {

    private final EntityManager em;

    public JpaTrainingRoomRepository(EntityManager em) {
        this.em = em;
    }

    public void add(TrainingRoomEnt room) {
        em.persist(room);
    }

    public TrainingRoomEnt findById(UUID id) {
        return em.find(TrainingRoomEnt.class, id);
    }

    public TrainingRoomEnt findByIdForUpdate(UUID id) {
        return em.find(TrainingRoomEnt.class, id, LockModeType.PESSIMISTIC_WRITE);
    }
}
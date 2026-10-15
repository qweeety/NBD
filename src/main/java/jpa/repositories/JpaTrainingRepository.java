package jpa.repositories;

import jakarta.persistence.EntityManager;
import jpa.model.TrainingEnt;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class JpaTrainingRepository {

    private final EntityManager em;

    public JpaTrainingRepository(EntityManager em) {
        this.em = em;
    }

    public void add(TrainingEnt training) {
        em.persist(training);
    }

    public List<TrainingEnt> findByRoom(UUID roomId) {
        return em.createQuery(
                        "SELECT t FROM TrainingEnt t JOIN FETCH t.athlete JOIN FETCH t.room WHERE t.room.id = :roomId",
                        TrainingEnt.class)
                .setParameter("roomId", roomId)
                .getResultList();
    }

    public List<TrainingEnt> findByRoomAndDate(UUID roomId, LocalDate date) {
        return em.createQuery(
                        "SELECT t FROM TrainingEnt t JOIN FETCH t.athlete JOIN FETCH t.room WHERE t.room.id = :roomId AND t.trainingDate = :date",
                        TrainingEnt.class)
                .setParameter("roomId", roomId)
                .setParameter("date", date)
                .getResultList();
    }
}
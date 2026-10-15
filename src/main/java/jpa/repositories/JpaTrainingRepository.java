package jpa.repositories;

import jakarta.persistence.EntityManager;
import jpa.model.TrainingEnt;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Repozytorium na poziomie modelu bazodanowego. Nie zna klas domenowych. */
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
                        "select t from TrainingEnt t "
                                + "join fetch t.athlete "
                                + "join fetch t.room "
                                + "where t.room.id = :roomId "
                                + "order by t.trainingDate, t.beginTime",
                        TrainingEnt.class)
                .setParameter("roomId", roomId)
                .getResultList();
    }

    public List<TrainingEnt> findByRoomAndDate(UUID roomId, LocalDate date) {
        return em.createQuery(
                        "select t from TrainingEnt t "
                                + "join fetch t.athlete "
                                + "join fetch t.room "
                                + "where t.room.id = :roomId and t.trainingDate = :date",
                        TrainingEnt.class)
                .setParameter("roomId", roomId)
                .setParameter("date", date)
                .getResultList();
    }
}

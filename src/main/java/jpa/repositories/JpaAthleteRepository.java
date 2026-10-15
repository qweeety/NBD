package jpa.repositories;

import jakarta.persistence.EntityManager;
import jpa.model.AthleteEnt;

import java.util.UUID;

/** Repozytorium na poziomie modelu bazodanowego. Nie zna klas domenowych. */
public class JpaAthleteRepository {

    private final EntityManager em;

    public JpaAthleteRepository(EntityManager em) {
        this.em = em;
    }

    public void add(AthleteEnt athlete) {
        em.persist(athlete);
    }

    public AthleteEnt findById(UUID id) {
        return em.find(AthleteEnt.class, id);
    }
}

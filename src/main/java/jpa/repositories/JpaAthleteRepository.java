package jpa.repositories;

import jakarta.persistence.EntityManager;
import jpa.model.AthleteEnt;

import java.util.UUID;

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

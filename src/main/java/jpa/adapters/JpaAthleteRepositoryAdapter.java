package jpa.adapters;

import domain.Athlete;
import jakarta.persistence.EntityManagerFactory;
import jpa.JpaTransactions;
import jpa.mappers.AthleteMapper;
import jpa.model.AthleteEnt;
import jpa.repositories.JpaAthleteRepository;
import repositories.AthleteRepository;

import java.util.UUID;

public class JpaAthleteRepositoryAdapter implements AthleteRepository {

    private final JpaTransactions tx;

    public JpaAthleteRepositoryAdapter(EntityManagerFactory emf) {
        this.tx = new JpaTransactions(emf);
    }

    @Override
    public void add(Athlete athlete) {
        tx.run(em -> new JpaAthleteRepository(em).add(AthleteMapper.toEnt(athlete)));
    }

    @Override
    public Athlete findById(UUID id) {
        return tx.call(em -> {
            AthleteEnt ent = new JpaAthleteRepository(em).findById(id);
            return ent == null ? null : AthleteMapper.toDomain(ent);
        });
    }
}

package jpa.mappers;

import domain.Athlete;
import jpa.model.AthleteEnt;

public final class AthleteMapper {

    private AthleteMapper() { }

    public static AthleteEnt toEnt(Athlete athlete) {
        return new AthleteEnt(athlete.getId(), athlete.getFirstName(), athlete.getLastName());
    }

    public static Athlete toDomain(AthleteEnt ent) {
        return new Athlete(ent.getId(), ent.getFirstName(), ent.getLastName());
    }
}

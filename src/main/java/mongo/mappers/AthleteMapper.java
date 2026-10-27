package mongo.mappers;

import domain.Athlete;
import mongo.model.AthleteDoc;

public class AthleteMapper {

    public static AthleteDoc toDoc(Athlete athlete) {
        return new AthleteDoc(athlete.getId(), athlete.getFirstName(), athlete.getLastName());
    }

    public static Athlete toDomain(AthleteDoc doc) {
        return new Athlete(doc.getId(), doc.getFirstName(), doc.getLastName());
    }
}
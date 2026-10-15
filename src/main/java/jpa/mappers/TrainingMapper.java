package jpa.mappers;

import domain.Training;
import jpa.model.AthleteEnt;
import jpa.model.TrainingEnt;
import jpa.model.TrainingRoomEnt;

public final class TrainingMapper {

    private TrainingMapper() { }

    public static TrainingEnt toEnt(Training training, AthleteEnt athlete, TrainingRoomEnt room) {
        return new TrainingEnt(
                training.getId(),
                athlete,
                room,
                training.getDate(),
                training.getBeginTime(),
                training.getEndTime());
    }

    public static Training toDomain(TrainingEnt ent) {
        return new Training(
                ent.getId(),
                AthleteMapper.toDomain(ent.getAthlete()),
                TrainingRoomMapper.toDomain(ent.getRoom()),
                ent.getTrainingDate(),
                ent.getBeginTime(),
                ent.getEndTime());
    }
}

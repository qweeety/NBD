package mongo.mappers;

import domain.Athlete;
import domain.Training;
import domain.TrainingRoom;
import mongo.model.TrainingDoc;

import java.time.LocalDate;
import java.time.LocalTime;

public class TrainingMapper {

    public static TrainingDoc toDoc(Training training) {
        return new TrainingDoc(
                training.getId(),
                training.getAthlete().getId(),
                training.getRoom().getId(),
                training.getDate().toString(),
                training.getBeginTime().toString(),
                training.getEndTime().toString());
    }

    public static Training toDomain(TrainingDoc doc, Athlete athlete, TrainingRoom room) {
        return new Training(
                doc.getId(),
                athlete,
                room,
                LocalDate.parse(doc.getDate()),
                LocalTime.parse(doc.getBeginTime()),
                LocalTime.parse(doc.getEndTime()));
    }
}
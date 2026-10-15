package jpa;

import domain.Athlete;
import domain.GymRoom;
import domain.PoolRoom;
import domain.Training;
import repositories.AthleteRepository;
import repositories.TrainingRepository;
import repositories.TrainingRoomRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public final class DataInitializer {

    public static final UUID ATHLETE_ANNA = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
    public static final UUID ATHLETE_PIOTR = UUID.fromString("00000000-0000-0000-0000-0000000000a2");
    public static final UUID GYM_ROOM = UUID.fromString("00000000-0000-0000-0000-0000000000b1");
    public static final UUID POOL_ROOM = UUID.fromString("00000000-0000-0000-0000-0000000000b2");
    public static final UUID TRAINING_1 = UUID.fromString("00000000-0000-0000-0000-0000000000c1");
    public static final UUID TRAINING_2 = UUID.fromString("00000000-0000-0000-0000-0000000000c2");

    private DataInitializer() { }

    public static void seed(AthleteRepository athletes,
                            TrainingRoomRepository rooms,
                            TrainingRepository trainings) {
        if (athletes.findById(ATHLETE_ANNA) != null) {
            return;
        }

        Athlete anna = new Athlete(ATHLETE_ANNA, "Anna", "Nowak");
        Athlete piotr = new Athlete(ATHLETE_PIOTR, "Piotr", "Wisniewski");
        athletes.add(anna);
        athletes.add(piotr);

        GymRoom gym = new GymRoom(GYM_ROOM, 1, false, "sztangi, hantle, wyciagi");
        PoolRoom pool = new PoolRoom(POOL_ROOM, 2, false, 6);
        rooms.add(gym);
        rooms.add(pool);

        LocalDate day = LocalDate.of(2026, 10, 20);
        trainings.add(new Training(TRAINING_1, anna, gym, day, LocalTime.of(9, 0), LocalTime.of(10, 0)));
        trainings.add(new Training(TRAINING_2, piotr, pool, day, LocalTime.of(9, 0), LocalTime.of(10, 30)));
    }
}

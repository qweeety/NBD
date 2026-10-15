package jpa;

import domain.Athlete;
import domain.GymRoom;
import domain.PoolRoom;
import domain.Training;
import domain.TrainingRoom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jpa.adapters.JpaAthleteRepositoryAdapter;
import jpa.adapters.JpaTrainingRepositoryAdapter;
import jpa.adapters.JpaTrainingRoomRepositoryAdapter;
import jpa.model.AthleteEnt;
import jpa.model.TrainingRoomEnt;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repositories.AthleteRepository;
import repositories.TrainingRepository;
import repositories.TrainingRoomRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdaptersTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 20);
    private static final AtomicInteger ROOM_NUMBERS = new AtomicInteger(100);

    private static EntityManagerFactory emf;
    private static AthleteRepository athletes;
    private static TrainingRoomRepository rooms;
    private static TrainingRepository trainings;

    @BeforeAll
    static void start() {
        emf = TestEntityManagerFactory.create();
        athletes = new JpaAthleteRepositoryAdapter(emf);
        rooms = new JpaTrainingRoomRepositoryAdapter(emf);
        trainings = new JpaTrainingRepositoryAdapter(emf);
    }

    @AfterAll
    static void stop() {
        emf.close();
    }

    private static Athlete newAthlete() {
        Athlete athlete = new Athlete(UUID.randomUUID(), "Jan", "Kowalski");
        athletes.add(athlete);
        return athlete;
    }

    private static GymRoom newGym() {
        GymRoom gym = new GymRoom(UUID.randomUUID(), ROOM_NUMBERS.incrementAndGet(), false, "barbells");
        rooms.add(gym);
        return gym;
    }

    private static PoolRoom newPool() {
        PoolRoom pool = new PoolRoom(UUID.randomUUID(), ROOM_NUMBERS.incrementAndGet(), false, 6);
        rooms.add(pool);
        return pool;
    }

    private static Training training(Athlete athlete, TrainingRoom room, LocalDate day, int fromHour, int toHour) {
        return new Training(UUID.randomUUID(), athlete, room, day,
                LocalTime.of(fromHour, 0), LocalTime.of(toHour, 0));
    }

    @Test
    void athleteRoundTrip() {
        Athlete saved = newAthlete();

        Athlete loaded = athletes.findById(saved.getId());

        assertNotNull(loaded);
        assertEquals(saved.getId(), loaded.getId());
        assertEquals("Jan", loaded.getFirstName());
        assertEquals("Kowalski", loaded.getLastName());
    }

    @Test
    void missingEntitiesAreReturnedAsNull() {
        assertNull(athletes.findById(UUID.randomUUID()));
        assertNull(rooms.findById(UUID.randomUUID()));
    }

    @Test
    void roomSubtypesSurviveRoundTrip() {
        GymRoom gym = newGym();
        PoolRoom pool = newPool();

        TrainingRoom loadedGym = rooms.findById(gym.getId());
        TrainingRoom loadedPool = rooms.findById(pool.getId());

        assertEquals("barbells", assertInstanceOf(GymRoom.class, loadedGym).getEquipment());
        assertEquals(6, assertInstanceOf(PoolRoom.class, loadedPool).getLaneCount());
        assertEquals(gym.getRoomNumber(), loadedGym.getRoomNumber());
    }

    @Test
    void trainingIsStoredAndRoomKeepsItsSubtype() {
        Athlete athlete = newAthlete();
        PoolRoom pool = newPool();

        trainings.add(training(athlete, pool, DAY, 10, 11));

        List<Training> loaded = trainings.findByRoom(pool.getId());
        assertEquals(1, loaded.size());
        Training t = loaded.get(0);
        assertInstanceOf(PoolRoom.class, t.getRoom());
        assertEquals(athlete.getId(), t.getAthlete().getId());
        assertEquals(DAY, t.getDate());
        assertEquals(LocalTime.of(10, 0), t.getBeginTime());
        assertEquals(LocalTime.of(11, 0), t.getEndTime());
    }

    @Test
    void overlappingTrainingInTheSameRoomIsRejected() {
        Athlete athlete = newAthlete();
        GymRoom gym = newGym();
        trainings.add(training(athlete, gym, DAY, 10, 12));

        assertThrows(IllegalStateException.class,
                () -> trainings.add(training(athlete, gym, DAY, 11, 13)));

        assertEquals(1, trainings.findByRoom(gym.getId()).size());
    }

    @Test
    void adjacentTrainingsAndOtherDaysAreAllowed() {
        Athlete athlete = newAthlete();
        GymRoom gym = newGym();

        trainings.add(training(athlete, gym, DAY, 10, 11));
        trainings.add(training(athlete, gym, DAY, 11, 12));
        trainings.add(training(athlete, gym, DAY.plusDays(1), 10, 11));

        assertEquals(3, trainings.findByRoom(gym.getId()).size());
    }

    @Test
    void sameTimeInDifferentRoomsIsAllowed() {
        Athlete athlete = newAthlete();
        GymRoom gym = newGym();
        PoolRoom pool = newPool();

        trainings.add(training(athlete, gym, DAY, 10, 11));
        trainings.add(training(athlete, pool, DAY, 10, 11));

        assertEquals(1, trainings.findByRoom(gym.getId()).size());
        assertEquals(1, trainings.findByRoom(pool.getId()).size());
    }

    @Test
    void roomArchivedInDatabaseIsRejectedEvenWhenDomainObjectIsStale() {
        Athlete athlete = newAthlete();
        GymRoom staleRoom = newGym();

        rooms.setArchived(staleRoom.getId(), true);

        assertThrows(IllegalStateException.class,
                () -> trainings.add(training(athlete, staleRoom, DAY, 10, 11)));
    }

    @Test
    void unknownRoomOrAthleteIsRejected() {
        Athlete savedAthlete = newAthlete();
        GymRoom savedRoom = newGym();
        GymRoom unsavedRoom = new GymRoom(UUID.randomUUID(), ROOM_NUMBERS.incrementAndGet(), false, "none");
        Athlete unsavedAthlete = new Athlete(UUID.randomUUID(), "Nie", "Istnieje");

        assertThrows(IllegalArgumentException.class,
                () -> trainings.add(training(savedAthlete, unsavedRoom, DAY, 10, 11)));
        assertThrows(IllegalArgumentException.class,
                () -> trainings.add(training(unsavedAthlete, savedRoom, DAY, 10, 11)));
    }

    @Test
    void relationsAreOneToManyAndBidirectional() {
        Athlete athlete = newAthlete();
        GymRoom gym = newGym();
        trainings.add(training(athlete, gym, DAY, 10, 11));
        trainings.add(training(athlete, gym, DAY, 12, 13));

        EntityManager em = emf.createEntityManager();
        try {
            assertEquals(2, em.find(TrainingRoomEnt.class, gym.getId()).getTrainings().size());
            assertEquals(2, em.find(AthleteEnt.class, athlete.getId()).getTrainings().size());
        } finally {
            em.close();
        }
    }
}

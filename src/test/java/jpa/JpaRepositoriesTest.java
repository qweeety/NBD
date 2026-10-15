package jpa;

import jakarta.persistence.EntityManagerFactory;
import jpa.model.AthleteEnt;
import jpa.model.GymRoomEnt;
import jpa.model.PoolRoomEnt;
import jpa.model.TrainingEnt;
import jpa.model.TrainingRoomEnt;
import jpa.repositories.JpaAthleteRepository;
import jpa.repositories.JpaTrainingRepository;
import jpa.repositories.JpaTrainingRoomRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class JpaRepositoriesTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 22);
    private static final AtomicInteger ROOM_NUMBERS = new AtomicInteger(2000);

    private static EntityManagerFactory emf;
    private static JpaTransactions tx;

    @BeforeAll
    static void start() {
        emf = TestEntityManagerFactory.create();
        tx = new JpaTransactions(emf);
    }

    @AfterAll
    static void stop() {
        emf.close();
    }

    @Test
    void athleteRepositoryPersistsAndFindsById() {
        UUID id = UUID.randomUUID();

        tx.run(em -> new JpaAthleteRepository(em).add(new AthleteEnt(id, "Jan", "Kowalski")));
        AthleteEnt found = tx.call(em -> new JpaAthleteRepository(em).findById(id));

        assertNotNull(found);
        assertEquals("Kowalski", found.getLastName());
        assertNotNull(found.getVersion());
        assertNull(tx.call(em -> new JpaAthleteRepository(em).findById(UUID.randomUUID())));
    }

    @Test
    void roomRepositoryReturnsConcreteSubclasses() {
        UUID gymId = UUID.randomUUID();
        UUID poolId = UUID.randomUUID();
        tx.run(em -> {
            JpaTrainingRoomRepository rooms = new JpaTrainingRoomRepository(em);
            rooms.add(new GymRoomEnt(gymId, ROOM_NUMBERS.incrementAndGet(), false, "barbells"));
            rooms.add(new PoolRoomEnt(poolId, ROOM_NUMBERS.incrementAndGet(), false, 8));
        });

        TrainingRoomEnt gym = tx.call(em -> new JpaTrainingRoomRepository(em).findById(gymId));
        TrainingRoomEnt pool = tx.call(em -> new JpaTrainingRoomRepository(em).findById(poolId));

        assertEquals("barbells", assertInstanceOf(GymRoomEnt.class, gym).getEquipment());
        assertEquals(8, assertInstanceOf(PoolRoomEnt.class, pool).getLaneCount());
    }

    @Test
    void findByIdForUpdateReturnsRoomOrNull() {
        UUID id = UUID.randomUUID();
        tx.run(em -> new JpaTrainingRoomRepository(em)
                .add(new GymRoomEnt(id, ROOM_NUMBERS.incrementAndGet(), false, "barbells")));

        TrainingRoomEnt locked = tx.call(em -> new JpaTrainingRoomRepository(em).findByIdForUpdate(id));
        TrainingRoomEnt missing = tx.call(em -> new JpaTrainingRoomRepository(em).findByIdForUpdate(UUID.randomUUID()));

        assertNotNull(locked);
        assertEquals(id, locked.getId());
        assertNull(missing);
    }

    @Test
    void trainingRepositoryFindsByRoomAndByRoomAndDate() {
        UUID athleteId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        tx.run(em -> {
            AthleteEnt athlete = new AthleteEnt(athleteId, "Jan", "Kowalski");
            TrainingRoomEnt room = new GymRoomEnt(roomId, ROOM_NUMBERS.incrementAndGet(), false, "barbells");
            em.persist(athlete);
            em.persist(room);
            JpaTrainingRepository trainings = new JpaTrainingRepository(em);
            trainings.add(new TrainingEnt(UUID.randomUUID(), athlete, room, DAY,
                    LocalTime.of(10, 0), LocalTime.of(11, 0)));
            trainings.add(new TrainingEnt(UUID.randomUUID(), athlete, room, DAY.plusDays(1),
                    LocalTime.of(10, 0), LocalTime.of(11, 0)));
        });

        List<TrainingEnt> all = tx.call(em -> new JpaTrainingRepository(em).findByRoom(roomId));
        List<TrainingEnt> oneDay = tx.call(em -> new JpaTrainingRepository(em).findByRoomAndDate(roomId, DAY));

        assertEquals(2, all.size());
        assertEquals(1, oneDay.size());
        assertEquals(athleteId, oneDay.get(0).getAthlete().getId());
        assertInstanceOf(GymRoomEnt.class, oneDay.get(0).getRoom());
    }
}

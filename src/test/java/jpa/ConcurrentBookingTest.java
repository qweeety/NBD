package jpa;

import domain.Athlete;
import domain.GymRoom;
import domain.Training;
import jakarta.persistence.EntityManagerFactory;
import jpa.adapters.JpaAthleteRepositoryAdapter;
import jpa.adapters.JpaTrainingRepositoryAdapter;
import jpa.adapters.JpaTrainingRoomRepositoryAdapter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repositories.AthleteRepository;
import repositories.TrainingRepository;
import repositories.TrainingRoomRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Współbieżne dodawanie treningów do tej samej sali. Wątki startują naraz (zatrzask),
 * każdy w osobnej transakcji na osobnym połączeniu z bazą.
 */
class ConcurrentBookingTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 21);
    private static final int THREADS = 16;
    private static final int ROUNDS = 5;
    private static final AtomicInteger ROOM_NUMBERS = new AtomicInteger(1000);

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

    private static GymRoom newRoom() {
        GymRoom room = new GymRoom(UUID.randomUUID(), ROOM_NUMBERS.incrementAndGet(), false, "barbells");
        rooms.add(room);
        return room;
    }

    private static Athlete newAthlete(int i) {
        Athlete athlete = new Athlete(UUID.randomUUID(), "Zawodnik", "Nr" + i);
        athletes.add(athlete);
        return athlete;
    }

    /** Uruchamia wszystkie próby jednocześnie i zwraca, ile z nich zakończyło się sukcesem. */
    private static int runConcurrently(List<Training> attempts) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(attempts.size());
        try {
            CountDownLatch ready = new CountDownLatch(attempts.size());
            CountDownLatch go = new CountDownLatch(1);
            List<Future<Boolean>> results = new ArrayList<>();
            for (Training attempt : attempts) {
                results.add(pool.submit(() -> {
                    ready.countDown();
                    go.await();
                    try {
                        trainings.add(attempt);
                        return true;
                    } catch (IllegalStateException rejected) {
                        return false;
                    }
                }));
            }
            ready.await();
            go.countDown();

            int succeeded = 0;
            for (Future<Boolean> result : results) {
                if (result.get(60, TimeUnit.SECONDS)) {
                    succeeded++;
                }
            }
            return succeeded;
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void ofManyConcurrentOverlappingBookingsExactlyOneSucceeds() throws Exception {
        for (int round = 0; round < ROUNDS; round++) {
            GymRoom room = newRoom();
            List<Training> attempts = new ArrayList<>();
            for (int i = 0; i < THREADS; i++) {
                Athlete athlete = newAthlete(i);
                attempts.add(new Training(UUID.randomUUID(), athlete, room, DAY,
                        LocalTime.of(10, 0), LocalTime.of(11, 0)));
            }

            int succeeded = runConcurrently(attempts);

            assertEquals(1, succeeded, "round " + round + ": exactly one booking should win");
            assertEquals(1, trainings.findByRoom(room.getId()).size(),
                    "round " + round + ": exactly one training should be stored");
        }
    }

    @Test
    void concurrentBookingsOfDisjointSlotsAllSucceed() throws Exception {
        GymRoom room = newRoom();
        List<Training> attempts = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Athlete athlete = newAthlete(i);
            attempts.add(new Training(UUID.randomUUID(), athlete, room, DAY,
                    LocalTime.of(8 + i, 0), LocalTime.of(9 + i, 0)));
        }

        int succeeded = runConcurrently(attempts);

        assertEquals(8, succeeded);
        assertEquals(8, trainings.findByRoom(room.getId()).size());
    }

    @Test
    void concurrentBookingsInDifferentRoomsDoNotBlockEachOther() throws Exception {
        List<Training> attempts = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            attempts.add(new Training(UUID.randomUUID(), newAthlete(i), newRoom(), DAY,
                    LocalTime.of(10, 0), LocalTime.of(11, 0)));
        }

        assertEquals(8, runConcurrently(attempts));
    }
}

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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurrentBookingTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 21);
    private static int roomCounter = 1000;

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
        GymRoom room = new GymRoom(UUID.randomUUID(), ++roomCounter, false, "barbells");
        rooms.add(room);
        return room;
    }

    private static Athlete newAthlete(int i) {
        Athlete athlete = new Athlete(UUID.randomUUID(), "Zawodnik", "Nr" + i);
        athletes.add(athlete);
        return athlete;
    }

    private static int runConcurrently(List<Training> attempts) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(attempts.size());
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(attempts.size());
        AtomicInteger succeeded = new AtomicInteger(0);

        for (Training attempt : attempts) {
            pool.submit(() -> {
                try {
                    startLatch.await();
                    trainings.add(attempt);
                    succeeded.incrementAndGet();
                } catch (IllegalStateException rejected) {
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean finished = doneLatch.await(60, TimeUnit.SECONDS);
        pool.shutdownNow();
        assertTrue(finished, "threads did not finish, possible deadlock");
        return succeeded.get();
    }

    @Test
    void ofManyConcurrentOverlappingBookingsExactlyOneSucceeds() throws InterruptedException {
        GymRoom room = newRoom();
        List<Training> attempts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Athlete athlete = newAthlete(i);
            attempts.add(new Training(UUID.randomUUID(), athlete, room, DAY,
                    LocalTime.of(10, 0), LocalTime.of(11, 0)));
        }

        int succeeded = runConcurrently(attempts);

        assertEquals(1, succeeded);
        assertEquals(1, trainings.findByRoom(room.getId()).size());
    }

    @Test
    void concurrentBookingsOfDisjointSlotsAllSucceed() throws InterruptedException {
        GymRoom room = newRoom();
        List<Training> attempts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Athlete athlete = newAthlete(i);
            attempts.add(new Training(UUID.randomUUID(), athlete, room, DAY,
                    LocalTime.of(8 + i, 0), LocalTime.of(9 + i, 0)));
        }

        int succeeded = runConcurrently(attempts);

        assertEquals(5, succeeded);
        assertEquals(5, trainings.findByRoom(room.getId()).size());
    }
}
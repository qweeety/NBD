package jpa;

import domain.GymRoom;
import domain.PoolRoom;
import jakarta.persistence.EntityManagerFactory;
import jpa.adapters.JpaAthleteRepositoryAdapter;
import jpa.adapters.JpaTrainingRepositoryAdapter;
import jpa.adapters.JpaTrainingRoomRepositoryAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataInitializerTest {

    @Test
    void seedsInitialDataAndIsIdempotent() {
        EntityManagerFactory emf = TestEntityManagerFactory.create();
        try {
            JpaAthleteRepositoryAdapter athletes = new JpaAthleteRepositoryAdapter(emf);
            JpaTrainingRoomRepositoryAdapter rooms = new JpaTrainingRoomRepositoryAdapter(emf);
            JpaTrainingRepositoryAdapter trainings = new JpaTrainingRepositoryAdapter(emf);

            DataInitializer.seed(athletes, rooms, trainings);
            DataInitializer.seed(athletes, rooms, trainings); // drugie wywołanie nic nie zmienia

            assertNotNull(athletes.findById(DataInitializer.ATHLETE_ANNA));
            assertNotNull(athletes.findById(DataInitializer.ATHLETE_PIOTR));
            assertInstanceOf(GymRoom.class, rooms.findById(DataInitializer.GYM_ROOM));
            assertInstanceOf(PoolRoom.class, rooms.findById(DataInitializer.POOL_ROOM));
            assertEquals(1, trainings.findByRoom(DataInitializer.GYM_ROOM).size());
            assertEquals(1, trainings.findByRoom(DataInitializer.POOL_ROOM).size());
        } finally {
            emf.close();
        }
    }
}

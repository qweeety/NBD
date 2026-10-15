package jpa;

import domain.Training;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jpa.adapters.JpaAthleteRepositoryAdapter;
import jpa.adapters.JpaTrainingRepositoryAdapter;
import jpa.adapters.JpaTrainingRoomRepositoryAdapter;

/**
 * Uruchomienie aplikacji na bazie "nbd" z docker-compose: Hibernate tworzy tabele (hbm2ddl=update),
 * a DataInitializer wstawia dane początkowe (tylko jeśli ich jeszcze nie ma).
 */
public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gymPU");
        try {
            JpaTrainingRepositoryAdapter trainings = new JpaTrainingRepositoryAdapter(emf);
            DataInitializer.seed(
                    new JpaAthleteRepositoryAdapter(emf),
                    new JpaTrainingRoomRepositoryAdapter(emf),
                    trainings);

            for (Training t : trainings.findByRoom(DataInitializer.GYM_ROOM)) {
                System.out.println("Sala " + t.getRoom().getRoomNumber() + " (" + t.getRoom().getClass().getSimpleName()
                        + "): " + t.getAthlete().getLastName() + " " + t.getDate() + " "
                        + t.getBeginTime() + "-" + t.getEndTime());
            }
            for (Training t : trainings.findByRoom(DataInitializer.POOL_ROOM)) {
                System.out.println("Sala " + t.getRoom().getRoomNumber() + " (" + t.getRoom().getClass().getSimpleName()
                        + "): " + t.getAthlete().getLastName() + " " + t.getDate() + " "
                        + t.getBeginTime() + "-" + t.getEndTime());
            }
        } finally {
            emf.close();
        }
    }
}

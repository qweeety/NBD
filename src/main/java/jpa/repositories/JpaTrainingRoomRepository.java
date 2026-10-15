package jpa.repositories;

import jakarta.persistence.EntityManager;
import jpa.model.TrainingRoomEnt;

import java.util.List;
import java.util.UUID;

/** Repozytorium na poziomie modelu bazodanowego. Nie zna klas domenowych. */
public class JpaTrainingRoomRepository {

    private final EntityManager em;

    public JpaTrainingRoomRepository(EntityManager em) {
        this.em = em;
    }

    public void add(TrainingRoomEnt room) {
        em.persist(room);
    }

    public TrainingRoomEnt findById(UUID id) {
        return em.find(TrainingRoomEnt.class, id);
    }

    /**
     * Blokada pesymistyczna wiersza sali (SELECT ... FOR UPDATE) i dopiero potem odczyt encji.
     *
     * Kolejność ma znaczenie: transakcja, która czekała na blokadę, po jej uzyskaniu czyta
     * świeży stan sali (np. flagę archived) i widzi zatwierdzone już treningi konkurentów
     * (READ_COMMITTED: każde zapytanie widzi dane zatwierdzone do swojego startu).
     *
     * Blokujemy natywnym zapytaniem po tabeli głównej hierarchii, bo em.find(..., PESSIMISTIC_WRITE)
     * dla dziedziczenia JOINED generuje w PostgreSQL zapytanie z LEFT JOIN do podtabel,
     * a FOR UPDATE na nullowalnej stronie złączenia zewnętrznego jest tam niedozwolone.
     *
     * Wywołuj na świeżym EntityManagerze, zanim sala trafi do kontekstu persystencji.
     *
     * @return sala albo null, gdy nie istnieje
     */
    public TrainingRoomEnt findByIdForUpdate(UUID id) {
        List<?> locked = em
                .createNativeQuery("select id from training_room where id = :id for update")
                .setParameter("id", id)
                .getResultList();
        if (locked.isEmpty()) {
            return null;
        }
        return em.find(TrainingRoomEnt.class, id);
    }
}

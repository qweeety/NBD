package jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Jedna jednostka pracy = jeden EntityManager + jedna transakcja (RESOURCE_LOCAL).
 * Commit przy sukcesie, rollback przy dowolnym wyjątku, który jest przekazywany dalej bez opakowywania.
 * Poziom izolacji ustawia persistence.xml (READ_COMMITTED).
 */
public class JpaTransactions {

    private final EntityManagerFactory emf;

    public JpaTransactions(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public <T> T call(Function<EntityManager, T> work) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = work.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void run(Consumer<EntityManager> work) {
        call(em -> {
            work.accept(em);
            return null;
        });
    }
}

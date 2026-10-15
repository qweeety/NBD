package jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Fabryka dla testów: ta sama jednostka persystencji co w aplikacji, ale na bazie nbd_test
 * i ze schematem tworzonym od zera (create-drop). Wymaga uruchomionego Docker/docker-compose.yml.
 * Adres bazy można nadpisać: -Dnbd.test.db.url=jdbc:postgresql://host:5432/nbd_test
 */
final class TestEntityManagerFactory {

    private TestEntityManagerFactory() { }

    static EntityManagerFactory create() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("jakarta.persistence.jdbc.url",
                System.getProperty("nbd.test.db.url", "jdbc:postgresql://localhost:5433/nbd_test"));
        overrides.put("hibernate.hbm2ddl.auto", "create-drop");
        return Persistence.createEntityManagerFactory("gymPU", overrides);
    }
}

package jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

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

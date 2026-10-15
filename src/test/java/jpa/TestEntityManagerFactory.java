package jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

class TestEntityManagerFactory {

    static EntityManagerFactory create() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:5433/nbd_test");
        overrides.put("hibernate.hbm2ddl.auto", "create");
        return Persistence.createEntityManagerFactory("gymPU", overrides);
    }
}
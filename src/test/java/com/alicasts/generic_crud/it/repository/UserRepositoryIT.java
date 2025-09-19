package com.alicasts.generic_crud.it.repository;

import com.alicasts.generic_crud.model.Sex;
import com.alicasts.generic_crud.model.User;
import com.alicasts.generic_crud.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    UserRepository repo;

    @Autowired
    EntityManager em;

    @Test
    void save_shouldNormalizeAndPersist_uniqueChecksWork() {
        User u = new User(
                "Ana Silva",
                "Ana.SILVA+TEST@example.com",
                28,
                "123.456.789-01",
                "88.000-000",
                "Rua X, 100",
                Sex.FEMALE
        );

        User saved = repo.save(u);

        em.flush();
        em.clear();

        User reloaded = repo.findById(saved.getId()).orElseThrow();

        assertThat(reloaded.getEmail()).isEqualTo("ana.silva+test@example.com");
        assertThat(reloaded.getCpf()).isEqualTo("12345678901");
        assertThat(reloaded.getCep()).isEqualTo("88000000");

        assertThat(repo.existsByEmail("ana.silva+test@example.com")).isTrue();
        assertThat(repo.existsByCpf("12345678901")).isTrue();
    }

    @Test
    void unique_violation_onDuplicateEmail() {
        repo.save(new User("Ana", "ana@example.com", 28, "12345678901", "88000000", "Rua X, 100", Sex.FEMALE));

        User dupEmail = new User("Bia", "ANA@example.com", 30, "99999999999", "11000000", "Rua Y, 200", Sex.FEMALE);

        assertThatThrownBy(() -> repo.saveAndFlush(dupEmail))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void unique_violation_onDuplicateCpf() {
        repo.save(new User("Ana", "ana@example.com", 28, "12345678901", "88000000", "Rua X, 100", Sex.FEMALE));

        User dupCpf = new User("Carla", "carla@example.com", 30, "123.456.789-01", "22000000", "Rua Z, 300", Sex.FEMALE);

        assertThatThrownBy(() -> repo.saveAndFlush(dupCpf))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

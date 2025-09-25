package com.alicasts.generic_crud.service;

import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.api.mapper.UserMapper;
import com.alicasts.generic_crud.model.Sex;
import com.alicasts.generic_crud.model.User;
import com.alicasts.generic_crud.repository.UserRepository;
import com.alicasts.generic_crud.service.exception.ResourceConflictException;
import com.alicasts.generic_crud.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService service;

    private static UserCreateRequestDTO req(String name, String email, Integer age,
                                            String cpf, String cep, String address, Sex sex) {
        try {
            UserCreateRequestDTO r = new UserCreateRequestDTO();
            set(r, "name", name);
            set(r, "email", email);
            set(r, "age", age);
            set(r, "cpf", cpf);
            set(r, "cep", cep);
            set(r, "address", address);
            set(r, "sex", sex);
            return r;
        } catch (Exception e) {
            throw new RuntimeException("Exception:", e);
        }
    }

    private static void set(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void create_happyPath_normalizesForPrecheck_andPersistsOriginals() throws Exception {
        var body = req(
                "Ana Silva",
                "Ana@Example.com",
                28,
                "123.456.789-01",
                "88000-000",
                "Rua X, 100",
                Sex.FEMALE
        );

        when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(userRepository.existsByCpf("12345678901")).thenReturn(false);

        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            User saved = new User(
                    u.getName(),
                    "ana@example.com",
                    u.getAge(),
                    "12345678901",
                    "88000000",
                    u.getAddress(),
                    u.getSex()
            );
            set(saved, "id", 1L);
            set(saved, "createdAt", Instant.parse("2025-09-16T13:00:00Z"));
            set(saved, "updatedAt", Instant.parse("2025-09-16T13:00:00Z"));
            return saved;
        });

        when(userMapper.toResponse(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new UserResponseDTO(
                    u.getId(),
                    u.getName(),
                    u.getEmail(),
                    u.getAge(),
                    u.getCpf(),
                    u.getCep(),
                    u.getAddress(),
                    u.getSex(),
                    u.getCreatedAt(),
                    u.getUpdatedAt()
            );
        });

        UserResponseDTO out = service.create(body);

        assertNotNull(out);
        assertEquals(1L, out.id());
        assertEquals("ana@example.com", out.email());
        assertEquals("12345678901", out.cpf());
        assertEquals("88000000", out.cep());

        verify(userRepository).existsByEmail("ana@example.com");
        verify(userRepository).existsByCpf("12345678901");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User sent = captor.getValue();
        assertEquals("Ana@Example.com", sent.getEmail());
        assertEquals("123.456.789-01", sent.getCpf());
        assertEquals("88000-000", sent.getCep());
    }

    @Test
    void create_emailConflict_returnsListWithEmail() {
        var body = req("Ana", "Ana@Example.com", 28, "123.456.789-01", "88000-000", "Rua X", Sex.FEMALE);

        when(userRepository.existsByEmail("ana@example.com")).thenReturn(true);
        when(userRepository.existsByCpf("12345678901")).thenReturn(false);

        ResourceConflictException ex =
                assertThrows(ResourceConflictException.class, () -> service.create(body));

        assertEquals(List.of("email"), ex.getFields());
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_cpfConflict_returnsListWithCpf() {
        var body = req("Ana", "Ana@Example.com", 28, "123.456.789-01", "88000-000", "Rua X", Sex.FEMALE);

        when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(userRepository.existsByCpf("12345678901")).thenReturn(true);

        ResourceConflictException ex =
                assertThrows(ResourceConflictException.class, () -> service.create(body));

        assertEquals(List.of("cpf"), ex.getFields());
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_bothConflicts_returnsBothFields() {
        var body = req("Ana", "Ana@Example.com", 28, "123.456.789-01", "88000-000", "Rua X", Sex.FEMALE);

        when(userRepository.existsByEmail("ana@example.com")).thenReturn(true);
        when(userRepository.existsByCpf("12345678901")).thenReturn(true);

        ResourceConflictException ex =
                assertThrows(ResourceConflictException.class, () -> service.create(body));

        assertEquals(List.of("email", "cpf"), ex.getFields());
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_dbUniqueViolation_fallback409Generic_fieldsEmpty() {
        var body = req("Ana", "Ana@Example.com", 28, "123.456.789-01", "88000-000", "Rua X", Sex.FEMALE);

        when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(userRepository.existsByCpf("12345678901")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        ResourceConflictException ex =
                assertThrows(ResourceConflictException.class, () -> service.create(body));

        assertTrue(ex.getFields().isEmpty());
    }

    @Test
    void findAll_mapsContent_andClampsSize() throws Exception {
        Pageable input = PageRequest.of(0, 999, Sort.by(Sort.Order.desc("createdAt")));

        var u = new User("Ana", "ana@example.com", 28, "123", "88000000", "Rua X", Sex.FEMALE);
        set(u, "id", 1L);
        set(u, "createdAt", Instant.parse("2025-09-16T13:00:00Z"));
        set(u, "updatedAt", Instant.parse("2025-09-16T13:00:00Z"));

        when(userRepository.findAll(any(Pageable.class)))
                .thenAnswer(inv -> new PageImpl<>(List.of(u), inv.getArgument(0), 1));

        when(userMapper.toResponse(u)).thenReturn(new UserResponseDTO(
                1L, "Ana", "ana@example.com", 28, "123", "88000000", "Rua X",
                Sex.FEMALE, u.getCreatedAt(), u.getUpdatedAt()
        ));

        var out = service.findAll(input);

        assertThat(out.size()).isEqualTo(100);
        assertThat(out.totalElements()).isEqualTo(1);
        assertThat(out.content()).hasSize(1);
        assertThat(out.content().getFirst().email()).isEqualTo("ana@example.com");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAll(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(100);
    }

}

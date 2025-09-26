package com.alicasts.generic_crud.service.guard;

import com.alicasts.generic_crud.repository.UserRepository;
import com.alicasts.generic_crud.service.exception.ResourceConflictException;
import com.alicasts.generic_crud.util.Normalizer;

import java.util.ArrayList;
import java.util.List;

public class UserUniquenessGuard {

    private final UserRepository userRepository;

    public UserUniquenessGuard(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void checkOnCreate(String email, String cpf) {
        String normEmail = Normalizer.email(email);
        String normCpf   = Normalizer.digitsOnly(cpf);

        List<String> conflicts = new ArrayList<>(2);
        if (userRepository.existsByEmail(normEmail)) conflicts.add("email");
        if (userRepository.existsByCpf(normCpf))     conflicts.add("cpf");

        if (!conflicts.isEmpty()) throw new ResourceConflictException(conflicts);
    }

    public void checkCpfOnUpdate(String newCpf, String currentCpf) {
        if (newCpf == null) return;

        String normNew = Normalizer.digitsOnly(newCpf);
        String normCur = Normalizer.digitsOnly(currentCpf);

        if (!normNew.equals(normCur) && userRepository.existsByCpf(normNew)) {
            throw new ResourceConflictException(List.of("cpf"));
        }
    }
}

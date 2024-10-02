package org.ukma.userserver.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ukma.userserver.user.models.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findUserEntityByEmail(String email);

    List<UserEntity> findAllByRole(Role role);

    Page<UserEntity> findAllByRole(Role role, Pageable pageable);
    Optional<UserEntity> findUserEntityByPhoneNumber(String phoneNum);
}

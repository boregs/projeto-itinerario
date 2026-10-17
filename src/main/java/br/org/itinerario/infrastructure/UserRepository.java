package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.User;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.email = :email")
    Optional<User> lockByEmail(String email);

    Optional<User> findByEmail(String email);
    Page<User> findByMentorProfileApprovalStatus(User.Approval status, Pageable pageable);
    Page<User> findByRoleAndStatusAndMentorProfileApprovalStatus(
        User.Role role,
        User.Status status,
        User.Approval approval,
        Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id")
    Optional<User> lockById(UUID id);
}

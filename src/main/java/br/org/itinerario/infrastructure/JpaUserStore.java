package br.org.itinerario.infrastructure;

import br.org.itinerario.application.UserStore;
import br.org.itinerario.domain.User;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaUserStore implements UserStore {

    private final UserRepository repository;

    public JpaUserStore(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public User save(User user) {
        return repository.saveAndFlush(user);
    }
}

package br.org.itinerario.application;

import br.org.itinerario.domain.User;
import java.util.Optional;

public interface UserStore {
    Optional<User> findByEmail(String email);
    User save(User user);
}

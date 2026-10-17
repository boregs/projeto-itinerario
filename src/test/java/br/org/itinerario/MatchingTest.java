package br.org.itinerario;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.itinerario.application.Matching;
import org.junit.jupiter.api.Test;

class MatchingTest {

    @Test
    void matchesExactNormalizedTokensAndAvailability() {
        assertThat(
            Matching.compatible("Matemática / Lógica", "Manhã; Tarde", " LÓGICA ", "tarde")
        ).isTrue();
        assertThat(Matching.compatible("Biologia", "Tarde", "logia", "Tarde")).isFalse();
        assertThat(Matching.compatible("Lógica", "Manhã", "Lógica", "Tarde")).isFalse();
    }
}

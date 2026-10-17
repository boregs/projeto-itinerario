package br.org.itinerario.application;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public final class Matching {

    private Matching() {}

    public static String normalize(String value) {
        return Normalizer.normalize(value.strip(), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT)
            .replaceAll("\\s+", " ");
    }

    public static Set<String> tokens(String value) {
        return Arrays.stream(value.split("[,;/]", -1))
            .map(Matching::normalize)
            .filter(token -> !token.isBlank())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static boolean compatible(
        String areas,
        String availability,
        String area,
        String schedule
    ) {
        return (
            tokens(areas).contains(normalize(area)) &&
            tokens(availability).contains(normalize(schedule))
        );
    }
}

package br.org.itinerario.application;

import jakarta.validation.*;
import java.util.regex.Pattern;

public class SafeTextValidator implements ConstraintValidator<SafeText, String> {

    private static final Pattern CONTACT = Pattern.compile(
        "(?i)(@|https?://|www\\.|whatsapp|instagram|telegram|tiktok|facebook|(?:\\+?\\d[\\s().-]*){8,})"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !CONTACT.matcher(value).find();
    }

    public static String display(String value) {
        return value != null && CONTACT.matcher(value).find()
            ? "[Conteúdo com possível contato pessoal ocultado]"
            : value;
    }
}

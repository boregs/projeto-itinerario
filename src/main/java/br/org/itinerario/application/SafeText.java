package br.org.itinerario.application;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.RECORD_COMPONENT,
    ElementType.TYPE_USE,
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SafeTextValidator.class)
public @interface SafeText {
    String message() default "Não inclua telefone, e-mail, links ou redes sociais neste campo.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

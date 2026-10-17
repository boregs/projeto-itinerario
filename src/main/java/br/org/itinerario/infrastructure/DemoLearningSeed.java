package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.demo-content", havingValue = "true")
public class DemoLearningSeed implements ApplicationRunner {

    private final LearningTrackRepository tracks;
    private final LearningModuleRepository modules;
    private final ActivityRepository activities;
    private final ObjectMapper json;

    public DemoLearningSeed(
        LearningTrackRepository tracks,
        LearningModuleRepository modules,
        ActivityRepository activities,
        ObjectMapper json
    ) {
        this.tracks = tracks;
        this.modules = modules;
        this.activities = activities;
        this.json = json;
    }

    private static UUID id(String key) {
        return UUID.nameUUIDFromBytes(("itinerario-demo-" + key).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (tracks.existsById(id("logic"))) return;
        var track = new LearningTrack();
        track.id = id("logic");
        track.title = "Introdução à Lógica e Algoritmos";
        track.description =
            "Aprenda a organizar instruções, usar variáveis e tomar decisões com exemplos do cotidiano.";
        tracks.save(track);
        seedModule(
            track,
            0,
            "O que é um algoritmo?",
            "Um algoritmo é uma sequência finita de instruções para resolver um problema. Exemplo: para preparar um suco, separe os ingredientes, misture e sirva.",
            List.of(
                new Question(
                    "Um algoritmo é…",
                    List.of(
                        "Uma sequência de instruções",
                        "Um equipamento",
                        "Um resultado aleatório"
                    ),
                    0,
                    "As instruções descrevem como resolver um problema."
                ),
                new Question(
                    "Qual ordem faz sentido para enviar uma carta?",
                    List.of(
                        "Enviar, escrever, fechar",
                        "Escrever, fechar, enviar",
                        "Fechar, enviar, escrever"
                    ),
                    1,
                    "É preciso escrever antes de fechar e enviar a carta."
                ),
                new Question(
                    "Qual instrução é mais precisa?",
                    List.of("Faça algo", "Misture por um minuto", "Continue por algum tempo"),
                    1,
                    "Uma duração definida torna a instrução menos ambígua."
                )
            )
        );
        seedModule(
            track,
            1,
            "Variáveis e sequência",
            "Uma variável guarda um valor que pode mudar. Se pontos recebe 2 e depois pontos recebe pontos + 3, seu valor passa a ser 5.",
            List.of(
                new Question(
                    "Se x recebe 4 e depois x recebe x + 2, quanto vale x?",
                    List.of("2", "4", "6"),
                    2,
                    "A atualização soma 2 ao valor anterior: 4 + 2 = 6."
                ),
                new Question(
                    "Qual é o papel de uma variável?",
                    List.of(
                        "Guardar um valor",
                        "Interromper sempre o programa",
                        "Impedir alterações"
                    ),
                    0,
                    "Variáveis representam informações usadas pelo algoritmo."
                ),
                new Question(
                    "Se a recebe 3, b recebe a e a recebe 8, quanto vale b?",
                    List.of("8", "3", "11"),
                    1,
                    "b recebeu o valor 3 naquele momento; mudar a depois não altera b."
                )
            )
        );
        seedModule(
            track,
            2,
            "Condicionais",
            "Uma condição permite escolher ações. Exemplo: se estiver chovendo, levar guarda-chuva; senão, seguir sem ele.",
            List.of(
                new Question(
                    "Se idade é 16, a condição idade >= 18 é…",
                    List.of("Verdadeira", "Falsa"),
                    1,
                    "16 é menor que 18."
                ),
                new Question(
                    "Se pontos é 10 e a regra é pontos > 10, a regra foi atendida?",
                    List.of("Sim", "Não"),
                    1,
                    "Maior que é diferente de maior ou igual: 10 não é maior que 10."
                ),
                new Question(
                    "A condição A E B é verdadeira quando…",
                    List.of(
                        "Apenas A é verdadeira",
                        "Apenas B é verdadeira",
                        "As duas são verdadeiras"
                    ),
                    2,
                    "O operador E exige que ambas as condições sejam verdadeiras."
                )
            )
        );
        seedModule(
            track,
            3,
            "Desafio final",
            "Combine sequência, variáveis e condições. Leia uma instrução por vez e acompanhe o valor de cada variável.",
            List.of(
                new Question(
                    "x recebe 2; x recebe x + 3; se x > 4, x recebe x * 2. Qual é o valor final?",
                    List.of("5", "7", "10"),
                    2,
                    "x passa de 2 para 5. Como 5 > 4, é multiplicado por 2, chegando a 10."
                )
            )
        );
    }

    private record Question(
        String question,
        List<String> options,
        int answer,
        String explanation
    ) {}

    private void seedModule(
        LearningTrack track,
        int position,
        String title,
        String text,
        List<Question> questions
    ) {
        var module = new LearningModule();
        module.id = id("module-" + position);
        module.trackId = track.id;
        module.title = title;
        module.content = text;
        module.position = position;
        modules.save(module);
        for (int index = 0; index < questions.size(); index++) {
            var question = questions.get(index);
            var activity = new Activity();
            activity.id = id("activity-" + position + "-" + index);
            activity.moduleId = module.id;
            activity.title = position == 3 ? "Desafio final" : "Atividade " + (index + 1);
            activity.type =
                position == 3 ? Activity.Type.LOGIC_CHALLENGE : Activity.Type.MULTIPLE_CHOICE;
            activity.question = question.question();
            activity.correctAnswer = question.answer();
            activity.explanation = question.explanation();
            activity.points = 10;
            activity.position = index;
            try {
                activity.optionsJson = json.writeValueAsString(question.options());
            } catch (JsonProcessingException error) {
                throw new IllegalStateException("Falha ao preparar conteúdo inicial.");
            }
            activities.save(activity);
        }
    }
}

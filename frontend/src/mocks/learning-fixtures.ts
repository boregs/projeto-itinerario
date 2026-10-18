import type { ActivityFixture, LearningModule, LearningTrack } from "@/types/learning";

export const tracks: LearningTrack[] = [
  {
    id: "logic",
    title: "Introdução à Lógica e Algoritmos",
    subject: "Tecnologia",
    description: "Aprenda a transformar problemas em passos claros, decisões e repetições.",
    accent: "violet",
    moduleIds: ["logic-start", "logic-decisions", "logic-loops", "logic-final"],
  },
  {
    id: "math",
    title: "Matemática em movimento",
    subject: "Matemática",
    description: "Equações e porcentagens conectadas a situações do dia a dia.",
    accent: "blue",
    moduleIds: ["math-everyday"],
  },
  {
    id: "history",
    title: "História em perspectiva",
    subject: "História",
    description: "Marcos históricos e transformações que ajudam a ler o presente.",
    accent: "amber",
    moduleIds: ["history-transformations"],
  },
  {
    id: "chemistry",
    title: "Química ao redor",
    subject: "Química",
    description: "Partículas e transformações presentes em materiais cotidianos.",
    accent: "green",
    moduleIds: ["chemistry-matter"],
  },
  {
    id: "biology",
    title: "Biologia da vida",
    subject: "Biologia",
    description: "Conheça processos fundamentais que mantêm as células vivas.",
    accent: "coral",
    moduleIds: ["biology-cells"],
  },
];

export const modules: LearningModule[] = [
  {
    id: "logic-start",
    trackId: "logic",
    title: "Pensar em passos",
    eyebrow: "Módulo 1",
    content: "Um algoritmo é uma sequência organizada de instruções para alcançar um objetivo.",
    activityIds: ["logic-algorithm", "logic-sequence", "logic-variable"],
  },
  {
    id: "logic-decisions",
    trackId: "logic",
    title: "Tomar decisões",
    eyebrow: "Módulo 2",
    content: "Condições permitem que um algoritmo escolha caminhos de acordo com uma situação.",
    activityIds: ["logic-condition", "logic-boolean", "logic-branch"],
  },
  {
    id: "logic-loops",
    trackId: "logic",
    title: "Repetir com propósito",
    eyebrow: "Módulo 3",
    content: "Repetições evitam escrever muitas vezes a mesma instrução.",
    activityIds: ["logic-loop", "logic-counter", "logic-stop"],
  },
  {
    id: "logic-final",
    trackId: "logic",
    title: "Desafio final",
    eyebrow: "Módulo 4",
    content: "Combine sequência, decisão e repetição para analisar um pequeno algoritmo.",
    activityIds: ["logic-final-challenge"],
  },
  {
    id: "math-everyday",
    trackId: "math",
    title: "Números no cotidiano",
    eyebrow: "Módulo único",
    content: "Use relações algébricas e percentuais para resolver situações práticas.",
    activityIds: ["math-linear-equation", "math-percentage"],
  },
  {
    id: "history-transformations",
    trackId: "history",
    title: "Independência e indústria",
    eyebrow: "Módulo único",
    content:
      "Observe eventos como processos históricos, conectados a mudanças políticas, sociais e econômicas.",
    activityIds: ["history-brazil-independence", "history-industrial-revolution"],
  },
  {
    id: "chemistry-matter",
    trackId: "chemistry",
    title: "Matéria e transformação",
    eyebrow: "Módulo único",
    content: "A estrutura dos átomos e as transformações ajudam a explicar os materiais.",
    activityIds: ["chemistry-atomic-number", "chemistry-transformation"],
  },
  {
    id: "biology-cells",
    trackId: "biology",
    title: "Energia nas células",
    eyebrow: "Módulo único",
    content: "Células transformam matéria e energia por processos fundamentais à vida.",
    activityIds: ["biology-cellular-respiration", "biology-photosynthesis"],
  },
];

const logicActivities: ActivityFixture[] = [
  [
    "logic-algorithm",
    "O que é um algoritmo?",
    "Qual opção descreve melhor um algoritmo?",
    [
      "Um erro de computador",
      "Uma sequência de instruções",
      "Apenas um programa pronto",
      "Uma peça do computador",
    ],
    1,
    "Um algoritmo organiza instruções finitas para alcançar um objetivo.",
  ],
  [
    "logic-sequence",
    "Ordem das instruções",
    "Para preparar suco, qual passo deve acontecer primeiro?",
    ["Servir no copo", "Lavar a fruta", "Beber o suco", "Guardar a jarra"],
    1,
    "A sequência precisa começar preparando com segurança os ingredientes.",
  ],
  [
    "logic-variable",
    "Caixas de informação",
    "Em um algoritmo, para que serve uma variável?",
    ["Guardar um valor", "Apagar o programa", "Desenhar a tela", "Impedir decisões"],
    0,
    "Variáveis guardam valores que podem ser consultados ou alterados.",
  ],
  [
    "logic-condition",
    "Se acontecer...",
    "Qual estrutura escolhe um caminho conforme uma condição?",
    ["Variável", "Sequência", "Condicional", "Comentário"],
    2,
    "Uma condicional executa caminhos diferentes conforme verdadeiro ou falso.",
  ],
  [
    "logic-boolean",
    "Verdadeiro ou falso",
    "A expressão 8 > 3 resulta em:",
    ["Verdadeiro", "Falso"],
    0,
    "Oito é maior que três, portanto a condição é verdadeira.",
  ],
  [
    "logic-branch",
    "Escolha de caminho",
    "Se está chovendo, leve guarda-chuva. Está chovendo. O que fazer?",
    ["Levar guarda-chuva", "Ignorar a condição", "Repetir para sempre", "Trocar o valor de chuva"],
    0,
    "A condição foi satisfeita, então a instrução associada deve ser executada.",
  ],
  [
    "logic-loop",
    "Repetições",
    "Qual estrutura é mais adequada para repetir uma ação cinco vezes?",
    ["Laço de repetição", "Título", "Imagem", "Variável sem alteração"],
    0,
    "Um laço controla a repetição sem duplicar instruções.",
  ],
  [
    "logic-counter",
    "Contador",
    "Se contador começa em 0 e soma 1 três vezes, qual valor final?",
    ["1", "2", "3", "4"],
    2,
    "Partindo de zero, três incrementos resultam em três.",
  ],
  [
    "logic-stop",
    "Hora de parar",
    "Por que um laço precisa de uma condição de parada?",
    [
      "Para evitar repetição infinita",
      "Para criar uma senha",
      "Para mudar a tela",
      "Para esconder o resultado",
    ],
    0,
    "A condição de parada garante que a repetição termine no momento esperado.",
  ],
  [
    "logic-final-challenge",
    "Desafio da pontuação",
    "Pontos começam em 2. Repita duas vezes: some 3. Se o total for maior que 7, mostre “Avançou”. O que acontece?",
    [
      "Mostra “Avançou” com 8 pontos",
      "Mostra “Avançou” com 5 pontos",
      "Não mostra nada com 8 pontos",
      "Repete para sempre",
    ],
    0,
    "Duas somas produzem 2 + 3 + 3 = 8; como 8 é maior que 7, a mensagem aparece.",
  ],
].map(
  ([id, title, question, options, correctAnswer, explanation]) =>
    ({
      id,
      moduleId: moduleForActivity(String(id)),
      title,
      question,
      options,
      correctAnswer,
      explanation,
      points: 10,
    }) as ActivityFixture,
);

function moduleForActivity(id: string) {
  if (["logic-algorithm", "logic-sequence", "logic-variable"].includes(id)) return "logic-start";
  if (["logic-condition", "logic-boolean", "logic-branch"].includes(id)) return "logic-decisions";
  if (["logic-loop", "logic-counter", "logic-stop"].includes(id)) return "logic-loops";
  return "logic-final";
}

export const activities: ActivityFixture[] = [
  ...logicActivities,
  {
    id: "math-linear-equation",
    moduleId: "math-everyday",
    title: "Equação do primeiro grau",
    question: "2x + 5 = 17. Qual é o valor de x?",
    options: ["4", "5", "6", "7"],
    correctAnswer: 2,
    explanation: "Subtraindo 5 dos dois lados, 2x = 12; dividindo por 2, x = 6.",
    points: 10,
  },
  {
    id: "math-percentage",
    moduleId: "math-everyday",
    title: "Porcentagem no cotidiano",
    question: "Um produto de R$ 80 recebeu 25% de desconto. Qual é o preço final?",
    options: ["R$ 20", "R$ 55", "R$ 60", "R$ 65"],
    correctAnswer: 2,
    explanation: "25% de 80 é 20; portanto, 80 - 20 = 60.",
    points: 10,
  },
  {
    id: "history-brazil-independence",
    moduleId: "history-transformations",
    title: "Independência do Brasil",
    question: "Qual ano é tradicionalmente associado à declaração da Independência do Brasil?",
    options: ["1789", "1808", "1822", "1889"],
    correctAnswer: 2,
    explanation:
      "A declaração ocorreu em 7 de setembro de 1822; o processo envolveu acontecimentos anteriores e posteriores.",
    points: 10,
  },
  {
    id: "history-industrial-revolution",
    moduleId: "history-transformations",
    title: "Revolução Industrial",
    question: "Qual transformação está diretamente associada à Revolução Industrial?",
    options: [
      "Expansão da produção mecanizada em fábricas",
      "Fim imediato do trabalho urbano",
      "Retorno geral à produção artesanal",
      "Desaparecimento do comércio internacional",
    ],
    correctAnswer: 0,
    explanation:
      "A mecanização e a expansão do sistema fabril alteraram a produção, o trabalho e a urbanização.",
    points: 10,
  },
  {
    id: "chemistry-atomic-number",
    moduleId: "chemistry-matter",
    title: "Número atômico",
    question: "Um átomo possui 6 prótons. Qual é seu número atômico?",
    options: ["3", "6", "12", "18"],
    correctAnswer: 1,
    explanation: "O número atômico corresponde à quantidade de prótons no núcleo.",
    points: 10,
  },
  {
    id: "chemistry-transformation",
    moduleId: "chemistry-matter",
    title: "Transformações químicas",
    question: "Qual situação representa uma transformação química?",
    options: ["Gelo derretendo", "Papel sendo cortado", "Ferro enferrujando", "Água evaporando"],
    correctAnswer: 2,
    explanation:
      "A ferrugem forma novas substâncias pela reação do ferro com o ambiente; os demais exemplos são mudanças físicas.",
    points: 10,
  },
  {
    id: "biology-cellular-respiration",
    moduleId: "biology-cells",
    title: "Estruturas celulares",
    question:
      "Qual organela é principalmente responsável pela respiração celular aeróbica em células eucarióticas?",
    options: ["Ribossomo", "Mitocôndria", "Lisossomo", "Complexo golgiense"],
    correctAnswer: 1,
    explanation:
      "As mitocôndrias realizam etapas centrais da respiração aeróbica e da produção de ATP.",
    points: 10,
  },
  {
    id: "biology-photosynthesis",
    moduleId: "biology-cells",
    title: "Fotossíntese",
    question: "Qual gás é liberado na fotossíntese oxigênica?",
    options: ["Nitrogênio", "Gás carbônico", "Metano", "Oxigênio"],
    correctAnswer: 3,
    explanation: "Na etapa luminosa, moléculas de água participam de reações que liberam oxigênio.",
    points: 10,
  },
];

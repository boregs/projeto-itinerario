import type { DemoIdentity, PlatformState } from "@/types/platform";

export const identities: DemoIdentity[] = [
  {
    id: "student-luana",
    role: "STUDENT",
    name: "Luana Silva",
    subtitle: "Estudante · 2º ano",
    initials: "LS",
  },
  {
    id: "mentor-rafael",
    role: "MENTOR",
    name: "Rafael Nunes",
    subtitle: "Mentor aprovado · Matemática e Lógica",
    initials: "RN",
  },
  {
    id: "mentor-carolina",
    role: "MENTOR",
    name: "Carolina Alves",
    subtitle: "Mentora aguardando aprovação",
    initials: "CA",
  },
  {
    id: "admin-marina",
    role: "ADMIN",
    name: "Marina Costa",
    subtitle: "Administradora da plataforma",
    initials: "MC",
  },
];

export const initialPlatformState: PlatformState = {
  version: 1,
  sessionId: "student-luana",
  users: identities.map((identity) => ({
    ...identity,
    email: `${identity.id}@demo.local`,
    status: "ACTIVE",
  })),
  mentors: [
    {
      userId: "mentor-rafael",
      bio: "Universitário de Engenharia que gosta de tornar problemas difíceis mais claros.",
      expertiseAreas: "Matemática, Lógica",
      availability: "Tarde, Noite",
      experience: "Monitor de cálculo e voluntário em olimpíadas escolares.",
      approvalStatus: "APPROVED",
      completedMentorships: 3,
    },
    {
      userId: "mentor-carolina",
      bio: "Professora de Ciências interessada em aprendizagem prática.",
      expertiseAreas: "Química, Biologia",
      availability: "Manhã, Tarde",
      experience: "Professora do Ensino Médio há quatro anos.",
      approvalStatus: "PENDING_APPROVAL",
      completedMentorships: 0,
    },
  ],
  requests: [
    {
      id: "request-conditionals",
      studentId: "student-luana",
      studentName: "Luana Silva",
      subject: "Entender condicionais",
      area: "Lógica",
      description:
        "Consigo entender exemplos, mas tenho dificuldade para criar minhas próprias condições.",
      objective: "Resolver exercícios de se e senão.",
      preferredSchedule: "Tarde",
      status: "OPEN",
      createdAt: "2026-10-17T15:00:00.000Z",
    },
  ],
  mentorships: [
    {
      id: "mentorship-fractions",
      requestId: "request-fractions",
      studentId: "student-luana",
      studentName: "Luana Silva",
      mentorId: "mentor-rafael",
      mentorName: "Rafael Nunes",
      subject: "Revisão de frações",
      area: "Matemática",
      scheduledAt: "2026-10-22T18:00:00.000Z",
      status: "MATCHED",
    },
    {
      id: "mentorship-equations",
      requestId: "request-equations",
      studentId: "student-luana",
      studentName: "Luana Silva",
      mentorId: "mentor-rafael",
      mentorName: "Rafael Nunes",
      subject: "Equações do primeiro grau",
      area: "Matemática",
      scheduledAt: "2026-10-10T17:00:00.000Z",
      status: "COMPLETED",
      summary: "Revisamos operações inversas e resolvemos três equações progressivamente.",
    },
  ],
  reports: [
    {
      id: "report-example",
      reporterName: "Usuário demonstrativo",
      target: "Conteúdo de atividade",
      reason: "Conteúdo impreciso",
      description: "A explicação pode ser mais clara para estudantes iniciantes.",
      status: "OPEN",
      createdAt: "2026-10-18T10:00:00.000Z",
    },
  ],
};

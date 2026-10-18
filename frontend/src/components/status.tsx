import type { ApprovalStatus, ReportStatus, RequestStatus, UserStatus } from "@/types/platform";

const labels: Record<ApprovalStatus | ReportStatus | RequestStatus | UserStatus, string> = {
  ACTIVE: "Ativo",
  BLOCKED: "Bloqueado",
  PENDING_APPROVAL: "Aguardando aprovação",
  APPROVED: "Aprovado",
  REJECTED: "Rejeitado",
  OPEN: "Aberto",
  MATCHED: "Agendado",
  IN_PROGRESS: "Em andamento",
  COMPLETED: "Concluído",
  CANCELLED: "Cancelado",
  UNDER_REVIEW: "Em análise",
  RESOLVED: "Resolvido",
  DISMISSED: "Descartado",
};

export function Status({ value }: { value: keyof typeof labels }) {
  return <span className={`status status-${value.toLowerCase()}`}>{labels[value]}</span>;
}

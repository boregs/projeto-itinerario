"use client";

import { CalendarCheck, CheckCircle2, Inbox } from "lucide-react";
import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { Status } from "@/components/status";
import { platformService } from "@/services/platform-service";

export default function MentorDashboard() {
  const profile = useQuery({
    queryKey: ["mentor-profile"],
    queryFn: () => platformService.getMentorProfile(),
  });
  const stats = useQuery({
    queryKey: ["mentor-dashboard"],
    queryFn: () => platformService.getDashboard("MENTOR"),
  });
  if (!profile.data || !stats.data) return <LoadingState label="Preparando painel" />;
  return (
    <div className="dashboard">
      <section className="welcome">
        <div>
          <p className="eyebrow">Área do mentor</p>
          <h1 className="title">
            Olá, {profile.data.name.split(" ")[0]}.{" "}
            {profile.data.approvalStatus === "APPROVED"
              ? "Há estudantes esperando orientação."
              : "Seu perfil está em análise."}
          </h1>
        </div>
        <Status value={profile.data.approvalStatus} />
      </section>
      {profile.data.approvalStatus !== "APPROVED" && (
        <div className="error-box">
          <strong>Suas ações estão temporariamente limitadas.</strong>
          <p>Um administrador precisa revisar seu perfil antes de novas mentorias.</p>
        </div>
      )}
      <div className="metric-grid">
        <Link className="card metric" href="/mentor/solicitacoes">
          <Inbox aria-hidden="true" />
          <strong>{stats.data.pendingRequests}</strong>
          <span>solicitações compatíveis</span>
        </Link>
        <Link className="card metric" href="/mentor/mentorias">
          <CalendarCheck aria-hidden="true" />
          <strong>{stats.data.activeMentorships}</strong>
          <span>mentorias ativas</span>
        </Link>
        <div className="card metric">
          <CheckCircle2 aria-hidden="true" />
          <strong>{stats.data.completedMentorships}</strong>
          <span>mentorias concluídas</span>
        </div>
      </div>
    </div>
  );
}

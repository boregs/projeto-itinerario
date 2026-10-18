"use client";

import { Flag, HandHeart, ShieldCheck, Users } from "lucide-react";
import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { platformService } from "@/services/platform-service";

export default function AdminDashboard() {
  const stats = useQuery({
    queryKey: ["admin-dashboard"],
    queryFn: () => platformService.getDashboard("ADMIN"),
  });
  const users = useQuery({ queryKey: ["users"], queryFn: () => platformService.listUsers() });
  const reports = useQuery({ queryKey: ["reports"], queryFn: () => platformService.listReports() });
  if (!stats.data || !users.data || !reports.data)
    return <LoadingState label="Calculando indicadores" />;
  return (
    <div className="dashboard">
      <header className="page-heading">
        <p className="eyebrow">Visão administrativa</p>
        <h1 className="title">Segurança e acompanhamento em primeiro lugar.</h1>
        <p className="lead">
          Os indicadores abaixo são calculados a partir dos dados atuais da demonstração.
        </p>
      </header>
      <div className="metric-grid admin-metrics">
        <Link className="card metric" href="/admin/mentores">
          <ShieldCheck aria-hidden="true" />
          <strong>{stats.data.pendingMentors}</strong>
          <span>mentores para revisar</span>
        </Link>
        <Link className="card metric" href="/admin/usuarios">
          <Users aria-hidden="true" />
          <strong>{users.data.length}</strong>
          <span>usuários cadastrados</span>
        </Link>
        <Link className="card metric" href="/admin/mentorias">
          <HandHeart aria-hidden="true" />
          <strong>{stats.data.activeMentorships}</strong>
          <span>mentorias ativas</span>
        </Link>
        <Link className="card metric" href="/admin/denuncias">
          <Flag aria-hidden="true" />
          <strong>{reports.data.filter((item) => item.status === "OPEN").length}</strong>
          <span>denúncias abertas</span>
        </Link>
      </div>
    </div>
  );
}

"use client";

import { ArrowLeft, Calendar, UserRound } from "lucide-react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { Status } from "@/components/status";
import { platformService } from "@/services/platform-service";

export default function StudentMentorshipPage() {
  const id = useSearchParams().get("id") ?? "";
  const query = useQuery({
    queryKey: ["mentorship", id],
    queryFn: () => platformService.getMentorship(id),
  });
  if (query.isLoading) return <LoadingState />;
  if (query.isError || !query.data)
    return <ErrorState message={query.error?.message ?? "Mentoria não encontrada"} />;
  const item = query.data;
  return (
    <div className="detail-page">
      <Link className="back-link" href="/aluno/mentorias">
        <ArrowLeft size={17} aria-hidden="true" /> Voltar às mentorias
      </Link>
      <section className="card detail-card">
        <div className="record-top">
          <span>{item.area}</span>
          <Status value={item.status} />
        </div>
        <h1 className="title">{item.subject}</h1>
        <div className="detail-facts">
          <span>
            <UserRound aria-hidden="true" /> Mentor: <strong>{item.mentorName}</strong>
          </span>
          <span>
            <Calendar aria-hidden="true" />{" "}
            {new Date(item.scheduledAt).toLocaleString("pt-BR", {
              dateStyle: "long",
              timeStyle: "short",
            })}
          </span>
        </div>
        {item.summary && (
          <div className="summary-box">
            <h2>Resumo do encontro</h2>
            <p>{item.summary}</p>
          </div>
        )}
        <p className="privacy-note">
          Por segurança, dados pessoais de contato não são exibidos entre participantes.
        </p>
      </section>
    </div>
  );
}

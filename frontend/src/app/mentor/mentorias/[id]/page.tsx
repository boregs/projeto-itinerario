"use client";

import { ArrowLeft } from "lucide-react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { Status } from "@/components/status";
import { platformService } from "@/services/platform-service";

export default function MentorMentorshipDetail() {
  const { id } = useParams<{ id: string }>();
  const client = useQueryClient();
  const [summary, setSummary] = useState("");
  const query = useQuery({
    queryKey: ["mentorship", id],
    queryFn: () => platformService.getMentorship(id),
  });
  const update = useMutation({
    mutationFn: ({
      status,
      text,
    }: {
      status: "IN_PROGRESS" | "COMPLETED" | "CANCELLED";
      text?: string;
    }) => platformService.updateMentorship(id, status, text),
    onSuccess: () => client.invalidateQueries(),
  });
  if (query.isLoading) return <LoadingState />;
  if (!query.data) return <ErrorState message="Mentoria não encontrada." />;
  const item = query.data;
  return (
    <div className="detail-page">
      <Link className="back-link" href="/mentor/mentorias">
        <ArrowLeft size={17} aria-hidden="true" /> Voltar às mentorias
      </Link>
      <section className="card detail-card">
        <div className="record-top">
          <span>{item.area}</span>
          <Status value={item.status} />
        </div>
        <h1 className="title">{item.subject}</h1>
        <p className="lead">Orientação para {item.studentName}</p>
        <dl className="profile-data">
          <div>
            <dt>Data</dt>
            <dd>{new Date(item.scheduledAt).toLocaleString("pt-BR")}</dd>
          </div>
          <div>
            <dt>Estado atual</dt>
            <dd>{item.status}</dd>
          </div>
        </dl>
        {item.status === "MATCHED" && (
          <button
            className="button"
            type="button"
            onClick={() => update.mutate({ status: "IN_PROGRESS" })}
          >
            Iniciar mentoria
          </button>
        )}
        {item.status === "IN_PROGRESS" && (
          <div className="completion-box">
            <label className="field">
              <span>Resumo estruturado da sessão</span>
              <textarea
                rows={5}
                value={summary}
                onChange={(event) => setSummary(event.target.value)}
              />
            </label>
            <button
              className="button"
              type="button"
              disabled={!summary.trim()}
              onClick={() => update.mutate({ status: "COMPLETED", text: summary })}
            >
              Concluir mentoria
            </button>
          </div>
        )}
        {["MATCHED", "IN_PROGRESS"].includes(item.status) && (
          <button
            className="button danger secondary"
            type="button"
            onClick={() => update.mutate({ status: "CANCELLED" })}
          >
            Cancelar encontro
          </button>
        )}
        {update.isError && (
          <div className="error-box" role="alert">
            {update.error.message}
          </div>
        )}
        {item.summary && (
          <div className="summary-box">
            <h2>Resumo</h2>
            <p>{item.summary}</p>
          </div>
        )}
      </section>
    </div>
  );
}

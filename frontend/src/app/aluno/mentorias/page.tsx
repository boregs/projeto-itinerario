"use client";

import { Calendar, Plus } from "lucide-react";
import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { Status } from "@/components/status";
import { platformService } from "@/services/platform-service";

export default function StudentMentorshipsPage() {
  const requests = useQuery({
    queryKey: ["student-requests"],
    queryFn: () => platformService.listStudentRequests(),
  });
  const mentorships = useQuery({
    queryKey: ["student-mentorships"],
    queryFn: () => platformService.listMentorships("STUDENT"),
  });
  if (requests.isLoading || mentorships.isLoading)
    return <LoadingState label="Carregando mentorias" />;
  if (requests.isError || mentorships.isError)
    return (
      <ErrorState
        message="Não foi possível carregar suas mentorias."
        retry={() => {
          requests.refetch();
          mentorships.refetch();
        }}
      />
    );
  return (
    <div>
      <header className="page-heading action-heading">
        <div>
          <p className="eyebrow">Apoio acadêmico</p>
          <h1 className="title">Suas mentorias</h1>
          <p className="lead">Peça ajuda, acompanhe encontros e consulte orientações anteriores.</p>
        </div>
        <Link className="button" href="/aluno/mentorias/nova">
          <Plus size={18} aria-hidden="true" /> Nova solicitação
        </Link>
      </header>
      <section className="management-section">
        <h2>Solicitações</h2>
        <div className="record-grid">
          {requests.data?.map((item) => (
            <article className="card record" key={item.id}>
              <div className="record-top">
                <span>{item.area}</span>
                <Status value={item.status} />
              </div>
              <h3>{item.subject}</h3>
              <p>{item.objective}</p>
              <small>Criada em {new Date(item.createdAt).toLocaleDateString("pt-BR")}</small>
            </article>
          ))}
        </div>
      </section>
      <section className="management-section">
        <h2>Encontros</h2>
        <div className="record-grid">
          {mentorships.data?.map((item) => (
            <Link
              className="card record"
              href={`/aluno/mentorias/detalhes?id=${encodeURIComponent(item.id)}`}
              key={item.id}
            >
              <div className="record-top">
                <span>{item.area}</span>
                <Status value={item.status} />
              </div>
              <h3>{item.subject}</h3>
              <p>Com {item.mentorName}</p>
              <small>
                <Calendar size={14} aria-hidden="true" />{" "}
                {new Date(item.scheduledAt).toLocaleString("pt-BR", {
                  dateStyle: "medium",
                  timeStyle: "short",
                })}
              </small>
            </Link>
          ))}
        </div>
      </section>
    </div>
  );
}

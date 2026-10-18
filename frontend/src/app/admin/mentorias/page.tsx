"use client";

import { useQuery } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { Status } from "@/components/status";
import { platformService } from "@/services/platform-service";

export default function AdminMentorshipsPage() {
  const query = useQuery({
    queryKey: ["admin-mentorships"],
    queryFn: () => platformService.listMentorships("ADMIN"),
  });
  if (!query.data) return <LoadingState />;
  return (
    <div>
      <header className="page-heading">
        <p className="eyebrow">Supervisão</p>
        <h1 className="title">Mentorias da plataforma</h1>
        <p className="lead">Visualização administrativa para segurança e moderação.</p>
      </header>
      <div className="record-grid">
        {query.data.map((item) => (
          <article className="card record" key={item.id}>
            <div className="record-top">
              <span>{item.area}</span>
              <Status value={item.status} />
            </div>
            <h2>{item.subject}</h2>
            <p>
              {item.studentName} com {item.mentorName}
            </p>
            <small>{new Date(item.scheduledAt).toLocaleString("pt-BR")}</small>
            {item.summary && (
              <div className="objective">
                <strong>Resumo</strong>
                <span>{item.summary}</span>
              </div>
            )}
          </article>
        ))}
      </div>
    </div>
  );
}

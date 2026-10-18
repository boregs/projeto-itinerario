"use client";

import { CalendarPlus } from "lucide-react";
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { platformService } from "@/services/platform-service";

export default function MentorRequestsPage() {
  const client = useQueryClient();
  const [dates, setDates] = useState<Record<string, string>>({});
  const query = useQuery({
    queryKey: ["available-requests"],
    queryFn: () => platformService.listAvailableRequests(),
  });
  const accept = useMutation({
    mutationFn: ({ id, date }: { id: string; date: string }) =>
      platformService.acceptRequest(id, new Date(date).toISOString()),
    onSuccess: () => client.invalidateQueries(),
  });
  if (query.isLoading) return <LoadingState />;
  if (query.isError) return <ErrorState message={query.error.message} />;
  return (
    <div>
      <header className="page-heading">
        <p className="eyebrow">Matching simples</p>
        <h1 className="title">Solicitações disponíveis</h1>
        <p className="lead">Escolha pedidos alinhados às suas áreas e disponibilidade.</p>
      </header>
      <div className="record-grid">
        {query.data?.length ? (
          query.data.map((item) => (
            <article className="card record" key={item.id}>
              <div className="record-top">
                <span>{item.area}</span>
                <span className="status">{item.preferredSchedule}</span>
              </div>
              <h2>{item.subject}</h2>
              <p>{item.description}</p>
              <div className="objective">
                <strong>Objetivo</strong>
                <span>{item.objective}</span>
              </div>
              <label className="field compact">
                <span>Data e hora do encontro</span>
                <input
                  type="datetime-local"
                  value={dates[item.id] ?? ""}
                  onChange={(event) => setDates({ ...dates, [item.id]: event.target.value })}
                />
              </label>
              <button
                className="button"
                type="button"
                disabled={!dates[item.id] || accept.isPending}
                onClick={() => accept.mutate({ id: item.id, date: dates[item.id]! })}
              >
                <CalendarPlus size={17} aria-hidden="true" /> Aceitar e agendar
              </button>
              {accept.isError && <small role="alert">{accept.error.message}</small>}
            </article>
          ))
        ) : (
          <div className="card empty-state">
            <InboxIcon />
            <h2>Nenhuma solicitação disponível</h2>
            <p>Novos pedidos compatíveis aparecerão aqui.</p>
          </div>
        )}
      </div>
    </div>
  );
}
function InboxIcon() {
  return (
    <span className="empty-icon" aria-hidden="true">
      ✓
    </span>
  );
}

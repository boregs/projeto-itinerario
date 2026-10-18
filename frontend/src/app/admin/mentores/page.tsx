"use client";

import { Check, X } from "lucide-react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { Status } from "@/components/status";
import { platformService } from "@/services/platform-service";

export default function AdminMentorsPage() {
  const client = useQueryClient();
  const query = useQuery({ queryKey: ["mentors"], queryFn: () => platformService.listMentors() });
  const review = useMutation({
    mutationFn: ({ id, status }: { id: string; status: "APPROVED" | "REJECTED" }) =>
      platformService.reviewMentor(id, status),
    onSuccess: () => client.invalidateQueries(),
  });
  if (!query.data) return <LoadingState />;
  return (
    <div>
      <header className="page-heading">
        <p className="eyebrow">Revisão de mentores</p>
        <h1 className="title">Avalie experiência e disponibilidade.</h1>
      </header>
      <div className="record-grid">
        {query.data.map((item) => (
          <article className="card record" key={item.userId}>
            <div className="record-top">
              <strong>{item.user.name}</strong>
              <Status value={item.approvalStatus} />
            </div>
            <p>{item.bio}</p>
            <dl className="mini-data">
              <div>
                <dt>Áreas</dt>
                <dd>{item.expertiseAreas}</dd>
              </div>
              <div>
                <dt>Disponibilidade</dt>
                <dd>{item.availability}</dd>
              </div>
              <div>
                <dt>Experiência</dt>
                <dd>{item.experience}</dd>
              </div>
            </dl>
            {item.approvalStatus === "PENDING_APPROVAL" && (
              <div className="form-actions">
                <button
                  className="button"
                  type="button"
                  onClick={() => review.mutate({ id: item.userId, status: "APPROVED" })}
                >
                  <Check size={17} aria-hidden="true" /> Aprovar
                </button>
                <button
                  className="button secondary danger"
                  type="button"
                  onClick={() => review.mutate({ id: item.userId, status: "REJECTED" })}
                >
                  <X size={17} aria-hidden="true" /> Rejeitar
                </button>
              </div>
            )}
          </article>
        ))}
      </div>
    </div>
  );
}

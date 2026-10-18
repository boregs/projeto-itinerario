"use client";

import { Award, LockKeyhole, Sparkles } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { learningService } from "@/services/learning-service";

export default function AchievementsPage() {
  const query = useQuery({
    queryKey: ["achievements"],
    queryFn: () => learningService.getAchievements(),
  });
  if (query.isLoading) return <LoadingState />;
  if (query.isError || !query.data)
    return (
      <ErrorState
        message={query.error?.message ?? "Erro inesperado"}
        retry={() => query.refetch()}
      />
    );
  return (
    <div>
      <header className="page-heading">
        <p className="eyebrow">Marcos da jornada</p>
        <h1 className="title">Conquistas são lembranças do que você aprendeu.</h1>
        <p className="lead">Sem rankings. O importante é reconhecer o seu próprio avanço.</p>
      </header>
      <div className="achievement-grid">
        {query.data.map((item) => (
          <article
            className={item.earned ? "card achievement earned" : "card achievement locked"}
            key={item.id}
          >
            <div>
              {item.earned ? <Award aria-hidden="true" /> : <LockKeyhole aria-hidden="true" />}
            </div>
            <span>
              {item.earned ? (
                <>
                  <Sparkles size={14} aria-hidden="true" /> Conquistada
                </>
              ) : (
                "Ainda bloqueada"
              )}
            </span>
            <h2>{item.title}</h2>
            <p>{item.description}</p>
          </article>
        ))}
      </div>
    </div>
  );
}

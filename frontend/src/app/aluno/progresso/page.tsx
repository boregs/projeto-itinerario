"use client";

import { BookOpenCheck, Star, Target } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { learningService } from "@/services/learning-service";

export default function ProgressPage() {
  const query = useQuery({ queryKey: ["progress"], queryFn: () => learningService.getProgress() });
  if (query.isLoading) return <LoadingState />;
  if (query.isError || !query.data)
    return (
      <ErrorState
        message={query.error?.message ?? "Erro inesperado"}
        retry={() => query.refetch()}
      />
    );
  const data = query.data;
  return (
    <div>
      <header className="page-heading">
        <p className="eyebrow">Seu progresso</p>
        <h1 className="title">Cada resposta constrói um caminho.</h1>
      </header>
      <div className="metric-grid">
        <div className="card metric">
          <Star aria-hidden="true" />
          <strong>Nível {data.level}</strong>
          <span>{data.points} pontos acumulados</span>
        </div>
        <div className="card metric">
          <BookOpenCheck aria-hidden="true" />
          <strong>{data.completedActivities}</strong>
          <span>de {data.totalActivities} atividades</span>
        </div>
        <div className="card metric">
          <Target aria-hidden="true" />
          <strong>{Math.floor((data.completedActivities / data.totalActivities) * 100)}%</strong>
          <span>dos desafios concluídos</span>
        </div>
      </div>
      <section className="card progress-panel">
        <h2>Progresso por trilha</h2>
        {data.tracks.map((track) => (
          <div className="progress-line" key={track.id}>
            <div>
              <strong>{track.title}</strong>
              <span>
                {track.completedModules}/{track.totalModules} módulos
              </span>
            </div>
            <div className="progress">
              <span style={{ width: `${track.percentage}%` }} />
            </div>
            <strong>{track.percentage}%</strong>
          </div>
        ))}
      </section>
    </div>
  );
}

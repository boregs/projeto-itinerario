"use client";

import { ArrowLeft, ArrowRight, Check, Circle } from "lucide-react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { learningService } from "@/services/learning-service";

export default function TrackPage() {
  const { trackId } = useParams<{ trackId: string }>();
  const query = useQuery({
    queryKey: ["track", trackId],
    queryFn: () => learningService.getTrack(trackId),
  });
  if (query.isLoading) return <LoadingState />;
  if (query.isError || !query.data)
    return (
      <ErrorState
        message={query.error?.message ?? "Trilha não encontrada"}
        retry={() => query.refetch()}
      />
    );
  const { track, modules } = query.data;
  return (
    <div>
      <Link className="back-link" href="/aluno/trilhas">
        <ArrowLeft size={17} aria-hidden="true" /> Todas as trilhas
      </Link>
      <header className={`track-hero accent-${track.accent}`}>
        <div>
          <p className="eyebrow">{track.subject}</p>
          <h1 className="title">{track.title}</h1>
          <p className="lead">{track.description}</p>
        </div>
        <div className="track-total">
          <strong>{track.percentage}%</strong>
          <span>da trilha concluída</span>
        </div>
      </header>
      <section className="module-list" aria-labelledby="modules-title">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Roteiro de estudo</p>
            <h2 id="modules-title">Módulos</h2>
          </div>
          <span>
            {modules.length} {modules.length === 1 ? "módulo" : "módulos"}
          </span>
        </div>
        {modules.map((module, index) => {
          const completed = index < track.completedModules;
          return (
            <article className="card module-row" key={module.id}>
              <div className={completed ? "module-index done" : "module-index"}>
                {completed ? <Check aria-label="Concluído" /> : <Circle aria-hidden="true" />}
              </div>
              <div>
                <span>{module.eyebrow}</span>
                <h3>{module.title}</h3>
                <p>{module.content}</p>
              </div>
              <Link
                href={`/aluno/trilhas/${track.id}/modulos/${module.id}`}
                aria-label={`Abrir ${module.title}`}
              >
                <ArrowRight aria-hidden="true" />
              </Link>
            </article>
          );
        })}
      </section>
    </div>
  );
}

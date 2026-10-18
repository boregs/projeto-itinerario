"use client";

import { ArrowLeft, ArrowRight, CheckCircle2, Circle } from "lucide-react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { learningService } from "@/services/learning-service";

export default function ModulePage() {
  const { trackId, moduleId } = useParams<{ trackId: string; moduleId: string }>();
  const query = useQuery({
    queryKey: ["module", moduleId],
    queryFn: () => learningService.getModule(moduleId),
  });
  if (query.isLoading) return <LoadingState />;
  if (query.isError || !query.data)
    return (
      <ErrorState
        message={query.error?.message ?? "Módulo não encontrado"}
        retry={() => query.refetch()}
      />
    );
  const { module, activities, completedIds } = query.data;
  return (
    <div className="reading-layout">
      <aside>
        <Link className="back-link" href={`/aluno/trilhas/${trackId}`}>
          <ArrowLeft size={17} aria-hidden="true" /> Voltar à trilha
        </Link>
        <p className="eyebrow">{module.eyebrow}</p>
        <h1 className="title">{module.title}</h1>
        <p className="lead">{module.content}</p>
      </aside>
      <section className="activity-list" aria-labelledby="activities-title">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Hora de praticar</p>
            <h2 id="activities-title">Atividades</h2>
          </div>
          <span>
            {completedIds.filter((id) => module.activityIds.includes(id)).length}/
            {activities.length}
          </span>
        </div>
        {activities.map((activity, index) => {
          const done = completedIds.includes(activity.id);
          return (
            <Link
              className="card activity-row"
              href={`/aluno/atividades/${activity.id}`}
              key={activity.id}
            >
              <span className={done ? "activity-check done" : "activity-check"}>
                {done ? <CheckCircle2 aria-label="Concluída" /> : <Circle aria-hidden="true" />}
              </span>
              <span>
                <small>
                  Atividade {index + 1} · {activity.points} pontos
                </small>
                <strong>{activity.title}</strong>
              </span>
              <ArrowRight aria-hidden="true" />
            </Link>
          );
        })}
      </section>
    </div>
  );
}

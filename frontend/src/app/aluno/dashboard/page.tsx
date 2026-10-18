"use client";

import { ArrowRight, Award, BookOpenCheck, Flame, Star } from "lucide-react";
import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { TrackCard } from "@/components/track-card";
import { learningService } from "@/services/learning-service";

export default function DashboardPage() {
  const progress = useQuery({
    queryKey: ["progress"],
    queryFn: () => learningService.getProgress(),
  });
  if (progress.isLoading) return <LoadingState label="Preparando seu painel" />;
  if (progress.isError || !progress.data)
    return (
      <ErrorState
        message={progress.error?.message ?? "Erro inesperado"}
        retry={() => progress.refetch()}
      />
    );
  const logic = progress.data.tracks[0];
  return (
    <div className="dashboard">
      <section className="welcome">
        <div>
          <p className="eyebrow">Domingo, 18 de outubro</p>
          <h1 className="title">Olá, Luana. O que vamos descobrir hoje?</h1>
        </div>
        <div className="level-badge">
          <Star aria-hidden="true" />
          <span>Nível {progress.data.level}</span>
          <strong>{progress.data.points} pts</strong>
        </div>
      </section>
      <section className="continue-card">
        <div>
          <p className="eyebrow">Continue de onde parou</p>
          <h2>Pensar em passos</h2>
          <p>Você começou a trilha de Lógica. A próxima atividade leva poucos minutos.</p>
          <div className="progress">
            <span style={{ width: `${logic.percentage}%` }} />
          </div>
        </div>
        <Link className="continue-action" href="/aluno/trilhas/logic/modulos/logic-start">
          <span>Continuar aprendendo</span>
          <ArrowRight aria-hidden="true" />
        </Link>
      </section>
      <section aria-labelledby="resumo-title">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Seu ritmo</p>
            <h2 id="resumo-title">Progresso em foco</h2>
          </div>
          <Link href="/aluno/progresso">
            Ver detalhes <ArrowRight size={16} aria-hidden="true" />
          </Link>
        </div>
        <div className="metric-grid">
          <div className="card metric">
            <BookOpenCheck aria-hidden="true" />
            <strong>{progress.data.completedActivities}</strong>
            <span>atividades concluídas</span>
          </div>
          <div className="card metric">
            <Flame aria-hidden="true" />
            <strong>{progress.data.points}</strong>
            <span>pontos conquistados</span>
          </div>
          <div className="card metric">
            <Award aria-hidden="true" />
            <strong>1</strong>
            <span>conquista desbloqueada</span>
          </div>
        </div>
      </section>
      <section aria-labelledby="trilhas-title">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Explore</p>
            <h2 id="trilhas-title">Uma trilha para cada curiosidade</h2>
          </div>
          <Link href="/aluno/trilhas">
            Ver todas <ArrowRight size={16} aria-hidden="true" />
          </Link>
        </div>
        <div className="track-grid compact">
          {progress.data.tracks.slice(1, 4).map((track) => (
            <TrackCard key={track.id} track={track} />
          ))}
        </div>
      </section>
    </div>
  );
}

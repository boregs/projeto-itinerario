"use client";

import { ArrowLeft, CheckCircle2, RotateCcw, Sparkles, XCircle } from "lucide-react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { learningService } from "@/services/learning-service";
import type { AttemptFeedback } from "@/types/learning";

export default function ActivityPage() {
  const { activityId } = useParams<{ activityId: string }>();
  const client = useQueryClient();
  const [answer, setAnswer] = useState<number | null>(null);
  const [feedback, setFeedback] = useState<AttemptFeedback | null>(null);
  const query = useQuery({
    queryKey: ["activity", activityId],
    queryFn: () => learningService.getActivity(activityId),
  });
  const attempt = useMutation({
    mutationFn: (selected: number) => learningService.submitAttempt(activityId, selected),
    onSuccess: async (result) => {
      setFeedback(result);
      await client.invalidateQueries();
    },
  });
  if (query.isLoading) return <LoadingState label="Preparando atividade" />;
  if (query.isError || !query.data)
    return (
      <ErrorState
        message={query.error?.message ?? "Atividade não encontrada"}
        retry={() => query.refetch()}
      />
    );
  const { activity, completed, trackId } = query.data;
  function retry() {
    setFeedback(null);
    setAnswer(null);
    attempt.reset();
  }
  return (
    <div className="activity-page">
      <div className="activity-toolbar">
        <Link className="back-link" href={`/aluno/trilhas/${trackId}`}>
          <ArrowLeft size={17} aria-hidden="true" /> Voltar à trilha
        </Link>
        <span className="status-pill">
          <Sparkles size={15} aria-hidden="true" /> {activity.points} pontos
        </span>
      </div>
      <section className="activity-card card">
        <header>
          <p className="eyebrow">Desafio rápido</p>
          <h1>{activity.title}</h1>
          {completed && (
            <span className="completed-note">
              <CheckCircle2 size={17} aria-hidden="true" /> Você já concluiu esta atividade
            </span>
          )}
        </header>
        <form
          onSubmit={(event) => {
            event.preventDefault();
            if (answer !== null) attempt.mutate(answer);
          }}
        >
          <fieldset disabled={attempt.isPending || feedback !== null}>
            <legend>{activity.question}</legend>
            <div className="options">
              {activity.options.map((option, index) => (
                <label key={option} className={answer === index ? "option selected" : "option"}>
                  <input
                    type="radio"
                    name="answer"
                    value={index}
                    checked={answer === index}
                    onChange={() => setAnswer(index)}
                  />
                  <span className="option-letter" aria-hidden="true">
                    {String.fromCharCode(65 + index)}
                  </span>
                  <span>{option}</span>
                </label>
              ))}
            </div>
          </fieldset>
          {attempt.isError && (
            <div className="error-box" role="alert">
              {attempt.error.message}
            </div>
          )}
          {!feedback && (
            <button
              className="button submit-answer"
              type="submit"
              disabled={answer === null || attempt.isPending}
            >
              {attempt.isPending ? "Conferindo…" : "Conferir resposta"}
            </button>
          )}
        </form>
        {feedback && (
          <div
            className={feedback.correct ? "feedback correct" : "feedback incorrect"}
            role="status"
            aria-live="polite"
          >
            <div className="feedback-title">
              {feedback.correct ? (
                <CheckCircle2 aria-hidden="true" />
              ) : (
                <XCircle aria-hidden="true" />
              )}
              <div>
                <strong>{feedback.correct ? "Mandou bem!" : "Ainda não. Vamos entender?"}</strong>
                <span>
                  {feedback.correct
                    ? feedback.awardedPoints
                      ? `+${feedback.awardedPoints} pontos`
                      : "Atividade já pontuada"
                    : "Errar também faz parte"}
                </span>
              </div>
            </div>
            <p>{feedback.explanation}</p>
            {feedback.newAchievements.length > 0 && (
              <p className="achievement-toast">
                <Sparkles aria-hidden="true" /> Nova conquista desbloqueada!
              </p>
            )}
            <div className="feedback-actions">
              {!feedback.correct && (
                <button className="button" type="button" onClick={retry}>
                  <RotateCcw size={17} aria-hidden="true" /> Tentar novamente
                </button>
              )}
              <Link className="button secondary" href="/aluno/trilhas">
                Explorar trilhas
              </Link>
            </div>
          </div>
        )}
      </section>
    </div>
  );
}

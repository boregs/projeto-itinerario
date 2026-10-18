"use client";

import { useQuery } from "@tanstack/react-query";
import { ErrorState, LoadingState } from "@/components/query-state";
import { TrackCard } from "@/components/track-card";
import { learningService } from "@/services/learning-service";

export default function TracksPage() {
  const query = useQuery({ queryKey: ["tracks"], queryFn: () => learningService.listTracks() });
  return (
    <div>
      <header className="page-heading">
        <p className="eyebrow">Biblioteca de trilhas</p>
        <h1 className="title">Escolha sua próxima descoberta.</h1>
        <p className="lead">Atividades curtas, feedback imediato e progresso no seu ritmo.</p>
      </header>
      {query.isLoading ? (
        <LoadingState />
      ) : query.isError ? (
        <ErrorState message={query.error.message} retry={() => query.refetch()} />
      ) : (
        <div className="track-grid">
          {query.data?.map((track) => (
            <TrackCard key={track.id} track={track} />
          ))}
        </div>
      )}
    </div>
  );
}

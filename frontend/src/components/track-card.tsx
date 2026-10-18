import { ArrowUpRight } from "lucide-react";
import Link from "next/link";
import type { TrackSummary } from "@/types/learning";

export function TrackCard({ track }: { track: TrackSummary }) {
  return (
    <article className={`card track-card accent-${track.accent}`}>
      <div className="track-top">
        <span className="subject-dot" aria-hidden="true" />
        <span>{track.subject}</span>
        <ArrowUpRight size={20} aria-hidden="true" />
      </div>
      <div>
        <h2>{track.title}</h2>
        <p>{track.description}</p>
      </div>
      <div>
        <div className="track-progress-copy">
          <span>
            {track.completedModules} de {track.totalModules} módulos
          </span>
          <strong>{track.percentage}%</strong>
        </div>
        <div className="progress" aria-label={`${track.percentage}% concluído`}>
          <span style={{ width: `${track.percentage}%` }} />
        </div>
      </div>
      <Link href={`/aluno/trilhas/${track.id}`} aria-label={`Abrir trilha ${track.title}`}>
        Abrir trilha
      </Link>
    </article>
  );
}

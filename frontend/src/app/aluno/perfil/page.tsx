"use client";

import { useQuery } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { platformService } from "@/services/platform-service";

export default function StudentProfilePage() {
  const query = useQuery({
    queryKey: ["student-profile"],
    queryFn: () => platformService.getStudentProfile(),
  });
  if (!query.data) return <LoadingState />;
  return (
    <div className="form-page">
      <header className="page-heading">
        <p className="eyebrow">Seu perfil</p>
        <h1 className="title">Informações de aprendizagem</h1>
      </header>
      <section className="card form-card">
        <div className="profile-avatar">LS</div>
        <dl className="profile-data">
          <div>
            <dt>Nome de exibição</dt>
            <dd>{query.data.name}</dd>
          </div>
          <div>
            <dt>Série escolar</dt>
            <dd>{query.data.schoolYear}</dd>
          </div>
          <div>
            <dt>Interesses</dt>
            <dd>{query.data.interests}</dd>
          </div>
        </dl>
        <p className="muted">
          A edição completa do perfil será conectada ao backend em uma etapa futura.
        </p>
      </section>
    </div>
  );
}

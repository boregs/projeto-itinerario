"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { Status } from "@/components/status";
import { platformService } from "@/services/platform-service";
import type { ReportStatus } from "@/types/platform";

export default function AdminReportsPage() {
  const client = useQueryClient();
  const query = useQuery({ queryKey: ["reports"], queryFn: () => platformService.listReports() });
  const update = useMutation({
    mutationFn: ({ id, status }: { id: string; status: ReportStatus }) =>
      platformService.updateReport(id, status),
    onSuccess: () => client.invalidateQueries(),
  });
  if (!query.data) return <LoadingState />;
  return (
    <div>
      <header className="page-heading">
        <p className="eyebrow">Moderação</p>
        <h1 className="title">Denúncias</h1>
      </header>
      <div className="record-grid">
        {query.data.map((report) => (
          <article className="card record" key={report.id}>
            <div className="record-top">
              <span>{report.reason}</span>
              <Status value={report.status} />
            </div>
            <h2>{report.target}</h2>
            <p>{report.description}</p>
            <small>Enviada por {report.reporterName}</small>
            <label className="field compact">
              <span>Alterar estado</span>
              <select
                value={report.status}
                onChange={(event) =>
                  update.mutate({ id: report.id, status: event.target.value as ReportStatus })
                }
              >
                <option value="OPEN">Aberta</option>
                <option value="UNDER_REVIEW">Em análise</option>
                <option value="RESOLVED">Resolvida</option>
                <option value="DISMISSED">Descartada</option>
              </select>
            </label>
          </article>
        ))}
      </div>
    </div>
  );
}

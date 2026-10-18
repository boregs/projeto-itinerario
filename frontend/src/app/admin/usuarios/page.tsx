"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { Status } from "@/components/status";
import { platformService } from "@/services/platform-service";

export default function AdminUsersPage() {
  const client = useQueryClient();
  const query = useQuery({ queryKey: ["users"], queryFn: () => platformService.listUsers() });
  const update = useMutation({
    mutationFn: ({ id, status }: { id: string; status: "ACTIVE" | "BLOCKED" }) =>
      platformService.updateUserStatus(id, status),
    onSuccess: () => client.invalidateQueries(),
  });
  if (!query.data) return <LoadingState />;
  return (
    <div>
      <header className="page-heading">
        <p className="eyebrow">Contas da plataforma</p>
        <h1 className="title">Usuários</h1>
      </header>
      <div className="table-wrap card">
        <table>
          <thead>
            <tr>
              <th>Usuário</th>
              <th>Papel</th>
              <th>Estado</th>
              <th>
                <span className="sr-only">Ações</span>
              </th>
            </tr>
          </thead>
          <tbody>
            {query.data.map((user) => (
              <tr key={user.id}>
                <td>
                  <strong>{user.name}</strong>
                  <small>{user.email}</small>
                </td>
                <td>{user.role}</td>
                <td>
                  <Status value={user.status} />
                </td>
                <td>
                  {user.role !== "ADMIN" && (
                    <button
                      className="button secondary small"
                      type="button"
                      onClick={() =>
                        update.mutate({
                          id: user.id,
                          status: user.status === "ACTIVE" ? "BLOCKED" : "ACTIVE",
                        })
                      }
                    >
                      {user.status === "ACTIVE" ? "Bloquear" : "Ativar"}
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

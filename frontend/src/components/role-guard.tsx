"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { platformService } from "@/services/platform-service";
import type { Role } from "@/types/platform";

export function RoleGuard({ role, children }: { role: Role; children: React.ReactNode }) {
  const router = useRouter();
  const query = useQuery({
    queryKey: ["session"],
    queryFn: () => platformService.getSession(),
    staleTime: Infinity,
  });
  useEffect(() => {
    if (!query.isLoading && query.data?.role !== role) router.replace("/login");
  }, [query.data, query.isLoading, role, router]);
  if (query.isLoading || query.data?.role !== role)
    return (
      <main id="conteudo" className="student-main page-shell">
        <LoadingState label="Verificando acesso" />
      </main>
    );
  return children;
}

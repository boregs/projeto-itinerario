"use client";

import { DemoBanner } from "@/components/demo-banner";
import { RoleNav } from "@/components/role-nav";
import { RoleGuard } from "@/components/role-guard";
import { HandHeart, House, Inbox, UserRound } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { platformService } from "@/services/platform-service";

export default function MentorLayout({ children }: { children: React.ReactNode }) {
  const session = useQuery({ queryKey: ["session"], queryFn: () => platformService.getSession() });
  const profile = useQuery({
    queryKey: ["mentor-profile", session.data?.id],
    queryFn: () => platformService.getMentorProfile(),
    enabled: session.data?.role === "MENTOR",
  });
  return (
    <RoleGuard role="MENTOR">
      {!session.data || !profile.data ? (
        <main id="conteudo" className="student-main page-shell">
          <LoadingState label="Carregando perfil do mentor" />
        </main>
      ) : (
        <>
          <DemoBanner />
          <RoleNav
            name={session.data.name.split(" ")[0] ?? session.data.name}
            subtitle={
              profile.data.approvalStatus === "APPROVED"
                ? "Mentor aprovado"
                : "Aguardando aprovação"
            }
            initials={session.data.initials}
            items={[
              { href: "/mentor/dashboard", label: "Início", icon: House },
              { href: "/mentor/solicitacoes", label: "Solicitações", icon: Inbox },
              { href: "/mentor/mentorias", label: "Mentorias", icon: HandHeart },
              { href: "/mentor/perfil", label: "Perfil", icon: UserRound },
            ]}
          />
          <main id="conteudo" className="student-main page-shell">
            {children}
          </main>
        </>
      )}
    </RoleGuard>
  );
}

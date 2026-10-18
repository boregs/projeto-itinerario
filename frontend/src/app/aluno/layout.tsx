"use client";

import { DemoBanner } from "@/components/demo-banner";
import { RoleNav } from "@/components/role-nav";
import { RoleGuard } from "@/components/role-guard";
import { Award, BookOpen, HandHeart, House, UserRound } from "lucide-react";

export default function StudentLayout({ children }: { children: React.ReactNode }) {
  return (
    <RoleGuard role="STUDENT">
      {
        <>
          <DemoBanner />
          <RoleNav
            name="Luana"
            subtitle="2º ano"
            initials="LS"
            items={[
              { href: "/aluno/dashboard", label: "Início", icon: House },
              { href: "/aluno/trilhas", label: "Trilhas", icon: BookOpen },
              { href: "/aluno/mentorias", label: "Mentorias", icon: HandHeart },
              { href: "/aluno/conquistas", label: "Conquistas", icon: Award },
              { href: "/aluno/perfil", label: "Perfil", icon: UserRound },
            ]}
          />
          <main id="conteudo" className="student-main page-shell">
            {children}
          </main>
        </>
      }
    </RoleGuard>
  );
}

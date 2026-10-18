"use client";

import { DemoBanner } from "@/components/demo-banner";
import { RoleNav } from "@/components/role-nav";
import { RoleGuard } from "@/components/role-guard";
import { Flag, HandHeart, House, ShieldCheck, Users } from "lucide-react";

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  return (
    <RoleGuard role="ADMIN">
      {
        <>
          <DemoBanner />
          <RoleNav
            name="Marina"
            subtitle="Administradora"
            initials="MC"
            items={[
              { href: "/admin/dashboard", label: "Início", icon: House },
              { href: "/admin/mentores", label: "Mentores", icon: ShieldCheck },
              { href: "/admin/usuarios", label: "Usuários", icon: Users },
              { href: "/admin/mentorias", label: "Mentorias", icon: HandHeart },
              { href: "/admin/denuncias", label: "Denúncias", icon: Flag },
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

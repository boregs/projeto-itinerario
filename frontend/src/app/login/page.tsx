"use client";

import { ArrowRight, ShieldCheck } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { identities } from "@/mocks/platform-fixtures";
import { platformService } from "@/services/platform-service";

const destinations = {
  STUDENT: "/aluno/dashboard",
  MENTOR: "/mentor/dashboard",
  ADMIN: "/admin/dashboard",
};

export default function LoginPage() {
  const router = useRouter();
  const client = useQueryClient();
  const [loading, setLoading] = useState<string | null>(null);
  async function enter(id: string, role: keyof typeof destinations) {
    setLoading(id);
    await platformService.login(id);
    client.clear();
    router.push(destinations[role]);
  }
  return (
    <main id="conteudo" className="login-page">
      <section className="login-card wide">
        <Link className="brand" href="/">
          <span>i</span> itinerário
        </Link>
        <div>
          <p className="eyebrow">Acesso demonstrativo</p>
          <h1 className="title">Escolha uma experiência</h1>
          <p className="lead">
            Os perfis abaixo usam dados fictícios e compartilham o mesmo cenário de demonstração.
          </p>
        </div>
        <div className="identity-grid">
          {identities.map((identity) => (
            <button
              className="demo-person identity-button"
              type="button"
              key={identity.id}
              disabled={loading !== null}
              onClick={() => enter(identity.id, identity.role)}
            >
              <span className="avatar">{identity.initials}</span>
              <span>
                <strong>{identity.name}</strong>
                <small>{identity.subtitle}</small>
              </span>
              <ArrowRight aria-hidden="true" />
            </button>
          ))}
        </div>
        <p className="privacy-note">
          <ShieldCheck size={18} aria-hidden="true" /> Nenhuma senha é solicitada. Este acesso não
          representa autenticação de produção.
        </p>
      </section>
    </main>
  );
}

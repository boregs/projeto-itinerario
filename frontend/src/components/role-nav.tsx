"use client";

import { LogOut, Menu, X, type LucideIcon } from "lucide-react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { platformService } from "@/services/platform-service";

export type NavItem = {
  href: string;
  label: string;
  icon: LucideIcon;
};

export function RoleNav({
  items,
  name,
  subtitle,
  initials,
}: {
  items: NavItem[];
  name: string;
  subtitle: string;
  initials: string;
}) {
  const pathname = usePathname();
  const router = useRouter();
  const client = useQueryClient();
  const [open, setOpen] = useState(false);
  async function logout() {
    await platformService.logout();
    client.clear();
    router.push("/login");
  }
  return (
    <header className="student-header">
      <div className="page-shell nav-row">
        <Link className="brand" href={items[0]?.href ?? "/"}>
          <span>i</span> itinerário
        </Link>
        <button
          className="menu-button"
          type="button"
          aria-expanded={open}
          aria-controls="role-menu"
          onClick={() => setOpen(!open)}
        >
          {open ? <X aria-hidden="true" /> : <Menu aria-hidden="true" />}
          <span className="sr-only">{open ? "Fechar menu" : "Abrir menu"}</span>
        </button>
        <nav
          id="role-menu"
          aria-label="Navegação principal"
          className={open ? "student-menu open" : "student-menu"}
        >
          {items.map(({ href, label, icon: Icon }) => (
            <Link
              key={href}
              href={href}
              onClick={() => setOpen(false)}
              aria-current={pathname.startsWith(href) ? "page" : undefined}
            >
              <Icon size={18} aria-hidden="true" />
              {label}
            </Link>
          ))}
        </nav>
        <div className="student-chip">
          <span>{initials}</span>
          <div>
            <strong>{name}</strong>
            <small>{subtitle}</small>
          </div>
          <button
            className="logout-button"
            type="button"
            onClick={logout}
            aria-label="Sair da demonstração"
          >
            <LogOut size={17} aria-hidden="true" />
          </button>
        </div>
      </div>
    </header>
  );
}

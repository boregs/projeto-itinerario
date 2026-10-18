"use client";

import { RotateCcw } from "lucide-react";
import { useQueryClient } from "@tanstack/react-query";
import { learningService } from "@/services/learning-service";
import { platformService } from "@/services/platform-service";

export function DemoBanner() {
  const queryClient = useQueryClient();
  async function reset() {
    if (!window.confirm("Restaurar todo o progresso da demonstração?")) return;
    await learningService.reset();
    await platformService.reset();
    await queryClient.invalidateQueries();
  }
  return (
    <div className="demo-banner">
      <div className="page-shell demo-inner">
        <span>
          <strong>Modo demonstração</strong> · dados fictícios salvos neste navegador
        </span>
        <button type="button" onClick={reset}>
          <RotateCcw size={15} aria-hidden="true" /> Restaurar dados
        </button>
      </div>
    </div>
  );
}

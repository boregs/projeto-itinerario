"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { ArrowLeft } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { useQueryClient } from "@tanstack/react-query";
import { platformService } from "@/services/platform-service";

const schema = z.object({
  subject: z.string().min(4, "Informe um tema mais específico."),
  area: z.string().min(1, "Escolha uma área."),
  description: z.string().min(15, "Conte um pouco mais sobre a dificuldade."),
  objective: z.string().min(8, "Informe o que deseja alcançar."),
  preferredSchedule: z.string().min(1, "Escolha um período."),
});
type FormData = z.infer<typeof schema>;

export default function NewRequestPage() {
  const router = useRouter();
  const client = useQueryClient();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({ resolver: zodResolver(schema) });
  async function submit(data: FormData) {
    await platformService.createRequest(data);
    await client.invalidateQueries();
    router.push("/aluno/mentorias");
  }
  return (
    <div className="form-page">
      <Link className="back-link" href="/aluno/mentorias">
        <ArrowLeft size={17} aria-hidden="true" /> Voltar às mentorias
      </Link>
      <header>
        <p className="eyebrow">Nova solicitação</p>
        <h1 className="title">Em que podemos ajudar?</h1>
        <p className="lead">Não inclua telefone, redes sociais ou outros dados de contato.</p>
      </header>
      <form className="card form-card" onSubmit={handleSubmit(submit)} noValidate>
        <Field label="Tema" error={errors.subject?.message}>
          <input {...register("subject")} placeholder="Ex.: Entender equações" />
        </Field>
        <Field label="Área" error={errors.area?.message}>
          <select {...register("area")} defaultValue="">
            <option value="" disabled>
              Selecione
            </option>
            <option>Matemática</option>
            <option>Lógica</option>
            <option>História</option>
            <option>Química</option>
            <option>Biologia</option>
          </select>
        </Field>
        <Field label="Descreva sua dificuldade" error={errors.description?.message}>
          <textarea {...register("description")} rows={4} />
        </Field>
        <Field label="O que você quer conseguir fazer?" error={errors.objective?.message}>
          <textarea {...register("objective")} rows={3} />
        </Field>
        <Field label="Melhor período" error={errors.preferredSchedule?.message}>
          <select {...register("preferredSchedule")} defaultValue="">
            <option value="" disabled>
              Selecione
            </option>
            <option>Manhã</option>
            <option>Tarde</option>
            <option>Noite</option>
          </select>
        </Field>
        <button className="button" type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Enviando…" : "Enviar solicitação"}
        </button>
      </form>
    </div>
  );
}

function Field({
  label,
  error,
  children,
}: {
  label: string;
  error?: string;
  children: React.ReactNode;
}) {
  return (
    <label className="field">
      <span>{label}</span>
      {children}
      {error && <small role="alert">{error}</small>}
    </label>
  );
}

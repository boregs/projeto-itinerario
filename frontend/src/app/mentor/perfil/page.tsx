"use client";

import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { Status } from "@/components/status";
import { platformService } from "@/services/platform-service";

export default function MentorProfilePage() {
  const client = useQueryClient();
  const query = useQuery({
    queryKey: ["mentor-profile"],
    queryFn: () => platformService.getMentorProfile(),
  });
  const [editing, setEditing] = useState(false);
  const mutation = useMutation({
    mutationFn: platformService.updateMentorProfile.bind(platformService),
    onSuccess: async () => {
      setEditing(false);
      await client.invalidateQueries();
    },
  });
  if (!query.data) return <LoadingState />;
  const profile = query.data;
  if (editing)
    return (
      <EditProfile
        profile={profile}
        saving={mutation.isPending}
        onCancel={() => setEditing(false)}
        onSave={(data) => mutation.mutate(data)}
      />
    );
  return (
    <div className="form-page">
      <header className="page-heading">
        <p className="eyebrow">Perfil profissional</p>
        <h1 className="title">Como estudantes conhecem seu trabalho</h1>
      </header>
      <section className="card form-card">
        <div className="record-top">
          <strong>{profile.name}</strong>
          <Status value={profile.approvalStatus} />
        </div>
        <dl className="profile-data">
          <div>
            <dt>Apresentação</dt>
            <dd>{profile.bio}</dd>
          </div>
          <div>
            <dt>Áreas</dt>
            <dd>{profile.expertiseAreas}</dd>
          </div>
          <div>
            <dt>Disponibilidade</dt>
            <dd>{profile.availability}</dd>
          </div>
          <div>
            <dt>Experiência</dt>
            <dd>{profile.experience}</dd>
          </div>
        </dl>
        <button className="button" type="button" onClick={() => setEditing(true)}>
          Editar perfil
        </button>
      </section>
    </div>
  );
}
function EditProfile({
  profile,
  saving,
  onCancel,
  onSave,
}: {
  profile: { bio: string; expertiseAreas: string; availability: string; experience: string };
  saving: boolean;
  onCancel: () => void;
  onSave: (value: typeof profile) => void;
}) {
  const [value, setValue] = useState(profile);
  return (
    <div className="form-page">
      <header>
        <p className="eyebrow">Editar perfil</p>
        <h1 className="title">Uma nova revisão será necessária</h1>
        <p className="lead">Ao salvar, o perfil voltará ao estado de aprovação pendente.</p>
      </header>
      <form
        className="card form-card"
        onSubmit={(event) => {
          event.preventDefault();
          onSave(value);
        }}
      >
        {(["bio", "expertiseAreas", "availability", "experience"] as const).map((key) => (
          <label className="field" key={key}>
            <span>
              {
                {
                  bio: "Apresentação",
                  expertiseAreas: "Áreas",
                  availability: "Disponibilidade",
                  experience: "Experiência",
                }[key]
              }
            </span>
            <textarea
              rows={key === "bio" ? 4 : 2}
              value={value[key]}
              onChange={(event) => setValue({ ...value, [key]: event.target.value })}
            />
          </label>
        ))}
        <div className="form-actions">
          <button className="button" disabled={saving}>
            Salvar e enviar para revisão
          </button>
          <button className="button secondary" type="button" onClick={onCancel}>
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
}

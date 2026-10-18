"use client";

import { Calendar, ArrowRight } from "lucide-react";
import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { LoadingState } from "@/components/query-state";
import { Status } from "@/components/status";
import { platformService } from "@/services/platform-service";

export default function MentorMentorshipsPage() {
  const query = useQuery({
    queryKey: ["mentor-mentorships"],
    queryFn: () => platformService.listMentorships("MENTOR"),
  });
  if (!query.data) return <LoadingState />;
  return (
    <div>
      <header className="page-heading">
        <p className="eyebrow">Acompanhamento</p>
        <h1 className="title">Suas mentorias</h1>
      </header>
      <div className="record-grid">
        {query.data.map((item) => (
          <Link
            className="card record record-link"
            href={`/mentor/mentorias/${item.id}`}
            key={item.id}
          >
            <div className="record-top">
              <span>{item.area}</span>
              <Status value={item.status} />
            </div>
            <h2>{item.subject}</h2>
            <p>Estudante: {item.studentName}</p>
            <small>
              <Calendar size={14} aria-hidden="true" />{" "}
              {new Date(item.scheduledAt).toLocaleString("pt-BR", {
                dateStyle: "medium",
                timeStyle: "short",
              })}
            </small>
            <ArrowRight className="record-arrow" aria-hidden="true" />
          </Link>
        ))}
      </div>
    </div>
  );
}

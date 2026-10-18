import { Suspense } from "react";
import { LoadingState } from "@/components/query-state";
import MentorMentorshipDetail from "./details-content";

export default function MentorMentorshipDetailsPage() {
  return (
    <Suspense fallback={<LoadingState label="Carregando mentoria" />}>
      <MentorMentorshipDetail />
    </Suspense>
  );
}

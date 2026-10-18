import { Suspense } from "react";
import { LoadingState } from "@/components/query-state";
import StudentMentorshipPage from "./details-content";

export default function StudentMentorshipDetailsPage() {
  return (
    <Suspense fallback={<LoadingState label="Carregando mentoria" />}>
      <StudentMentorshipPage />
    </Suspense>
  );
}

import { modules } from "@/mocks/learning-fixtures";
import ModulePage from "./module-page-client";

export function generateStaticParams() {
  return modules.map((module) => ({ trackId: module.trackId, moduleId: module.id }));
}

export default function ModuleRoutePage() {
  return <ModulePage />;
}

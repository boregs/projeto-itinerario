import { activities } from "@/mocks/learning-fixtures";
import ActivityPage from "./activity-page-client";

export function generateStaticParams() {
  return activities.map((activity) => ({ activityId: activity.id }));
}

export default function ActivityRoutePage() {
  return <ActivityPage />;
}

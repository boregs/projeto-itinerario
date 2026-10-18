import { tracks } from "@/mocks/learning-fixtures";
import TrackPage from "./track-page-client";

export function generateStaticParams() {
  return tracks.map((track) => ({ trackId: track.id }));
}

export default function TrackRoutePage() {
  return <TrackPage />;
}

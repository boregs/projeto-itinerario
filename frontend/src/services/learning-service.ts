import { activities, modules, tracks } from "@/mocks/learning-fixtures";
import type {
  Activity,
  AttemptFeedback,
  DemoState,
  LearningModule,
  Progress,
  TrackSummary,
} from "@/types/learning";

const STORAGE_KEY = "itinerario-demo-learning-v1";
const INITIAL_STATE: DemoState = {
  version: 1,
  attempts: [
    {
      activityId: "logic-algorithm",
      answer: 1,
      correct: true,
      createdAt: "2026-10-18T09:00:00.000Z",
    },
  ],
  completedActivityIds: ["logic-algorithm"],
  achievementIds: ["FIRST_ACTIVITY"],
};

export class MockLearningService {
  constructor(private readonly latency = 180) {}

  private async wait() {
    await new Promise((resolve) => setTimeout(resolve, this.latency));
  }

  private read(): DemoState {
    if (typeof window === "undefined") return structuredClone(INITIAL_STATE);
    try {
      const parsed = JSON.parse(
        localStorage.getItem(STORAGE_KEY) ?? "null",
      ) as Partial<DemoState> | null;
      if (
        parsed?.version !== 1 ||
        !Array.isArray(parsed.attempts) ||
        !Array.isArray(parsed.completedActivityIds) ||
        !Array.isArray(parsed.achievementIds)
      )
        throw new Error("invalid demo data");
      return parsed as DemoState;
    } catch {
      this.write(INITIAL_STATE);
      return structuredClone(INITIAL_STATE);
    }
  }

  private write(state: DemoState) {
    if (typeof window !== "undefined") localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  }

  async listTracks(): Promise<TrackSummary[]> {
    await this.wait();
    return this.summarize(this.read());
  }

  async getTrack(trackId: string) {
    await this.wait();
    const track = this.summarize(this.read()).find((item) => item.id === trackId);
    if (!track) throw new Error("Trilha não encontrada.");
    return { track, modules: modules.filter((item) => item.trackId === trackId) };
  }

  async getModule(
    moduleId: string,
  ): Promise<{ module: LearningModule; activities: Activity[]; completedIds: string[] }> {
    await this.wait();
    const learningModule = modules.find((item) => item.id === moduleId);
    if (!learningModule) throw new Error("Módulo não encontrado.");
    const state = this.read();
    return {
      module: learningModule,
      activities: activities.filter((item) => item.moduleId === moduleId).map(this.publicActivity),
      completedIds: state.completedActivityIds,
    };
  }

  async getActivity(
    activityId: string,
  ): Promise<{ activity: Activity; completed: boolean; trackId: string }> {
    await this.wait();
    const fixture = activities.find((item) => item.id === activityId);
    if (!fixture) throw new Error("Atividade não encontrada.");
    const trackId = modules.find((item) => item.id === fixture.moduleId)?.trackId;
    if (!trackId) throw new Error("Trilha não encontrada.");
    return {
      activity: this.publicActivity(fixture),
      completed: this.read().completedActivityIds.includes(activityId),
      trackId,
    };
  }

  async submitAttempt(activityId: string, answer: number): Promise<AttemptFeedback> {
    await this.wait();
    const activity = activities.find((item) => item.id === activityId);
    if (!activity || !Number.isInteger(answer) || answer < 0 || answer >= activity.options.length)
      throw new Error("Escolha uma alternativa válida.");
    const state = this.read();
    const correct = answer === activity.correctAnswer;
    const firstCorrect = correct && !state.completedActivityIds.includes(activityId);
    state.attempts.push({ activityId, answer, correct, createdAt: new Date().toISOString() });
    if (firstCorrect) state.completedActivityIds.push(activityId);
    const newAchievements: string[] = [];
    if (firstCorrect && !state.achievementIds.includes("FIRST_ACTIVITY"))
      newAchievements.push("FIRST_ACTIVITY");
    if (
      firstCorrect &&
      this.hasCompletedAnyTrack(state) &&
      !state.achievementIds.includes("FIRST_TRACK")
    )
      newAchievements.push("FIRST_TRACK");
    state.achievementIds.push(...newAchievements);
    this.write(state);
    return {
      correct,
      explanation: activity.explanation,
      awardedPoints: firstCorrect ? activity.points : 0,
      totalPoints: this.points(state),
      newAchievements,
    };
  }

  async getProgress(): Promise<Progress> {
    await this.wait();
    const state = this.read();
    const points = this.points(state);
    return {
      points,
      level: 1 + Math.floor(points / 100),
      completedActivities: state.completedActivityIds.length,
      totalActivities: activities.length,
      tracks: this.summarize(state),
    };
  }

  async getAchievements() {
    await this.wait();
    const earned = this.read().achievementIds;
    return [
      {
        id: "FIRST_ACTIVITY",
        title: "Primeiro passo",
        description: "Acerte sua primeira atividade.",
        earned: earned.includes("FIRST_ACTIVITY"),
      },
      {
        id: "FIRST_TRACK",
        title: "Caminho completo",
        description: "Conclua sua primeira trilha.",
        earned: earned.includes("FIRST_TRACK"),
      },
    ];
  }

  async reset() {
    await this.wait();
    this.write(structuredClone(INITIAL_STATE));
  }

  private points(state: DemoState) {
    return state.completedActivityIds.reduce(
      (sum, id) => sum + (activities.find((item) => item.id === id)?.points ?? 0),
      0,
    );
  }

  private publicActivity({
    id,
    moduleId,
    title,
    question,
    options,
    points,
  }: (typeof activities)[number]): Activity {
    return { id, moduleId, title, question, options, points };
  }

  private hasCompletedAnyTrack(state: DemoState) {
    return tracks.some((track) =>
      track.moduleIds.every((moduleId) =>
        modules
          .find((item) => item.id === moduleId)
          ?.activityIds.every((id) => state.completedActivityIds.includes(id)),
      ),
    );
  }

  private summarize(state: DemoState): TrackSummary[] {
    return tracks.map((track) => {
      const trackModules = modules.filter((item) => item.trackId === track.id);
      const completedModules = trackModules.filter((module) =>
        module.activityIds.every((id) => state.completedActivityIds.includes(id)),
      ).length;
      return {
        ...track,
        completedModules,
        totalModules: trackModules.length,
        percentage: trackModules.length
          ? Math.floor((completedModules / trackModules.length) * 100)
          : 0,
      };
    });
  }
}

export const learningService = new MockLearningService();

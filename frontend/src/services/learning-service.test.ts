import { beforeEach, describe, expect, it } from "vitest";
import { MockLearningService } from "./learning-service";

describe("MockLearningService", () => {
  let service: MockLearningService;

  beforeEach(() => {
    localStorage.clear();
    service = new MockLearningService(0);
  });

  it("keeps answer keys out of the presentation contract", async () => {
    const result = await service.getActivity("math-linear-equation");
    expect(result.activity).not.toHaveProperty("correctAnswer");
    expect(result.activity).not.toHaveProperty("explanation");
  });

  it("records mistakes and awards points only on the first correct answer", async () => {
    const wrong = await service.submitAttempt("math-linear-equation", 0);
    const correct = await service.submitAttempt("math-linear-equation", 2);
    const repeated = await service.submitAttempt("math-linear-equation", 2);

    expect(wrong).toMatchObject({ correct: false, awardedPoints: 0, totalPoints: 10 });
    expect(correct).toMatchObject({ correct: true, awardedPoints: 10, totalPoints: 20 });
    expect(repeated).toMatchObject({ correct: true, awardedPoints: 0, totalPoints: 20 });
  });

  it("completes a track, calculates progress and awards the track achievement once", async () => {
    await service.submitAttempt("math-linear-equation", 2);
    const result = await service.submitAttempt("math-percentage", 2);
    const progress = await service.getProgress();
    const math = progress.tracks.find((track) => track.id === "math");

    expect(result.newAchievements).toEqual(["FIRST_TRACK"]);
    expect(math).toMatchObject({ completedModules: 1, totalModules: 1, percentage: 100 });
    expect(progress).toMatchObject({ points: 30, level: 1, completedActivities: 3 });
  });

  it("restores the initial partial progress", async () => {
    await service.submitAttempt("biology-photosynthesis", 3);
    await service.reset();
    const progress = await service.getProgress();

    expect(progress).toMatchObject({ points: 10, completedActivities: 1 });
    expect(progress.tracks[0]?.percentage).toBe(0);
  });
});

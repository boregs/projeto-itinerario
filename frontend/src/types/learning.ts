export type Activity = {
  id: string;
  moduleId: string;
  title: string;
  question: string;
  options: string[];
  points: number;
};

export type ActivityFixture = Activity & {
  correctAnswer: number;
  explanation: string;
};

export type LearningModule = {
  id: string;
  trackId: string;
  title: string;
  eyebrow: string;
  content: string;
  activityIds: string[];
};

export type LearningTrack = {
  id: string;
  title: string;
  subject: string;
  description: string;
  accent: string;
  moduleIds: string[];
};

export type TrackSummary = LearningTrack & {
  completedModules: number;
  totalModules: number;
  percentage: number;
};

export type AttemptFeedback = {
  correct: boolean;
  explanation: string;
  awardedPoints: number;
  totalPoints: number;
  newAchievements: string[];
};

export type DemoState = {
  version: 1;
  attempts: Array<{ activityId: string; answer: number; correct: boolean; createdAt: string }>;
  completedActivityIds: string[];
  achievementIds: string[];
};

export type Progress = {
  points: number;
  level: number;
  completedActivities: number;
  totalActivities: number;
  tracks: TrackSummary[];
};

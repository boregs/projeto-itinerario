export type Role = "STUDENT" | "MENTOR" | "ADMIN";
export type UserStatus = "ACTIVE" | "BLOCKED";
export type ApprovalStatus = "PENDING_APPROVAL" | "APPROVED" | "REJECTED";
export type RequestStatus = "OPEN" | "MATCHED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED";
export type ReportStatus = "OPEN" | "UNDER_REVIEW" | "RESOLVED" | "DISMISSED";

export type DemoIdentity = {
  id: string;
  role: Role;
  name: string;
  subtitle: string;
  initials: string;
};

export type DemoUser = DemoIdentity & { email: string; status: UserStatus };

export type MentorProfile = {
  userId: string;
  bio: string;
  expertiseAreas: string;
  availability: string;
  experience: string;
  approvalStatus: ApprovalStatus;
  completedMentorships: number;
};

export type MentorshipRequest = {
  id: string;
  studentId: string;
  studentName: string;
  subject: string;
  area: string;
  description: string;
  objective: string;
  preferredSchedule: string;
  status: RequestStatus;
  createdAt: string;
};

export type Mentorship = {
  id: string;
  requestId: string;
  studentId: string;
  studentName: string;
  mentorId: string;
  mentorName: string;
  subject: string;
  area: string;
  scheduledAt: string;
  status: Exclude<RequestStatus, "OPEN">;
  summary?: string;
};

export type Report = {
  id: string;
  reporterName: string;
  target: string;
  reason: string;
  description: string;
  status: ReportStatus;
  createdAt: string;
};

export type PlatformState = {
  version: 1;
  sessionId: string | null;
  users: DemoUser[];
  mentors: MentorProfile[];
  requests: MentorshipRequest[];
  mentorships: Mentorship[];
  reports: Report[];
};

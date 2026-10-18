import { identities, initialPlatformState } from "@/mocks/platform-fixtures";
import type {
  ApprovalStatus,
  Mentorship,
  MentorshipRequest,
  PlatformState,
  ReportStatus,
  RequestStatus,
  Role,
  UserStatus,
} from "@/types/platform";

const STORAGE_KEY = "itinerario-demo-platform-v1";

export class MockPlatformService {
  constructor(private readonly latency = 160) {}

  private async wait() {
    await new Promise((resolve) => setTimeout(resolve, this.latency));
  }
  private read(): PlatformState {
    if (typeof window === "undefined") return structuredClone(initialPlatformState);
    try {
      const value = JSON.parse(localStorage.getItem(STORAGE_KEY) ?? "null") as PlatformState | null;
      if (
        value?.version !== 1 ||
        !Array.isArray(value.users) ||
        !Array.isArray(value.requests) ||
        !Array.isArray(value.mentorships)
      )
        throw new Error("invalid state");
      return value;
    } catch {
      this.write(initialPlatformState);
      return structuredClone(initialPlatformState);
    }
  }
  private write(state: PlatformState) {
    if (typeof window !== "undefined") localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  }
  private identity(state: PlatformState) {
    return identities.find((item) => item.id === state.sessionId) ?? null;
  }

  async getSession() {
    await this.wait();
    const state = this.read();
    return this.identity(state);
  }
  async login(identityId: string) {
    await this.wait();
    const state = this.read();
    if (!identities.some((item) => item.id === identityId))
      throw new Error("Perfil demonstrativo inválido.");
    state.sessionId = identityId;
    this.write(state);
    return this.identity(state);
  }
  async logout() {
    await this.wait();
    const state = this.read();
    state.sessionId = null;
    this.write(state);
  }
  async reset() {
    await this.wait();
    this.write(structuredClone(initialPlatformState));
  }

  async getDashboard(role: Role) {
    await this.wait();
    const state = this.read();
    if (role === "MENTOR") {
      const mentorId = this.currentMentorId(state);
      return {
        pendingRequests: state.requests.filter((item) => item.status === "OPEN").length,
        activeMentorships: state.mentorships.filter(
          (item) => item.mentorId === mentorId && !["COMPLETED", "CANCELLED"].includes(item.status),
        ).length,
        completedMentorships: state.mentorships.filter(
          (item) => item.mentorId === mentorId && item.status === "COMPLETED",
        ).length,
        pendingMentors: 0,
      };
    }
    return {
      pendingRequests: state.requests.filter((item) => item.status === "OPEN").length,
      activeMentorships: state.mentorships.filter(
        (item) => !["COMPLETED", "CANCELLED"].includes(item.status),
      ).length,
      completedMentorships: state.mentorships.filter((item) => item.status === "COMPLETED").length,
      pendingMentors: state.mentors.filter((item) => item.approvalStatus === "PENDING_APPROVAL")
        .length,
    };
  }

  async listStudentRequests() {
    await this.wait();
    return this.read().requests.filter((item) => item.studentId === "student-luana");
  }
  async createRequest(
    input: Pick<
      MentorshipRequest,
      "subject" | "area" | "description" | "objective" | "preferredSchedule"
    >,
  ) {
    await this.wait();
    const state = this.read();
    const request: MentorshipRequest = {
      id: `request-${Date.now()}`,
      studentId: "student-luana",
      studentName: "Luana Silva",
      ...input,
      status: "OPEN",
      createdAt: new Date().toISOString(),
    };
    state.requests.unshift(request);
    this.write(state);
    return request;
  }
  async cancelRequest(id: string) {
    await this.wait();
    const state = this.read();
    const request = state.requests.find(
      (item) => item.id === id && item.studentId === "student-luana",
    );
    if (!request || request.status !== "OPEN")
      throw new Error("Esta solicitação não pode mais ser cancelada.");
    request.status = "CANCELLED";
    this.write(state);
  }
  async listAvailableRequests() {
    await this.wait();
    const state = this.read();
    const mentorId = this.currentMentorId(state);
    const mentor = state.mentors.find((item) => item.userId === mentorId);
    if (mentor?.approvalStatus !== "APPROVED") return [];
    return state.requests.filter((item) => item.status === "OPEN");
  }

  async listMentorships(role: Role) {
    await this.wait();
    const state = this.read();
    if (role === "STUDENT")
      return state.mentorships.filter((item) => item.studentId === "student-luana");
    if (role === "MENTOR")
      return state.mentorships.filter((item) => item.mentorId === this.currentMentorId(state));
    return state.mentorships;
  }
  async getMentorship(id: string) {
    await this.wait();
    const state = this.read();
    const item = state.mentorships.find((mentorship) => mentorship.id === id);
    if (!item) throw new Error("Mentoria não encontrada.");
    const identity = this.identity(state);
    if (identity?.role === "MENTOR" && item.mentorId !== identity.id)
      throw new Error("Mentoria não encontrada.");
    if (identity?.role === "STUDENT" && item.studentId !== identity.id)
      throw new Error("Mentoria não encontrada.");
    return item;
  }
  async acceptRequest(requestId: string, scheduledAt: string) {
    await this.wait();
    const state = this.read();
    const request = state.requests.find((item) => item.id === requestId);
    const mentorId = this.currentMentorId(state);
    const mentor = state.mentors.find((item) => item.userId === mentorId);
    const mentorUser = state.users.find((item) => item.id === mentorId);
    if (!request || request.status !== "OPEN") throw new Error("Solicitação indisponível.");
    if (mentor?.approvalStatus !== "APPROVED")
      throw new Error("Somente mentores aprovados podem aceitar solicitações.");
    request.status = "MATCHED";
    const mentorship: Mentorship = {
      id: `mentorship-${Date.now()}`,
      requestId,
      studentId: request.studentId,
      studentName: request.studentName,
      mentorId,
      mentorName: mentorUser?.name ?? "Mentor",
      subject: request.subject,
      area: request.area,
      scheduledAt,
      status: "MATCHED",
    };
    state.mentorships.unshift(mentorship);
    this.write(state);
    return mentorship;
  }
  async updateMentorship(
    id: string,
    status: Exclude<RequestStatus, "OPEN" | "MATCHED">,
    summary?: string,
  ) {
    await this.wait();
    const state = this.read();
    const item = state.mentorships.find((mentorship) => mentorship.id === id);
    if (!item) throw new Error("Mentoria não encontrada.");
    const identity = this.identity(state);
    if (identity?.role === "MENTOR") {
      const mentor = state.mentors.find((profile) => profile.userId === identity.id);
      if (item.mentorId !== identity.id) throw new Error("Mentoria não encontrada.");
      if (mentor?.approvalStatus !== "APPROVED")
        throw new Error("Seu perfil precisa estar aprovado para alterar mentorias.");
    }
    const allowed: Record<Mentorship["status"], Mentorship["status"][]> = {
      MATCHED: ["IN_PROGRESS", "CANCELLED"],
      IN_PROGRESS: ["COMPLETED", "CANCELLED"],
      COMPLETED: [],
      CANCELLED: [],
    };
    if (!allowed[item.status].includes(status)) throw new Error("Transição de status inválida.");
    if (status === "COMPLETED" && !summary?.trim())
      throw new Error("Informe um resumo para concluir.");
    item.status = status;
    if (summary) item.summary = summary;
    const request = state.requests.find((request) => request.id === item.requestId);
    if (request) request.status = status;
    this.write(state);
    return item;
  }

  async getStudentProfile() {
    await this.wait();
    return {
      name: "Luana Silva",
      schoolYear: "2º ano",
      interests: "Matemática, tecnologia e ciências",
    };
  }
  async getMentorProfile(userId?: string) {
    await this.wait();
    const state = this.read();
    const resolvedUserId = userId ?? this.currentMentorId(state);
    const profile = state.mentors.find((item) => item.userId === resolvedUserId);
    const user = state.users.find((item) => item.id === resolvedUserId);
    if (!profile || !user) throw new Error("Mentor não encontrado.");
    return { ...profile, name: user.name, email: user.email };
  }
  async updateMentorProfile(input: {
    bio: string;
    expertiseAreas: string;
    availability: string;
    experience: string;
  }) {
    await this.wait();
    const state = this.read();
    const profile = state.mentors.find((item) => item.userId === this.currentMentorId(state));
    if (!profile) throw new Error("Perfil não encontrado.");
    Object.assign(profile, input, { approvalStatus: "PENDING_APPROVAL" satisfies ApprovalStatus });
    this.write(state);
    return profile;
  }

  private currentMentorId(state: PlatformState) {
    const identity = this.identity(state);
    if (identity?.role !== "MENTOR") throw new Error("Acesso restrito a mentores.");
    return identity.id;
  }

  async listMentors() {
    await this.wait();
    const state = this.read();
    return state.mentors.map((profile) => ({
      ...profile,
      user: state.users.find((item) => item.id === profile.userId)!,
    }));
  }
  async reviewMentor(
    userId: string,
    approvalStatus: Extract<ApprovalStatus, "APPROVED" | "REJECTED">,
  ) {
    await this.wait();
    const state = this.read();
    const profile = state.mentors.find((item) => item.userId === userId);
    if (!profile) throw new Error("Mentor não encontrado.");
    profile.approvalStatus = approvalStatus;
    this.write(state);
  }
  async listUsers() {
    await this.wait();
    return this.read().users;
  }
  async updateUserStatus(id: string, status: UserStatus) {
    await this.wait();
    const state = this.read();
    const user = state.users.find((item) => item.id === id);
    if (!user || user.role === "ADMIN") throw new Error("Este usuário não pode ser alterado.");
    user.status = status;
    this.write(state);
  }
  async listReports() {
    await this.wait();
    return this.read().reports;
  }
  async updateReport(id: string, status: ReportStatus) {
    await this.wait();
    const state = this.read();
    const report = state.reports.find((item) => item.id === id);
    if (!report) throw new Error("Denúncia não encontrada.");
    report.status = status;
    this.write(state);
  }
}

export const platformService = new MockPlatformService();

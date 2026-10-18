import { beforeEach, describe, expect, it } from "vitest";
import { MockPlatformService } from "./platform-service";

describe("MockPlatformService", () => {
  let service: MockPlatformService;

  beforeEach(async () => {
    localStorage.clear();
    service = new MockPlatformService(0);
    await service.login("mentor-rafael");
  });

  it("shares a request across student and mentor views", async () => {
    const request = await service.createRequest({
      subject: "Revisar porcentagem",
      area: "Matemática",
      description: "Tenho dificuldade com descontos em problemas.",
      objective: "Calcular descontos corretamente.",
      preferredSchedule: "Tarde",
    });
    const available = await service.listAvailableRequests();
    expect(available).toContainEqual(expect.objectContaining({ id: request.id, status: "OPEN" }));
  });

  it("accepts and completes a request using valid transitions", async () => {
    const mentorship = await service.acceptRequest(
      "request-conditionals",
      "2030-01-15T17:00:00.000Z",
    );
    await service.updateMentorship(mentorship.id, "IN_PROGRESS");
    const completed = await service.updateMentorship(
      mentorship.id,
      "COMPLETED",
      "Praticamos duas condições.",
    );
    expect(completed).toMatchObject({ status: "COMPLETED", summary: "Praticamos duas condições." });
    await expect(service.updateMentorship(mentorship.id, "IN_PROGRESS")).rejects.toThrow(
      "Transição de status inválida",
    );
  });

  it("requires new approval after a mentor edits the profile", async () => {
    await service.updateMentorProfile({
      bio: "Nova apresentação",
      expertiseAreas: "Matemática",
      availability: "Tarde",
      experience: "Monitoria",
    });
    const profile = await service.getMentorProfile();
    expect(profile.approvalStatus).toBe("PENDING_APPROVAL");
  });

  it("keeps pending mentor data and permissions separate from the approved mentor", async () => {
    await service.login("mentor-carolina");
    const profile = await service.getMentorProfile();

    expect(profile).toMatchObject({ name: "Carolina Alves", approvalStatus: "PENDING_APPROVAL" });
    expect(await service.listAvailableRequests()).toEqual([]);
    await expect(
      service.acceptRequest("request-conditionals", "2030-01-15T17:00:00.000Z"),
    ).rejects.toThrow("Somente mentores aprovados");

    await service.updateMentorProfile({
      bio: "Perfil da Carolina atualizado",
      expertiseAreas: "Química",
      availability: "Manhã",
      experience: "Professora",
    });
    await service.login("mentor-rafael");
    expect((await service.getMentorProfile()).bio).not.toBe("Perfil da Carolina atualizado");
  });
});

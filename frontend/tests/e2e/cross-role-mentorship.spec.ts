import { expect, test } from "@playwright/test";

const appPath = (path: string) => `${process.env.NEXT_PUBLIC_BASE_PATH ?? ""}${path}`;

test("request created by student can be accepted by mentor", async ({ page }, testInfo) => {
  test.skip(testInfo.project.name !== "desktop", "Cross-role flow runs once on desktop");
  await page.goto(appPath("/login"));
  await page.evaluate(() => localStorage.clear());
  await page.reload();
  await page.getByRole("button", { name: /Luana Silva/ }).click();
  await expect(page).toHaveURL(/\/aluno\/dashboard/);
  await page.goto(appPath("/aluno/mentorias/nova"));
  await page.getByLabel("Tema").fill("Preparar prova de porcentagem");
  await page.getByLabel("Área").selectOption("Matemática");
  await page
    .getByLabel("Descreva sua dificuldade")
    .fill("Tenho dificuldade para interpretar descontos e acréscimos.");
  await page
    .getByLabel("O que você quer conseguir fazer?")
    .fill("Resolver problemas de porcentagem.");
  await page.getByLabel("Melhor período").selectOption("Tarde");
  await page.getByRole("button", { name: "Enviar solicitação" }).click();
  await expect(page.getByText("Preparar prova de porcentagem")).toBeVisible();

  await page.getByRole("button", { name: "Sair da demonstração" }).click();
  await expect(page).toHaveURL(/\/login/);
  await page.getByRole("button", { name: /Rafael Nunes/ }).click();
  await expect(page).toHaveURL(/\/mentor\/dashboard/);
  await page.goto(appPath("/mentor/solicitacoes"));
  const request = page.getByRole("article").filter({ hasText: "Preparar prova de porcentagem" });
  await request.getByLabel("Data e hora do encontro").fill("2030-01-15T17:00");
  await request.getByRole("button", { name: "Aceitar e agendar" }).click();
  await expect(request).toBeHidden();

  await page.goto(appPath("/mentor/mentorias"));
  await expect(page.getByText("Preparar prova de porcentagem")).toBeVisible();

  await page.getByRole("button", { name: "Sair da demonstração" }).click();
  await expect(page).toHaveURL(/\/login/);
  await page.getByRole("button", { name: /Marina Costa/ }).click();
  await expect(page).toHaveURL(/\/admin\/dashboard/);
  await page.goto(appPath("/admin/mentores"));
  const pendingMentor = page.getByRole("article").filter({ hasText: "Carolina Alves" });
  await pendingMentor.getByRole("button", { name: "Aprovar" }).click();
  await expect(pendingMentor.getByText("Aprovado")).toBeVisible();
});

test("pending mentor sees her own profile", async ({ page }, testInfo) => {
  test.skip(testInfo.project.name !== "desktop", "Identity regression runs once on desktop");
  await page.goto(appPath("/login"));
  await page.getByRole("button", { name: /Carolina Alves/ }).click();
  await expect(page).toHaveURL(/\/mentor\/dashboard/);
  await expect(page.getByRole("heading", { name: /Olá, Carolina/ })).toBeVisible();
  await page.goto(appPath("/mentor/perfil"));
  await expect(page.getByText("Carolina Alves")).toBeVisible();
  await expect(page.getByText("Aguardando aprovação").first()).toBeVisible();
  await page.goto(appPath("/mentor/solicitacoes"));
  await expect(page.getByText("Nenhuma solicitação disponível")).toBeVisible();
});

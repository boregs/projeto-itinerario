import { expect, test } from "@playwright/test";

test("student learns from a mistake and keeps progress after reload", async ({ page }) => {
  await page.goto("/aluno/atividades/math-linear-equation");
  await page.evaluate(() => localStorage.clear());
  await page.reload();

  await page.getByLabel("4").check();
  await page.getByRole("button", { name: "Conferir resposta" }).click();
  await expect(page.getByText("Ainda não. Vamos entender?")).toBeVisible();

  await page.getByRole("button", { name: "Tentar novamente" }).click();
  await page.getByLabel("6").check();
  await page.getByRole("button", { name: "Conferir resposta" }).click();
  await expect(page.getByText("Mandou bem!")).toBeVisible();
  await expect(page.getByText("+10 pontos")).toBeVisible();

  await page.reload();
  await expect(page.getByText("Você já concluiu esta atividade")).toBeVisible();
  await page.goto("/aluno/progresso");
  await expect(page.getByText("20 pontos acumulados")).toBeVisible();
  await expect(page.locator("body")).toHaveJSProperty(
    "scrollWidth",
    await page.locator("body").evaluate((body) => body.clientWidth),
  );
});

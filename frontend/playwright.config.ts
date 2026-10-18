import { defineConfig, devices } from "@playwright/test";

const staticExport = process.env.E2E_STATIC === "true";

export default defineConfig({
  testDir: "./tests/e2e",
  fullyParallel: true,
  retries: process.env.CI ? 2 : 0,
  reporter: "list",
  workers: 1,
  use: { baseURL: "http://localhost:3000", trace: "on-first-retry" },
  webServer: {
    command: staticExport ? "npm run preview:pages" : "npm run dev",
    url: staticExport ? "http://localhost:3000/projeto-itinerario/" : "http://localhost:3000",
    reuseExistingServer: !process.env.CI,
  },
  projects: [
    {
      name: "small-phone",
      use: { ...devices["Desktop Chrome"], viewport: { width: 320, height: 568 } },
    },
    { name: "android", use: { ...devices["Pixel 7"] } },
    { name: "iphone", use: { ...devices["iPhone 15"] } },
    {
      name: "tablet",
      use: { ...devices["Desktop Chrome"], viewport: { width: 768, height: 1024 } },
    },
    {
      name: "desktop",
      use: { ...devices["Desktop Chrome"], viewport: { width: 1440, height: 900 } },
    },
  ],
});

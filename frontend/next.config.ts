import type { NextConfig } from "next";

const isGitHubPages = process.env.GITHUB_PAGES === "true";

const nextConfig: NextConfig = {
  output: "export",
  images: { unoptimized: true },
  trailingSlash: true,
  basePath: isGitHubPages ? "/projeto-itinerario" : "",
  turbopack: { root: process.cwd() },
};

export default nextConfig;

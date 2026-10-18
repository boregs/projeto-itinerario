import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "export",          // generates static files in frontend/out
  images: { unoptimized: true }, // the default image optimizer needs a server
  trailingSlash: true,       // avoids 404s on refresh with Pages
  basePath: "/your-repo-name", // required for project sites (user.github.io/repo)
};

export default nextConfig;

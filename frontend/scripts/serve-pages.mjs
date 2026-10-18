import { createServer } from "node:http";
import { statSync } from "node:fs";
import { join } from "node:path";
import handler from "serve-handler";

const port = Number(process.env.PORT ?? 3000);
const basePath = "/projeto-itinerario";

const server = createServer((request, response) => {
  const url = request.url ?? "/";
  if (url === basePath) {
    response.writeHead(308, { Location: `${basePath}/` });
    response.end();
    return;
  }
  if (!url.startsWith(`${basePath}/`)) {
    response.writeHead(404, { "Content-Type": "text/plain; charset=utf-8" });
    response.end("Not found");
    return;
  }

  const strippedUrl = url.slice(basePath.length) || "/";
  const parsedUrl = new URL(strippedUrl, "http://localhost");
  const filePath = join("out", decodeURIComponent(parsedUrl.pathname));
  try {
    if (statSync(filePath).isDirectory()) {
      const pathname = parsedUrl.pathname.replace(/\/$/, "");
      request.url = `${pathname}/index.html${parsedUrl.search}`;
    } else {
      request.url = strippedUrl;
    }
  } catch {
    request.url = strippedUrl;
  }
  void handler(request, response, { public: "out", cleanUrls: false, directoryListing: false });
});

server.listen(port, "127.0.0.1", () => {
  console.log(`GitHub Pages preview: http://localhost:${port}${basePath}/`);
});

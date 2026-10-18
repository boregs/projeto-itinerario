import type { Metadata } from "next";
import { Atkinson_Hyperlegible, Manrope } from "next/font/google";
import { Providers } from "./providers";
import "./globals.css";

const bodyFont = Atkinson_Hyperlegible({
  subsets: ["latin"],
  variable: "--font-body",
  weight: ["400", "700"],
});
const displayFont = Manrope({ subsets: ["latin"], variable: "--font-display" });

export const metadata: Metadata = {
  title: "Itinerário | Aprender no seu ritmo",
  description: "Demonstração da plataforma de mentoria e aprendizagem interativa.",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="pt-BR">
      <body className={`${bodyFont.variable} ${displayFont.variable}`}>
        <a className="skip-link" href="#conteudo">
          Pular para o conteúdo
        </a>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}

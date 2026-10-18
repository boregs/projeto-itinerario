import { ArrowRight, BookOpen, Sparkles } from "lucide-react";
import Link from "next/link";

export default function Home() {
  return (
    <main id="conteudo" className="landing">
      <div className="page-shell landing-nav">
        <div className="brand">
          <span>i</span> itinerário
        </div>
        <Link className="button secondary" href="/login">
          Entrar na demo
        </Link>
      </div>
      <section className="page-shell hero">
        <div>
          <p className="eyebrow">Aprender muda caminhos</p>
          <h1 className="display">Seu próximo passo começa aqui.</h1>
          <p className="lead">
            Trilhas curtas, desafios com explicações e apoio acadêmico para avançar no seu ritmo.
          </p>
          <Link className="button" href="/login">
            Explorar demonstração <ArrowRight size={18} aria-hidden="true" />
          </Link>
        </div>
        <div className="hero-visual" aria-hidden="true">
          <div className="orbit orbit-one">
            <BookOpen />
          </div>
          <div className="hero-number">+1</div>
          <p>
            uma descoberta
            <br />
            de cada vez
          </p>
          <div className="orbit orbit-two">
            <Sparkles />
          </div>
        </div>
      </section>
    </main>
  );
}

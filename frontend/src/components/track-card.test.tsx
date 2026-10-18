import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { TrackCard } from "./track-card";

describe("TrackCard", () => {
  it("exposes the progress and a descriptive link", () => {
    render(
      <TrackCard
        track={{
          id: "math",
          title: "Matemática em movimento",
          subject: "Matemática",
          description: "Descrição",
          accent: "blue",
          moduleIds: ["one"],
          completedModules: 0,
          totalModules: 1,
          percentage: 0,
        }}
      />,
    );

    expect(screen.getByLabelText("0% concluído")).toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: "Abrir trilha Matemática em movimento" }),
    ).toHaveAttribute("href", "/aluno/trilhas/math");
  });
});

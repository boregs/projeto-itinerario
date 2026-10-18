export function LoadingState({ label = "Carregando conteúdo" }: { label?: string }) {
  return (
    <div className="loading" role="status">
      <span>{label}…</span>
    </div>
  );
}

export function ErrorState({ message, retry }: { message: string; retry?: () => void }) {
  return (
    <div className="error-box" role="alert">
      <strong>Não foi possível carregar.</strong>
      <p>{message}</p>
      {retry && (
        <button className="button secondary" type="button" onClick={retry}>
          Tentar novamente
        </button>
      )}
    </div>
  );
}

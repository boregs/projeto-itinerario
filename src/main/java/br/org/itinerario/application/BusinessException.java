package br.org.itinerario.application;

public class BusinessException extends RuntimeException {

    public final int status;

    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
    }

    public static BusinessException notFound() {
        return new BusinessException(404, "Registro não encontrado.");
    }

    public static BusinessException forbidden() {
        return new BusinessException(403, "Esta ação não está disponível para sua conta.");
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(409, message);
    }
}

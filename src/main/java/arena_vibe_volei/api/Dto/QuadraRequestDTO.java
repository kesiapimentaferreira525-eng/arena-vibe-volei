package arena_vibe_volei.api.Dto;


import java.time.LocalDateTime;

public class QuadraRequestDTO {
    private String clienteResponsavel;
    private LocalDateTime inicioReserva;
    private int duracaoMinutos; // Ex: 60 minutos

    public String getClienteResponsavel() {
        return clienteResponsavel;
    }

    public void setClienteResponsavel(String clienteResponsavel) {
        this.clienteResponsavel = clienteResponsavel;
    }

    public LocalDateTime getInicioReserva() {
        return inicioReserva;
    }

    public void setInicioReserva(LocalDateTime inicioReserva) {
        this.inicioReserva = inicioReserva;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }
}

package arena_vibe_volei.api.Dto;


import arena_vibe_volei.api.Enum.StatusQuadra;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

public class QuadraResponseDTO {
    private Long id;
    private String nome;
    private StatusQuadra status;
    private String clienteResponsavel;
    private LocalDateTime fimPrevistoReserva;
    private long segundosRestantes; // Positivo se dentro do tempo
    private long segundosUltrapassados; // Positivo se estourou o tempo
    private boolean horaExtraAtiva; // True se passou de 10 minutos (>= 600 segundos)
    private BigDecimal valorHora;
    private ReservaResponseDTO reservaAtual;
    private List<ReservaResponseDTO> proximasReservas;
    private List<ReservaResponseDTO> historicoReservas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public StatusQuadra getStatus() {
        return status;
    }

    public void setStatus(StatusQuadra status) {
        this.status = status;
    }

    public String getClienteResponsavel() {
        return clienteResponsavel;
    }

    public void setClienteResponsavel(String clienteResponsavel) {
        this.clienteResponsavel = clienteResponsavel;
    }

    public LocalDateTime getFimPrevistoReserva() {
        return fimPrevistoReserva;
    }

    public void setFimPrevistoReserva(LocalDateTime fimPrevistoReserva) {
        this.fimPrevistoReserva = fimPrevistoReserva;
    }

    public long getSegundosRestantes() {
        return segundosRestantes;
    }

    public void setSegundosRestantes(long segundosRestantes) {
        this.segundosRestantes = segundosRestantes;
    }

    public long getSegundosUltrapassados() {
        return segundosUltrapassados;
    }

    public void setSegundosUltrapassados(long segundosUltrapassados) {
        this.segundosUltrapassados = segundosUltrapassados;
    }

    public boolean isHoraExtraAtiva() {
        return horaExtraAtiva;
    }

    public void setHoraExtraAtiva(boolean horaExtraAtiva) {
        this.horaExtraAtiva = horaExtraAtiva;
    }

    public BigDecimal getValorHora() {
        return valorHora;
    }

    public void setValorHora(BigDecimal valorHora) {
        this.valorHora = valorHora;
    }

    public ReservaResponseDTO getReservaAtual() {
        return reservaAtual;
    }

    public void setReservaAtual(ReservaResponseDTO reservaAtual) {
        this.reservaAtual = reservaAtual;
    }

    public List<ReservaResponseDTO> getProximasReservas() {
        return proximasReservas;
    }

    public void setProximasReservas(List<ReservaResponseDTO> proximasReservas) {
        this.proximasReservas = proximasReservas;
    }

    public List<ReservaResponseDTO> getHistoricoReservas() {
        return historicoReservas;
    }

    public void setHistoricoReservas(List<ReservaResponseDTO> historicoReservas) {
        this.historicoReservas = historicoReservas;
    }
}
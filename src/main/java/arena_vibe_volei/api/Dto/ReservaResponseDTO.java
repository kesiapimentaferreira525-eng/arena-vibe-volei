package arena_vibe_volei.api.Dto;

import arena_vibe_volei.api.Enum.StatusPagamento;
import arena_vibe_volei.api.Enum.StatusReserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservaResponseDTO {
    private Long id;
    private Long quadraId;
    private String quadraNome;
    private String clienteResponsavel;
    private LocalDateTime inicioReserva;
    private LocalDateTime fimPrevistoReserva;
    private StatusReserva status;
    private StatusPagamento statusPagamento;
    private BigDecimal valorBase;
    private long minutosExcedentes;
    private BigDecimal taxaHoraExtra;
    private BigDecimal valorTotal;
    private LocalDateTime canceladaEm;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuadraId() { return quadraId; }
    public void setQuadraId(Long quadraId) { this.quadraId = quadraId; }
    public String getQuadraNome() { return quadraNome; }
    public void setQuadraNome(String quadraNome) { this.quadraNome = quadraNome; }
    public String getClienteResponsavel() { return clienteResponsavel; }
    public void setClienteResponsavel(String clienteResponsavel) { this.clienteResponsavel = clienteResponsavel; }
    public LocalDateTime getInicioReserva() { return inicioReserva; }
    public void setInicioReserva(LocalDateTime inicioReserva) { this.inicioReserva = inicioReserva; }
    public LocalDateTime getFimPrevistoReserva() { return fimPrevistoReserva; }
    public void setFimPrevistoReserva(LocalDateTime fimPrevistoReserva) { this.fimPrevistoReserva = fimPrevistoReserva; }
    public StatusReserva getStatus() { return status; }
    public void setStatus(StatusReserva status) { this.status = status; }
    public StatusPagamento getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(StatusPagamento statusPagamento) { this.statusPagamento = statusPagamento; }
    public BigDecimal getValorBase() { return valorBase; }
    public void setValorBase(BigDecimal valorBase) { this.valorBase = valorBase; }
    public long getMinutosExcedentes() { return minutosExcedentes; }
    public void setMinutosExcedentes(long minutosExcedentes) { this.minutosExcedentes = minutosExcedentes; }
    public BigDecimal getTaxaHoraExtra() { return taxaHoraExtra; }
    public void setTaxaHoraExtra(BigDecimal taxaHoraExtra) { this.taxaHoraExtra = taxaHoraExtra; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public LocalDateTime getCanceladaEm() { return canceladaEm; }
    public void setCanceladaEm(LocalDateTime canceladaEm) { this.canceladaEm = canceladaEm; }
}

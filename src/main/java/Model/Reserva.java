package Model;

import arena_vibe_volei.api.Enum.StatusPagamento;
import arena_vibe_volei.api.Enum.StatusReserva;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quadra_id", nullable = false)
    private Quadra quadra;

    @Column(nullable = false, length = 150)
    private String clienteResponsavel;

    @Column(nullable = false)
    private LocalDateTime inicioReserva;

    @Column(nullable = false)
    private LocalDateTime fimPrevistoReserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReserva status = StatusReserva.AGENDADA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorBase = BigDecimal.ZERO;

    @Column(nullable = false)
    private long minutosExcedentes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxaHoraExtra = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    private LocalDateTime canceladaEm;

    public Long getId() {
        return id;
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

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

    public LocalDateTime getFimPrevistoReserva() {
        return fimPrevistoReserva;
    }

    public void setFimPrevistoReserva(LocalDateTime fimPrevistoReserva) {
        this.fimPrevistoReserva = fimPrevistoReserva;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public BigDecimal getValorBase() {
        return valorBase;
    }

    public void setValorBase(BigDecimal valorBase) {
        this.valorBase = valorBase;
    }

    public long getMinutosExcedentes() {
        return minutosExcedentes;
    }

    public void setMinutosExcedentes(long minutosExcedentes) {
        this.minutosExcedentes = minutosExcedentes;
    }

    public BigDecimal getTaxaHoraExtra() {
        return taxaHoraExtra;
    }

    public void setTaxaHoraExtra(BigDecimal taxaHoraExtra) {
        this.taxaHoraExtra = taxaHoraExtra;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getCanceladaEm() {
        return canceladaEm;
    }

    public void setCanceladaEm(LocalDateTime canceladaEm) {
        this.canceladaEm = canceladaEm;
    }
}

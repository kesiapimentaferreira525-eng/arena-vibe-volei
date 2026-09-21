package Model;

import arena_vibe_volei.api.Enum.StatusQuadra;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_quadras")
public class Quadra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorHora = BigDecimal.valueOf(100);

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusQuadra status = StatusQuadra.LIVRE;

    @Column(length = 150)
    private String clienteResponsavel;

    private LocalDateTime inicioReserva;

    private LocalDateTime fimPrevistoReserva;


    public Quadra() {}

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

    public BigDecimal getValorHora() {
        return valorHora;
    }

    public void setValorHora(BigDecimal valorHora) {
        this.valorHora = valorHora;
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
}
package arena_vibe_volei.api.Dto;

import java.math.BigDecimal;

public class QuadraCadastroDTO {
    private String nome;
    private BigDecimal valorHora;

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
}

package arena_vibe_volei.api.Dto;

import java.time.LocalDate;
import java.util.List;

public class DisponibilidadeDTO {
    private LocalDate data;
    private boolean disponivel;
    private List<ReservaResponseDTO> reservas;

    public DisponibilidadeDTO(LocalDate data, boolean disponivel, List<ReservaResponseDTO> reservas) {
        this.data = data;
        this.disponivel = disponivel;
        this.reservas = reservas;
    }

    public LocalDate getData() { return data; }
    public boolean isDisponivel() { return disponivel; }
    public List<ReservaResponseDTO> getReservas() { return reservas; }
}

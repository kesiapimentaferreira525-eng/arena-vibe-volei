package Controller;


import Model.Quadra;
import Service.QuadraService;
import arena_vibe_volei.api.Dto.DisponibilidadeDTO;
import arena_vibe_volei.api.Dto.QuadraRequestDTO;
import arena_vibe_volei.api.Dto.ReservaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/quadras")
public class QuadraController {

    @Autowired
    private QuadraService quadraService;

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<Quadra> iniciarReserva(@PathVariable Long id, @RequestBody QuadraRequestDTO dto) {
        return ResponseEntity.ok(quadraService.iniciarReserva(id, dto));
    }

    @PostMapping("/{id}/liberar")
    public ResponseEntity<Quadra> liberarQuadra(@PathVariable Long id) {
        return ResponseEntity.ok(quadraService.liberarQuadra(id));
    }

    @PostMapping("/{id}/reservas")
    public ResponseEntity<ReservaResponseDTO> agendarReserva(
            @PathVariable Long id, @RequestBody QuadraRequestDTO dto) {
        return ResponseEntity.ok(quadraService.agendarReserva(id, dto));
    }

    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<List<DisponibilidadeDTO>> listarDisponibilidade(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate inicio,
            @RequestParam(required = false) LocalDate fim) {
        LocalDate dataInicio = inicio == null ? LocalDate.now() : inicio;
        LocalDate dataFim = fim == null ? dataInicio.plusDays(30) : fim;
        return ResponseEntity.ok(quadraService.listarDisponibilidade(id, dataInicio, dataFim));
    }
}
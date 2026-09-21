package Controller;

import Service.QuadraService;
import arena_vibe_volei.api.Dto.ReservaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservas")
public class ReservaController {

    @Autowired
    private QuadraService quadraService;

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listar() {
        return ResponseEntity.ok(quadraService.listarReservas());
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(quadraService.cancelarReserva(id));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<ReservaResponseDTO> pagar(@PathVariable Long id) {
        return ResponseEntity.ok(quadraService.registrarPagamento(id));
    }
}

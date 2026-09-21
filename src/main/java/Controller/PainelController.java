package Controller;


import Service.QuadraService;
import arena_vibe_volei.api.Dto.QuadraResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/painel")
public class PainelController {

    @Autowired
    private QuadraService quadraService;

    @GetMapping
    public ResponseEntity<List<QuadraResponseDTO>> obterDadosTelao() {
        return ResponseEntity.ok(quadraService.listarTodasParaPainel());
    }
}